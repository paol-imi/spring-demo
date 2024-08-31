package com.example.library.service;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import com.example.library.specification.BookSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	private ModelMapper modelMapper;
	private BookService bookService;

	private Book testBook;
	private BookDTO testBookDTO;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
		bookService = new BookServiceImpl(bookRepository, modelMapper);

		testBook = new Book(1L, "Test Book", "Test Author", "1234567890", LocalDate.of(2023, 1, 1));
		testBookDTO = modelMapper.map(testBook, BookDTO.class);
	}

	@Nested
	@DisplayName("getBookById")
	class GetBookById {

		@Test
		@DisplayName("should return book when found")
		void shouldReturnBookWhenFound() {
			when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

			Optional<BookDTO> result = bookService.getBookById(1L);

			assertThat(result).isPresent();
			assertThat(result.get()).usingRecursiveComparison().isEqualTo(testBookDTO);
			verify(bookRepository).findById(1L);
		}

		@Test
		@DisplayName("should return empty when book not found")
		void shouldReturnEmptyWhenBookNotFound() {
			when(bookRepository.findById(1L)).thenReturn(Optional.empty());

			Optional<BookDTO> result = bookService.getBookById(1L);

			assertThat(result).isEmpty();
			verify(bookRepository).findById(1L);
		}
	}

	@Nested
	@DisplayName("getBooks")
	class GetBooks {

		@Test
		@DisplayName("should return paged books")
		void shouldReturnPagedBooks() {
			Pageable pageable = PageRequest.of(0, 10);
			BookSpecification spec = mock(BookSpecification.class);
			List<Book> books = Arrays.asList(testBook, new Book(2L, "Another Book", "Another Author", "0987654321", LocalDate.of(2022, 1, 1)));
			Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

			when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(bookPage);

			Page<BookDTO> result = bookService.getBooks(spec, pageable);

			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent().get(0)).usingRecursiveComparison().isEqualTo(testBookDTO);
			verify(bookRepository).findAll(any(Specification.class), eq(pageable));
		}
	}

	@Nested
	@DisplayName("createBook")
	class CreateBook {

		@Test
		@DisplayName("should create book successfully")
		void shouldCreateBookSuccessfully() throws BookService.BookAlreadyExistsException {
			when(bookRepository.findByIsbn(testBookDTO.getIsbn())).thenReturn(Optional.empty());
			when(bookRepository.save(any(Book.class))).thenReturn(testBook);

			BookDTO result = bookService.createBook(testBookDTO);

			testBookDTO.setId(1L);
			assertThat(result).usingRecursiveComparison().isEqualTo(testBookDTO);
			verify(bookRepository).findByIsbn(testBookDTO.getIsbn());
			verify(bookRepository).save(any(Book.class));
		}

		@Test
		@DisplayName("should throw exception when book already exists")
		void shouldThrowExceptionWhenBookAlreadyExists() {
			when(bookRepository.findByIsbn(testBookDTO.getIsbn())).thenReturn(Optional.of(testBook));

			assertThatThrownBy(() -> bookService.createBook(testBookDTO))
					.isInstanceOf(BookService.BookAlreadyExistsException.class)
					.hasMessageContaining("Book already exists with ISBN: " + testBookDTO.getIsbn());

			verify(bookRepository).findByIsbn(testBookDTO.getIsbn());
			verifyNoMoreInteractions(bookRepository);
		}
	}

	@Nested
	@DisplayName("updateBook")
	class UpdateBook {

		@Test
		@DisplayName("should update book successfully")
		void shouldUpdateBookSuccessfully() throws BookService.BookNotFoundException {
			when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
			when(bookRepository.save(any(Book.class))).thenReturn(testBook);

			BookDTO updatedBookDTO = new BookDTO(1L, "Updated Book", "Updated Author", "1234567890", LocalDate.of(2023, 1, 1));
			BookDTO result = bookService.updateBook(1L, updatedBookDTO);

			assertThat(result).usingRecursiveComparison().isEqualTo(updatedBookDTO);
			verify(bookRepository).findById(1L);
			verify(bookRepository).save(any(Book.class));
		}

		@Test
		@DisplayName("should throw exception when book not found")
		void shouldThrowExceptionWhenBookNotFound() {
			when(bookRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> bookService.updateBook(1L, testBookDTO))
					.isInstanceOf(BookService.BookNotFoundException.class)
					.hasMessageContaining("Book not found with id: 1");

			verify(bookRepository).findById(1L);
			verifyNoMoreInteractions(bookRepository);
		}
	}

	@Nested
	@DisplayName("deleteBook")
	class DeleteBook {

		@Test
		@DisplayName("should delete book successfully")
		void shouldDeleteBookSuccessfully() throws BookService.BookNotFoundException {
			when(bookRepository.existsById(1L)).thenReturn(true);

			bookService.deleteBook(1L);

			verify(bookRepository).existsById(1L);
			verify(bookRepository).deleteById(1L);
		}

		@Test
		@DisplayName("should throw exception when book not found")
		void shouldThrowExceptionWhenBookNotFound() {
			when(bookRepository.existsById(1L)).thenReturn(false);

			assertThatThrownBy(() -> bookService.deleteBook(1L))
					.isInstanceOf(BookService.BookNotFoundException.class)
					.hasMessageContaining("Book not found with id: 1");

			verify(bookRepository).existsById(1L);
			verifyNoMoreInteractions(bookRepository);
		}
	}
}