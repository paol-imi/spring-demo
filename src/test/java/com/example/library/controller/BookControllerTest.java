package com.example.library.controller;

import com.example.library.dto.BookDTO;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("Book Controller Tests")
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	private BookDTO testBookDTO;

	@BeforeEach
	void setUp() {
		testBookDTO = new BookDTO(1L, "Test Book", "Test Author", "978–88–8080–123–4", LocalDate.of(2023, 1, 1));
	}

	@Nested
	@DisplayName("GET /api/books")
	class GetAllBooks {

		@Test
		@DisplayName("should return all books")
		void shouldReturnAllBooks() throws Exception {
			Page<BookDTO> bookPage = new PageImpl<>(Collections.singletonList(testBookDTO));
			when(bookService.getBooks(any(), any(Pageable.class))).thenReturn(bookPage);

			mockMvc.perform(get("/api/books"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.content[0].title").value("Test Book"));

			verify(bookService).getBooks(any(), any(Pageable.class));
		}

		@Test
		@DisplayName("should filter books by title and author")
		void shouldFilterBooksByTitleAndAuthor() throws Exception {
			Page<BookDTO> bookPage = new PageImpl<>(Collections.singletonList(testBookDTO));
			when(bookService.getBooks(any(), any(Pageable.class))).thenReturn(bookPage);

			mockMvc.perform(get("/api/books")
							.param("title", "Test")
							.param("author", "Author"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.content[0].title").value("Test Book"));

			verify(bookService).getBooks(any(), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("GET /api/books/{id}")
	class GetBookById {

		@Test
		@DisplayName("should return book when found")
		void shouldReturnBookWhenFound() throws Exception {
			when(bookService.getBookById(1L)).thenReturn(Optional.of(testBookDTO));

			mockMvc.perform(get("/api/books/1"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.title").value("Test Book"));

			verify(bookService).getBookById(1L);
		}

		@Test
		@DisplayName("should return 404 when book not found")
		void shouldReturn404WhenBookNotFound() throws Exception {
			when(bookService.getBookById(1L)).thenReturn(Optional.empty());

			mockMvc.perform(get("/api/books/1"))
					.andExpect(status().isNotFound());

			verify(bookService).getBookById(1L);
		}
	}

	@Nested
	@DisplayName("POST /api/books")
	class CreateBook {

		@Test
		@DisplayName("should create book successfully")
		void shouldCreateBookSuccessfully() throws Exception {
			when(bookService.createBook(any(BookDTO.class))).thenReturn(testBookDTO);

			mockMvc.perform(post("/api/books")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testBookDTO)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.title").value("Test Book"));

			verify(bookService).createBook(any(BookDTO.class));
		}

		@Test
		@DisplayName("should return 409 when book already exists")
		void shouldReturn409WhenBookAlreadyExists() throws Exception {
			when(bookService.createBook(any(BookDTO.class)))
					.thenThrow(new BookService.BookAlreadyExistsException("1234567890"));

			mockMvc.perform(post("/api/books")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testBookDTO)))
					.andExpect(status().isConflict());

			verify(bookService).createBook(any(BookDTO.class));
		}
	}

	@Nested
	@DisplayName("PUT /api/books/{id}")
	class UpdateBook {

		@Test
		@DisplayName("should update book successfully")
		void shouldUpdateBookSuccessfully() throws Exception {
			when(bookService.updateBook(eq(1L), any(BookDTO.class))).thenReturn(testBookDTO);

			mockMvc.perform(put("/api/books/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testBookDTO)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.title").value("Test Book"));

			verify(bookService).updateBook(eq(1L), any(BookDTO.class));
		}

		@Test
		@DisplayName("should return 404 when book not found")
		void shouldReturn404WhenBookNotFound() throws Exception {
			when(bookService.updateBook(eq(1L), any(BookDTO.class)))
					.thenThrow(new BookService.BookNotFoundException(1L));

			mockMvc.perform(put("/api/books/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testBookDTO)))
					.andExpect(status().isNotFound());

			verify(bookService).updateBook(eq(1L), any(BookDTO.class));
		}
	}

	@Nested
	@DisplayName("DELETE /api/books/{id}")
	class DeleteBook {

		@Test
		@DisplayName("should delete book successfully")
		void shouldDeleteBookSuccessfully() throws Exception {
			doNothing().when(bookService).deleteBook(1L);

			mockMvc.perform(delete("/api/books/1"))
					.andExpect(status().isNoContent());

			verify(bookService).deleteBook(1L);
		}

		@Test
		@DisplayName("should return 404 when book not found")
		void shouldReturn404WhenBookNotFound() throws Exception {
			doThrow(new BookService.BookNotFoundException(1L)).when(bookService).deleteBook(1L);

			mockMvc.perform(delete("/api/books/1"))
					.andExpect(status().isNotFound());

			verify(bookService).deleteBook(1L);
		}
	}
}