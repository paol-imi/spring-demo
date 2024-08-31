package com.example.library.integration;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private Book testBook;

	@BeforeEach
	void setUp() {
		testBook = new Book(null, "Integration Test Book", "Test Author", "1234567890", LocalDate.now());
		testBook = bookRepository.save(testBook);
	}

	@AfterEach
	void tearDown() {
		bookRepository.deleteAll();
	}

	@Test
	void testGetAllBooks() throws Exception {
		mockMvc.perform(get("/api/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].title").value("Integration Test Book"));
	}

	@Test
	void testGetBookById() throws Exception {
		mockMvc.perform(get("/api/books/" + testBook.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Integration Test Book"));
	}

	@Test
	void testCreateBook() throws Exception {
		BookDTO newBook = new BookDTO(1L, "New Book", "New Author", "0987654321", LocalDate.of(2023, 1, 1));

		mockMvc.perform(post("/api/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newBook)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("New Book"));
	}

	@Test
	void testUpdateBook() throws Exception {
		BookDTO updatedBook = new BookDTO(testBook.getId(), "Updated Book", "Updated Author", testBook.getIsbn(), LocalDate.of(2023, 1, 1));

		mockMvc.perform(put("/api/books/" + testBook.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedBook)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated Book"));
	}

	@Test
	void testDeleteBook() throws Exception {
		mockMvc.perform(delete("/api/books/" + testBook.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/books/" + testBook.getId()))
				.andExpect(status().isNotFound());
	}
}