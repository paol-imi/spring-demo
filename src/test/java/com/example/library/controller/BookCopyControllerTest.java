package com.example.library.controller;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.service.BookCopyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookCopyController.class)
@DisplayName("BookCopy Controller Tests")
class BookCopyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookCopyService bookCopyService;

	@Autowired
	private ObjectMapper objectMapper;

	private BookWithQuantityDTO testBookWithQuantityDTO;

	@BeforeEach
	void setUp() {
		testBookWithQuantityDTO = new BookWithQuantityDTO("Test Book", "Test Author", "1234567890", 5);
	}

	@Nested
	@DisplayName("GET /api/locations/{locationId}/book-copies")
	class GetBookCopies {

		@Test
		@DisplayName("should return all book copies at a location")
		void shouldReturnAllBookCopiesAtLocation() throws Exception {
			Page<BookWithQuantityDTO> bookCopyPage = new PageImpl<>(Collections.singletonList(testBookWithQuantityDTO));
			when(bookCopyService.getBooksWithQuantitiesAtLocation(eq(1L), any(Pageable.class))).thenReturn(bookCopyPage);

			mockMvc.perform(get("/api/locations/1/book-copies"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.content[0].title").value("Test Book"))
					.andExpect(jsonPath("$.content[0].quantity").value(5));

			verify(bookCopyService).getBooksWithQuantitiesAtLocation(eq(1L), any(Pageable.class));
		}

		@Test
		@DisplayName("should return 404 when location not found")
		void shouldReturn404WhenLocationNotFound() throws Exception {
			when(bookCopyService.getBooksWithQuantitiesAtLocation(eq(1L), any(Pageable.class)))
					.thenThrow(new LocationService.LocationNotFoundException(1L));

			mockMvc.perform(get("/api/locations/1/book-copies"))
					.andExpect(status().isNotFound());

			verify(bookCopyService).getBooksWithQuantitiesAtLocation(eq(1L), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("PUT /api/locations/{locationId}/book-copies/{bookId}")
	class UpdateBookCopyQuantity {

		@Test
		@DisplayName("should update book copy quantity successfully")
		void shouldUpdateBookCopyQuantitySuccessfully() throws Exception {
			when(bookCopyService.updateBookCopyQuantity(eq(1L), eq(1L), eq(5))).thenReturn(10);

			mockMvc.perform(put("/api/locations/1/book-copies/1")
							.param("quantityChange", "5"))
					.andExpect(status().isOk())
					.andExpect(content().string("10"));

			verify(bookCopyService).updateBookCopyQuantity(eq(1L), eq(1L), eq(5));
		}

		@Test
		@DisplayName("should return 404 when location not found")
		void shouldReturn404WhenLocationNotFound() throws Exception {
			when(bookCopyService.updateBookCopyQuantity(eq(1L), eq(1L), eq(5)))
					.thenThrow(new LocationService.LocationNotFoundException(1L));

			mockMvc.perform(put("/api/locations/1/book-copies/1")
							.param("quantityChange", "5"))
					.andExpect(status().isNotFound());

			verify(bookCopyService).updateBookCopyQuantity(eq(1L), eq(1L), eq(5));
		}

		@Test
		@DisplayName("should return 404 when book not found")
		void shouldReturn404WhenBookNotFound() throws Exception {
			when(bookCopyService.updateBookCopyQuantity(eq(1L), eq(1L), eq(5)))
					.thenThrow(new BookService.BookNotFoundException(1L));

			mockMvc.perform(put("/api/locations/1/book-copies/1")
							.param("quantityChange", "5"))
					.andExpect(status().isNotFound());

			verify(bookCopyService).updateBookCopyQuantity(eq(1L), eq(1L), eq(5));
		}

		@Test
		@DisplayName("should return 409 when insufficient copies")
		void shouldReturn409WhenInsufficientCopies() throws Exception {
			when(bookCopyService.updateBookCopyQuantity(eq(1L), eq(1L), eq(-10)))
					.thenThrow(new BookCopyService.InsufficientCopiesException(1L, 1L, 5, 10));

			mockMvc.perform(put("/api/locations/1/book-copies/1")
							.param("quantityChange", "-10"))
					.andExpect(status().isConflict());

			verify(bookCopyService).updateBookCopyQuantity(eq(1L), eq(1L), eq(-10));
		}
	}
}