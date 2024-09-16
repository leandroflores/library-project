package com.example.library.controllers;

import com.example.library.TestConfig;
import com.example.library.domain.Book;
import com.example.library.services.BookService;
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

public class BookControllerTest {

    private MockMvc mvc;

    @Mock
    private BookService service;

    @InjectMocks
    private BookController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetBooks() throws Exception {
        List<Book> books = TestConfig.books();
        Book book = books.get(0);

        when(service.getAllBooks()).thenReturn(books);

        mvc.perform(MockMvcRequestBuilders.get("/books/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].isbn").value(book.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].publishDate").value(book.getPublishDate()));
    }

    @Test
    public void testGetBook() throws Exception {
        Book book = TestConfig.book();

        when(service.getBookById(book.getId())).thenReturn(Optional.of(book));

        mvc.perform(MockMvcRequestBuilders.get("/books/" + book.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("publishDate").value(book.getPublishDate()));
    }

    @Test
    public void testGetNotFoundBook() throws Exception {
        Book book = TestConfig.book();

        when(service.getBookById(book.getId())).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get("/books/" + book.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Book not found"));
    }

    @Test
    public void testCreateBook() throws Exception {
        Book book = TestConfig.book();
        String bookJson = TestConfig.bookJson();

        when(service.createBook(book)).thenReturn(book);

        mvc.perform(MockMvcRequestBuilders.post("/books/")
           .contentType(MediaType.APPLICATION_JSON)
           .content(bookJson))
           .andExpect(MockMvcResultMatchers.status().isCreated())
           .andExpect(MockMvcResultMatchers.content().json(bookJson));
    }

    @Test
    public void testDeleteBook() throws Exception {
        Book book = TestConfig.book();

        when(service.deleteBook(book.getId())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.delete("/books/" + book.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Book deleted successfully"));
    }

    @Test
    public void testDeleteNotFoundBook() throws Exception {
        Book book = TestConfig.book();

        when(service.deleteBook(book.getId())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.delete("/books/" + book.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Book not found"));
    }

    @Test
    public void testUpdateBook() throws Exception {
        Book book = TestConfig.book();
             book.setIsbn("9780197723821");
        String bookJson = new ObjectMapper().writeValueAsString(book);

        when(service.updateBook(book.getId(), book)).thenReturn(Optional.of(book));

        mvc.perform(MockMvcRequestBuilders.put("/books/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()))
                .andExpect(MockMvcResultMatchers.jsonPath("publishDate").value(book.getPublishDate()));
    }

    @Test
    public void testUpdateNotFoundBook() throws Exception {
        Book book = TestConfig.book();
             book.setIsbn("9780197723821");
        String bookJson = new ObjectMapper().writeValueAsString(book);

        when(service.updateBook(book.getId(), book)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.put("/books/" + book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Book not found"));;
    }

}
