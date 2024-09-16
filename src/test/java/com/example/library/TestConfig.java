package com.example.library;

import com.example.library.domain.Book;
import com.example.library.domain.Loan;
import com.example.library.domain.LoanStatus;
import com.example.library.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestConfig {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static User user() {
        User user = new User();
        user.setId(1L);
        user.setName("Paul");
        user.setEmail("paul2@gmail.com");
        user.setPhone("0000000");
        user.setCreatedAt(LocalDate.of(2024, 9, 14));
        return user;
    }

    public static List<User> users() {
        List<User> users = new ArrayList<>();
        users.add(user());
        return users;
    }

    public static String userJson() throws JsonProcessingException {
        User user = user();
        return MAPPER.writeValueAsString(user);
    }

    public static Book book() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Dom Casmurro");
        book.setAuthor("Machado de Assis");
        book.setIsbn("9788542221091");
        book.setCategory("Romance");
        book.setPublishDate(LocalDate.of(1899, 12, 5));
        return book;
    }

    public static List<Book> books() {
        List<Book> books = new ArrayList<>();
        books.add(book());
        return books;
    }

    public static String bookJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(book());
    }

    public static Loan loan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUser(user());
        loan.setBook(book());
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDate.of(2024, 9, 10));
        return loan;
    }

    public static List<Loan> loans() {
        List<Loan> loans = new ArrayList<>();
        loans.add(loan());
        return loans;
    }

    public static String loanJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(loan());
    }
}
