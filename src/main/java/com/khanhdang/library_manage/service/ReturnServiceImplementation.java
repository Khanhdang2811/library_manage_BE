package com.khanhdang.library_manage.service;

import com.khanhdang.library_manage.dto.Book;
import com.khanhdang.library_manage.dto.Loan;
import com.khanhdang.library_manage.dto.LoanDetail;
import com.khanhdang.library_manage.dto.User;
import com.khanhdang.library_manage.exception.LoanException;
import com.khanhdang.library_manage.exception.UserException;
import com.khanhdang.library_manage.repository.BookRepository;
import com.khanhdang.library_manage.repository.LoanDetailRepository;
import com.khanhdang.library_manage.repository.LoanRepository;
import com.khanhdang.library_manage.repository.UserRepository;
import com.khanhdang.library_manage.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ReturnServiceImplementation implements ReturnService{
    private LoanRepository loanRepository;
    private LoanDetailRepository loanDetailRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;

    @Autowired
    public ReturnServiceImplementation(LoanRepository loanRepository,
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
    @Override
    public ApiResponse<?> returnLoan(Long idLoan) {
        Loan loan = loanRepository.findById(idLoan).orElseThrow(() -> new LoanException("Loan not found with id: " + idLoan));
        User currentUser = getCurrentUser();
        if (!loan.getUser().getId().equals(currentUser.getId())){
            throw new LoanException("Not authorized to return this loan");
        }
        if (STATUS_RETURNED.equals(loan.getStatus())){
            throw new LoanException("Loan already returned");
        }
        List<LoanDetail> loanDetails = loanDetailRepository.findByLoan(loan);
        List<Book> booksReturn = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime borrowingDay = loan.getBorrowingDate();
        LocalDate borrowingDate = borrowingDay.toLocalDate();
        LocalDate todayDate = today.toLocalDate();
        Period period = Period.between(todayDate,borrowingDate);
        int numberOfDaysLate = period.getDays();
        log.warn(String.valueOf(numberOfDaysLate));
        //Return books on time
        if(numberOfDaysLate < 0){
            for (LoanDetail detail : loanDetails ){
                Book book = detail.getBook();
                book.setQuantity(book.getQuantity()+detail.getQuantity());
                booksReturn.add(book);
            }
            bookRepository.saveAll(booksReturn);
            loan.setStatus(STATUS_RETURNED);
            loan.setActualReturndDate(LocalDateTime.now());
        }
        //Return books late
        if(numberOfDaysLate > 0) {

        }

        return null;
    }
}
