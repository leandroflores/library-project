package com.example.library.controllers;

import com.example.library.TestConfig;
import com.example.library.domain.Book;
import com.example.library.domain.Loan;
import com.example.library.services.BookService;
import com.example.library.services.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class LoanControllerTest {

    private MockMvc mvc;

    @Mock
    private LoanService service;

    @InjectMocks
    private LoanController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetLoans() throws Exception {
        List<Loan> loans = TestConfig.loans();
        Loan loan = loans.get(0);

        when(service.getAllLoans()).thenReturn(loans);

        mvc.perform(MockMvcRequestBuilders.get("/loans/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(loan.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].book").value(loan.getBook()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].user").value(loan.getUser()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value(loan.getStatus().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].loanDate").value(loan.getLoanDate()));
    }

//    @Test
//    public void testGetBook() throws Exception {
//        Book book = TestConfig.book();
//
//        when(service.getBookById(book.getId())).thenReturn(Optional.of(book));
//
//        mvc.perform(MockMvcRequestBuilders.get("/books/" + book.getId()))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
//                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
//                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
//                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()))
//                .andExpect(MockMvcResultMatchers.jsonPath("publishDate").value(book.getPublishDate()));
//    }

//    @Test
//    public void testGetNotFoundBook() throws Exception {
//        Book book = TestConfig.book();
//
//        when(service.getBookById(book.getId())).thenReturn(Optional.empty());
//
//        mvc.perform(MockMvcRequestBuilders.get("/books/" + book.getId()))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andExpect(MockMvcResultMatchers.content().string("Book not found"));
//    }

    @Test
    public void testCreateLoan() throws Exception {
        Loan loan = TestConfig.loan();
        String loanJson = TestConfig.loanJson();

        when(service.checkUser(loan)).thenReturn(true);
        when(service.checkBook(loan)).thenReturn(true);
        when(service.createLoan(loan)).thenReturn(Optional.of(loan));

        mvc.perform(MockMvcRequestBuilders.post("/loans/")
           .contentType(MediaType.APPLICATION_JSON)
           .content(loanJson))
           .andExpect(MockMvcResultMatchers.status().isCreated())
           .andExpect(MockMvcResultMatchers.content().json(loanJson));
    }

    @Test
    public void testCreateLoanWithUserNotFound() throws Exception {
        Loan loan = TestConfig.loan();
        String loanJson = TestConfig.loanJson();

        when(service.checkUser(loan)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }

    @Test
    public void testCreateLoanWithBookNotFound() throws Exception {
        Loan loan = TestConfig.loan();
        String loanJson = TestConfig.loanJson();

        when(service.checkUser(loan)).thenReturn(true);
        when(service.checkBook(loan)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Book not found"));
    }

    @Test
    public void testCreateLoanWithBookNotAvailable() throws Exception {
        Loan loan = TestConfig.loan();
        String loanJson = TestConfig.loanJson();

        when(service.checkUser(loan)).thenReturn(true);
        when(service.checkBook(loan)).thenReturn(true);
        when(service.createLoan(loan)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.post("/loans/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Book is not available"));
    }

    @Test
    public void testFinishLoan() throws Exception {
        Loan loan = TestConfig.loan();

        when(service.finishLoan(loan.getId())).thenReturn(Optional.of(loan));

        mvc.perform(MockMvcRequestBuilders.patch("/loans/" + loan.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Loan finished successfully"));
    }

    @Test
    public void testFinishLoanNotFound() throws Exception {
        Loan loan = TestConfig.loan();

        when(service.finishLoan(loan.getId())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.patch("/loans/" + loan.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Loan not found"));
    }

    @Test
    public void testDeleteLoan() throws Exception {
        Loan loan = TestConfig.loan();

        when(service.deleteLoan(loan.getId())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.delete("/loans/" + loan.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Loan cancelled successfully"));
    }

    @Test
    public void testDeleteLoanNotFound() throws Exception {
        Loan loan = TestConfig.loan();

        when(service.deleteLoan(loan.getId())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.delete("/loans/" + loan.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Loan not found"));
    }

}
