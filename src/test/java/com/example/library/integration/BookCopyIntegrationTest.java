package com.example.library.integration;

import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.Location;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookCopyIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private BookCopyRepository bookCopyRepository;

	private Book testBook;
	private Location testLocation;

	@BeforeEach
	void setUp() {
		testBook = new Book(null, "Test Book", "Test Author", "1234567890", LocalDate.now());
		testBook = bookRepository.save(testBook);

		testLocation = new Location(null, "Test Location", "Test Address");
		testLocation = locationRepository.save(testLocation);

		BookCopy testBookCopy = new BookCopy(testBook, testLocation, 5);
		bookCopyRepository.save(testBookCopy);
	}

	@AfterEach
	void tearDown() {
		bookCopyRepository.deleteAll();
		bookRepository.deleteAll();
		locationRepository.deleteAll();
	}

	@Test
	void testGetBookCopies() throws Exception {
		mockMvc.perform(get("/api/locations/" + testLocation.getId() + "/book-copies"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].title").value("Test Book"))
				.andExpect(jsonPath("$.content[0].quantity").value(5));
	}

	@Test
	void testUpdateBookCopyQuantity() throws Exception {
		mockMvc.perform(put("/api/locations/" + testLocation.getId() + "/book-copies/" + testBook.getId())
						.param("quantityChange", "3"))
				.andExpect(status().isOk())
				.andExpect(content().string("8")); // 5 + 3 = 8

		mockMvc.perform(get("/api/locations/" + testLocation.getId() + "/book-copies"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].quantity").value(8));
	}
}