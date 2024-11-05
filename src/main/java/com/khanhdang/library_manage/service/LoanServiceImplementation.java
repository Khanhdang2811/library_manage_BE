package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.Book;
import com.khanhdang.library_manage.dto.Loan;
import com.khanhdang.library_manage.dto.LoanDetail;
import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.exception.ApiStatus;
import com.khanhdang.library_manage.exception.BookException;
import com.khanhdang.library_manage.exception.LoanException;
import com.khanhdang.library_manage.exception.UserException;
import com.khanhdang.library_manage.mapper.LoanMapper;
import com.khanhdang.library_manage.repository.BookRepository;
import com.khanhdang.library_manage.repository.LoanDetailRepository;
import com.khanhdang.library_manage.repository.LoanRepository;
import com.khanhdang.library_manage.repository.UserRepository;
import com.khanhdang.library_manage.request.loan.CreationLoanRequest;
import com.khanhdang.library_manage.request.loan.LoanDetailRequest;
import com.khanhdang.library_manage.response.ApiResponse;
import com.khanhdang.library_manage.response.LoanDetailResponse;
import com.khanhdang.library_manage.response.LoanResponse;
import com.khanhdang.library_manage.response.LoanResponseDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    public LoanServiceImplementation(LoanRepository loanRepository,
                                     LoanDetailRepository loanDetailRepository,
                                     BookRepository bookRepository,
                                     UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.loanDetailRepository = loanDetailRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    private static final int MAX_BOOKS_PER_LOAN = 5;
    private static final String STATUS_BORROWED = "BORROWED";
    private static final String STATUS_RETURNED = "RETURNED";
    private static final String STATUS_OVERDUE = "OVERDUE";

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



}
