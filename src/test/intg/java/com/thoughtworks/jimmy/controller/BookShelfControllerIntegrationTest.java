package com.thoughtworks.jimmy.controller;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.google.gson.Gson;
import com.thoughtworks.jimmy.SpringBootWebApplicationTests;
import com.thoughtworks.jimmy.entity.BookEntity;
import com.thoughtworks.jimmy.entity.CategoryEntity;
import com.thoughtworks.jimmy.repository.BookRepository;
import com.thoughtworks.jimmy.repository.CategoryRepository;
import org.springframework.web.util.NestedServletException;


public class BookShelfControllerIntegrationTest extends SpringBootWebApplicationTests {

    @Autowired
    private BookShelfController bookShelfController;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookShelfController).build();
    }

    @Test
    public void should_search_book_by_isbn_successfully() throws Exception {
        BookEntity savedBook = bookRepository.save(new BookEntity("book-isbn", "Book Name", "Book Author", 32.52));

        String isbn = savedBook.getIsbn();
        mockMvc.perform(get(format("/books/%s", isbn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(isbn))
                .andExpect(jsonPath("$.title").value(savedBook.getTitle()))
                .andExpect(jsonPath("$.author").value(savedBook.getAuthor()))
                .andExpect(jsonPath("$.price").value(savedBook.getPrice()));
    }

    @Test
    public void should_be_able_to_add_book_to_shelf() throws Exception {
        BookEntity bookEntity = new BookEntity("1234567890", "Hello World", "sqlin", 54.2);
        String bookEntityJson = new Gson().toJson(bookEntity);

        mockMvc.perform(post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookEntityJson))
                .andExpect(status().isCreated());

        BookEntity fetchBook = bookRepository.findOne(bookEntity.getIsbn());
        assertEquals(bookEntity.getIsbn(), fetchBook.getIsbn());
        assertEquals(bookEntity.getTitle(), fetchBook.getTitle());
    }

    @Ignore
    @Test
    public void should_add_book_conflict_when_book_already_exists() throws Exception {
        BookEntity existedBook = bookRepository.save(new BookEntity("123456", "Exist Book", "me", 34.3));

        mockMvc.perform(post("/books/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(existedBook)))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_search_book_by_title_fuzzily() throws Exception {
        bookRepository.save(asList(
                new BookEntity("12345", "Head First Java", "you", 55.6),
                new BookEntity("45678", "Basic Java Learning", "she", 32.5),
                new BookEntity("89234", "Other Books Basic", "me", 12.5)));

        String titleFuzzyFilter = "Java";

        mockMvc.perform(get(format("/books/title/%s", titleFuzzyFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Head First Java"))
                .andExpect(jsonPath("$[1].title").value("Basic Java Learning"));
    }

    @Test
    public void should_be_able_to_update_book_to_shelf() throws Exception {
        bookRepository.save(asList(
                new BookEntity("12345", "Head First Java", "you", 55.6),
                new BookEntity("45678", "Basic Java Learning", "she", 32.5),
                new BookEntity("89234", "Other Books Basic", "me", 12.5)));

        BookEntity bookEntity = new BookEntity("12345", "1", "1", 1.0);
        String bookEntityJson = new Gson().toJson(bookEntity);

        mockMvc.perform(put("/books/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookEntityJson));

        BookEntity fetchBook = bookRepository.findOne(bookEntity.getIsbn());
        assertEquals(bookEntity.getIsbn(), fetchBook.getIsbn());
        assertEquals(bookEntity.getTitle(), fetchBook.getTitle());
    }

    @Test
    public void should_not_be_able_to_update_book_to_shelf_when_isbn_is_not_same() throws Exception {
        BookEntity old = new BookEntity("12345", "Head First Java", "you", 55.6);
        bookRepository.save(asList(
                old,
                new BookEntity("45678", "Basic Java Learning", "she", 32.5),
                new BookEntity("89234", "Other Books Basic", "me", 12.5)));

        BookEntity bookEntity = new BookEntity("1", "1", "1", 1.0);
        String bookEntityJson = new Gson().toJson(bookEntity);

        mockMvc.perform(put("/books/12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookEntityJson));

        BookEntity fetchBook = bookRepository.findOne(old.getIsbn());
        assertEquals(old.getIsbn(), fetchBook.getIsbn());
        assertEquals(old.getTitle(), fetchBook.getTitle());
    }

    @Test
    public void should_be_able_to_delete_book_from_shelf() throws Exception {
        BookEntity old = new BookEntity("12345", "Head First Java", "you", 55.6);
        bookRepository.save(asList(
                old,
                new BookEntity("45678", "Basic Java Learning", "she", 32.5),
                new BookEntity("89234", "Other Books Basic", "me", 12.5)));

        mockMvc.perform(delete("/books/12345"));
        assertNull(bookRepository.findOne(old.getIsbn()));
    }

    @Test (expected = NestedServletException.class)
    public void should_now_be_able_to_delete_book_from_shelf_when_give_invalid_isbn() throws Exception {
        BookEntity old = new BookEntity("12345", "Head First Java", "you", 55.6);
        bookRepository.save(asList(
                old,
                new BookEntity("45678", "Basic Java Learning", "she", 32.5),
                new BookEntity("89234", "Other Books Basic", "me", 12.5)));

        mockMvc.perform(delete("/books/123456"));
        assertNotNull(bookRepository.findOne(old.getIsbn()));
    }

    @Test
    public void should_search_book_by_category_name() throws Exception {
        CategoryEntity category = new CategoryEntity("C123456", "Category Name", "Category Description");
        categoryRepository.save(category);
        BookEntity book1 = new BookEntity("12345", "Hello1", "monkey1", 23.5);
        BookEntity book2 = new BookEntity("22345", "Hello2", "monkey2", 23.5);
        BookEntity book3 = new BookEntity("32345", "Hello3", "monkey3", 23.5);
        book1.setCategoryCode(category.getCode());
        book2.setCategoryCode(category.getCode());
        bookRepository.save(asList(book1, book2, book3));

        mockMvc.perform(get(format("/books/category/%s", category.getName())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$.[1].title").value(book2.getTitle()));

    }

    @Test
    public void should_find_category_of_the_book_when_given_book_isbn() {
        

    }
}
