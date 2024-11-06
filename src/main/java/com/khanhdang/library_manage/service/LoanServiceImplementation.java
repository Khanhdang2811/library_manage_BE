package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.*;
import com.khanhdang.library_manage.exception.ApiStatus;
import com.khanhdang.library_manage.exception.BookException;
import com.khanhdang.library_manage.exception.LoanException;
import com.khanhdang.library_manage.exception.UserException;
import com.khanhdang.library_manage.mapper.LoanMapper;
import com.khanhdang.library_manage.mapper.ReturnMapper;
import com.khanhdang.library_manage.repository.*;
import com.khanhdang.library_manage.request.loan.CreationLoanRequest;
import com.khanhdang.library_manage.request.loan.LoanDetailRequest;
import com.khanhdang.library_manage.response.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LoanServiceImplementation implements LoanService {

    private LoanRepository loanRepository;
    private LoanDetailRepository loanDetailRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private ReturnRepository returnRepository;
    private ReturnDetailRepository returnDetailRepository;
    private FineRepository fineRepository;


    @Autowired
    public LoanServiceImplementation(LoanRepository loanRepository,
                                     LoanDetailRepository loanDetailRepository,
                                     BookRepository bookRepository,
                                     UserRepository userRepository,
                                     ReturnRepository returnRepository,
                                     ReturnDetailRepository returnDetailRepository,
                                     FineRepository fineRepository) {
        this.loanRepository = loanRepository;
        this.loanDetailRepository = loanDetailRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.returnRepository = returnRepository;
        this.returnDetailRepository = returnDetailRepository;
        this.fineRepository = fineRepository;

    }

    private static final int MAX_BOOKS_PER_LOAN = 5;
    private static final String STATUS_BORROWED = "BORROWED";
    private static final String STATUS_RETURNED = "RETURNED";
    private static final String STATUS_OVERDUE = "OVERDUE";
    private static final double FINE_PER_BOOK_PER_DAY = 1000.0;

    private User getCurrentUser(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String nameCurrentUser = auth.getName();
        return userRepository.findByUsername(nameCurrentUser).orElseThrow(() -> new UserException("User not found by name: " + auth));
    }
    private boolean hasOverdueLoans(User user) {
        return loanRepository.existsByUserAndStatus(user, STATUS_OVERDUE);
    }


    @Override
    @Transactional
    public ApiResponse<?> createLoanBook(CreationLoanRequest request) {
        if (request.getLoanDetails() == null || request.getLoanDetails().isEmpty()){
            throw new LoanException("Loan details cannot be empty");
        }
        int totalBooks = request.getLoanDetails()
                .stream()
                .mapToInt(LoanDetailRequest::getQuantity)
                .sum();
        if (totalBooks > MAX_BOOKS_PER_LOAN){
            throw new LoanException("Cannot borrow more than " + MAX_BOOKS_PER_LOAN + " books at once");        }
        if (hasOverdueLoans(getCurrentUser())){
            throw new LoanException("Cannot create new loan while having overdue books");
        }
        Loan loan = Loan.builder()
                .user(getCurrentUser())
                .status(STATUS_BORROWED)
                .build();
        loan = loanRepository.save(loan);
        List<LoanDetail> loanDetails = new ArrayList<>();
        List<Book> booksToUpdate = new ArrayList<>();
        for (LoanDetailRequest detail : request.getLoanDetails()){
            Book book = bookRepository.findById(detail.getBookId())
                    .orElseThrow(() -> new BookException("Book not found with id: " + detail.getBookId()));
            if (book.getQuantity() < detail.getQuantity() ){
                throw new BookException("Not enough copies available for book: " + book.getTitle());
            }
            //Cập nhật số lượng sách
            book.setQuantity(book.getQuantity() - detail.getQuantity());
            booksToUpdate.add(book);
            LoanDetail loanDetail  = LoanDetail.builder()
                    .loan(loan)
                    .book(book)
                    .quantity(detail.getQuantity())
                    .build();
            loanDetails.add(loanDetail);
        }
        loanDetailRepository.saveAll(loanDetails);
        bookRepository.saveAll(booksToUpdate);
        return ApiResponse.builder()
                .status(ApiStatus.SUCCESS)
                .data(LoanMapper.INSTANCE.loanToLoanResponse(loan, loanDetails))
                .message("fine book is success")
                .build();
    }

    @Override
    public ApiResponse<List<LoanResponseDTO>> getLoanByUserId() {
        try {
            User currentUser = getCurrentUser();
            log.info("Fetching loans for user: {}", currentUser.getUsername());

            List<Loan> loans = loanRepository.findByUser(currentUser);
            List<LoanResponseDTO> loanResponses = loans.stream()
                    .map(LoanMapper.INSTANCE::LoanToLoanResponseDTO)
                    .toList();

            String message = loans.isEmpty() ?
                    "No loans found for user" :
                    "Successfully retrieved " + loans.size() + " loans";

            return ApiResponse.<List<LoanResponseDTO>>builder()
                    .status(ApiStatus.SUCCESS)
                    .message(message)
                    .data(loanResponses)
                    .build();

        } catch (UserException e) {
            log.error("User not found or not authenticated: {}", e.getMessage());
            return ApiResponse.<List<LoanResponseDTO>>builder()
                    .status(ApiStatus.ERROR)
                    .message("User authentication failed")
                    .data(Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Error while fetching loans: {}", e.getMessage());
            return ApiResponse.<List<LoanResponseDTO>>builder()
                    .status(ApiStatus.ERROR)
                    .message("Error occurred while fetching loans")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    //Check Loan Update at 00:00 PM
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateOverDueLoans(){
        List<Loan> activeLoans = loanRepository.findByStatusIn(Arrays.asList(STATUS_BORROWED));

        LocalDateTime localDateTime = LocalDateTime.now();
        List<Loan> overdueLoans = activeLoans.stream()
                .filter(loan -> loan.getDueDate().isBefore(localDateTime))
                .toList();
        for (Loan loan : overdueLoans){
            loan.setStatus(STATUS_OVERDUE);
        }
        loanRepository.saveAll(overdueLoans);
    }

    @Override
    public ApiResponse<List<LoanDetailResponse>> getLoanDetailByIdLoan(Long idLoan) {
        Loan loan = loanRepository.findById(idLoan)
                .orElseThrow(() -> new LoanException("Loan not found with id: " + idLoan));

        // Verify user permission
        User currentUser = getCurrentUser();
        if (!loan.getUser().getId().equals(currentUser.getId())) {
            throw new LoanException("Not authorized to view this loan");
        }
        List<LoanDetail> details = loanDetailRepository.findByLoan(loan);
        List<LoanDetailResponse> loanDetailResponses = details.stream()
                .map(LoanMapper.INSTANCE::loanDetailToLoanDetailResponse)
                .toList();

        return ApiResponse.<List<LoanDetailResponse>>builder()
                .status(ApiStatus.SUCCESS)
                .message("Loan details retrieved successfully")
                .data(loanDetailResponses)
                .build();
    }
    private long calculateDaysOverdue(Loan loan) {
        LocalDateTime dueDateTime = loan.getDueDate();  // Convert LocalDate to LocalDateTime
        if (LocalDateTime.now().isAfter(dueDateTime.plusDays(15))) {  // 15 days grace period
            return ChronoUnit.DAYS.between(dueDateTime.plusDays(15), LocalDateTime.now());
        }
        return 0;
    }
    @Override
    public ApiResponse<ReturnResponseDTO> returnLoan(Long idLoan) {
        Loan loan = loanRepository.findById(idLoan)
                .orElseThrow(() -> new LoanException("Loan not found with id: " + idLoan) );
        User user = getCurrentUser();
        if(!user.getId().equals(loan.getUser().getId())){
            throw new LoanException("Not authorized to view this loan");
        }
        if(loan.getStatus().equals(STATUS_RETURNED)){
            throw new LoanException("Books already returned for this loan");
        }
        long daysOverdue = calculateDaysOverdue(loan);
        log.warn(String.valueOf(daysOverdue));
        Return returnRecord = Return.builder()
                .loan(loan)
                .description(daysOverdue > 0 ? "LATE PAYMENT" : "PAY ON TIME")
                .build();
        returnRecord = returnRepository.save(returnRecord);
        List<LoanDetail> loanDetails = loanDetailRepository.findByLoan(loan);
        List<ReturnDetail> returnDetails = new ArrayList<>();
        double totalFine = 0.0;
        for(LoanDetail loanDetail : loanDetails){
            double bookFine = daysOverdue * FINE_PER_BOOK_PER_DAY * loanDetail.getQuantity();
            totalFine += bookFine;
            // Create return detail
            ReturnDetail returnDetail = ReturnDetail.builder()
                    .aReturn(returnRecord)
                    .books(loanDetail.getBook())
                    .quantity(Long.valueOf(loanDetail.getQuantity()))
                    .fineAmount(BigDecimal.valueOf(bookFine))
                    .build();
            returnDetails.add(returnDetail);
            // Update book quantity
            Book book = loanDetail.getBook();
            book.setQuantity(book.getQuantity() + loanDetail.getQuantity());
            bookRepository.save(book);
        }
        // Save return details
        returnDetailRepository.saveAll(returnDetails);
        if (totalFine > 0) {
            Fine fine = Fine.builder()
                    .aReturn(returnRecord)
                    .daysOverdue(daysOverdue)
                    .fineAmount(BigDecimal.valueOf(totalFine))
                    .build();
            fineRepository.save(fine);
        }
        // Update return record with total fine
        returnRecord.setTotalFine(BigDecimal.valueOf(totalFine));
        returnRepository.save(returnRecord);
        // Update loan status
        loan.setStatus(STATUS_RETURNED);
        loan.setActualReturndDate(LocalDateTime.now());
        loanRepository.save(loan);
        log.warn(String.valueOf(ReturnMapper.INSTANCE.returnToReturnResponseDTO(returnRecord)));
        return ApiResponse.<ReturnResponseDTO>builder()
                .status(ApiStatus.SUCCESS)
                .message("Books returned successfully" + (totalFine > 0 ? ". Fine amount: " + totalFine : ""))
                .data(ReturnMapper.INSTANCE.returnToReturnResponseDTO(returnRecord))
                .build();
    }
}
