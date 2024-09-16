package com.example.library.controllers;

import com.example.library.domain.Book;
import com.example.library.domain.Loan;
import com.example.library.services.BookService;
import com.example.library.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService service;

    @GetMapping("/")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(service.getAllLoans());
    }

    @PostMapping("/")
    public ResponseEntity<Object> createLoan(@RequestBody Loan loan) {
        try {
            if (!service.checkUser(loan)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (!service.checkBook(loan)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }
            Optional<Loan> result = service.createLoan(loan);
            if (result.isPresent()) {
                Loan newLoan = result.get();
                return ResponseEntity.status(HttpStatus.CREATED).body(newLoan);
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Book is not available");
        }catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(exception.getMessage());
        }
    }

    @PatchMapping("/{loan_id}")
    public ResponseEntity<Object> finishLoan(@PathVariable Long loan_id) {
        try {
            Optional<Loan> loan = service.finishLoan(loan_id);
            if (loan.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
            }
            return ResponseEntity.ok("Loan finished successfully");
        }catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(exception.getMessage());
        }
    }

    @DeleteMapping("/{loan_id}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long loan_id) {
        try {
            boolean deletedLoan = service.deleteLoan(loan_id);
            if (!deletedLoan) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found");
            }
            return ResponseEntity.ok("Loan cancelled successfully");
        }catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(exception.getMessage());
        }
    }
}
