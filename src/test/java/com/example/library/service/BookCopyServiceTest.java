package com.example.library.service;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.Location;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookCopyService Tests")
class BookCopyServiceTest {

	@Mock
	private BookCopyRepository bookCopyRepository;

	@Mock
	private LocationRepository locationRepository;

	@Mock
	private BookRepository bookRepository;

	private BookCopyService bookCopyService;

	private Book testBook;
	private Location testLocation;
	private BookCopy testBookCopy;

	@BeforeEach
	void setUp() {
		bookCopyService = new BookCopyService(bookCopyRepository, locationRepository, bookRepository);

		testBook = new Book(1L, "Test Book", "Test Author", "1234567890", LocalDate.of(2023, 1, 1));
		testLocation = new Location(1L, "Test Location", "Test Address");
		testBookCopy = new BookCopy(testBook, testLocation, 5);
	}

	@Nested
	@DisplayName("updateBookCopyQuantity")
	class UpdateBookCopyQuantity {

		@Test
		@DisplayName("should update quantity when book copy exists")
		void shouldUpdateQuantityWhenBookCopyExists() throws Exception {
			when(bookCopyRepository.findById(any(BookCopy.BookCopyId.class))).thenReturn(Optional.of(testBookCopy));
			when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(testBookCopy);

			Integer result = bookCopyService.updateBookCopyQuantity(1L, 1L, 3);

			assertThat(result).isEqualTo(8);
			verify(bookCopyRepository).findById(any(BookCopy.BookCopyId.class));
			verify(bookCopyRepository).save(any(BookCopy.class));
		}

		@Test
		@DisplayName("should create new book copy when it doesn't exist")
		void shouldCreateNewBookCopyWhenItDoesntExist() throws Exception {
			when(bookCopyRepository.findById(any(BookCopy.BookCopyId.class))).thenReturn(Optional.empty());
			when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
			when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
			when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(new BookCopy(testBook, testLocation, 3));

			Integer result = bookCopyService.updateBookCopyQuantity(1L, 1L, 3);

			assertThat(result).isEqualTo(3);
			verify(bookCopyRepository).findById(any(BookCopy.BookCopyId.class));
			verify(locationRepository).findById(1L);
			verify(bookRepository).findById(1L);
			verify(bookCopyRepository).save(any(BookCopy.class));
		}

		@Test
		@DisplayName("should throw exception when trying to remove more copies than available")
		void shouldThrowExceptionWhenTryingToRemoveMoreCopiesThanAvailable() {
			when(bookCopyRepository.findById(any(BookCopy.BookCopyId.class))).thenReturn(Optional.of(testBookCopy));

			assertThatThrownBy(() -> bookCopyService.updateBookCopyQuantity(1L, 1L, -6))
					.isInstanceOf(BookCopyService.InsufficientCopiesException.class)
					.hasMessageContaining("Insufficient copies of book 1 at location 1. Found 5, requested 6");

			verify(bookCopyRepository).findById(any(BookCopy.BookCopyId.class));
			verifyNoMoreInteractions(bookCopyRepository);
		}
	}

	@Nested
	@DisplayName("getBookCopyQuantity")
	class GetBookCopyQuantity {

		@Test
		@DisplayName("should return quantity when book copy exists")
		void shouldReturnQuantityWhenBookCopyExists() throws Exception {
			when(locationRepository.existsById(1L)).thenReturn(true);
			when(bookRepository.existsById(1L)).thenReturn(true);
			when(bookCopyRepository.getByLocationIdAndBookId(1L, 1L)).thenReturn(new BookCopy(testBook, testLocation, 5));

			Integer result = bookCopyService.getBookCopyQuantity(1L, 1L);

			assertThat(result).isEqualTo(5);
			verify(locationRepository).existsById(1L);
			verify(bookRepository).existsById(1L);
			verify(bookCopyRepository).getByLocationIdAndBookId(1L, 1L);
		}

		@Test
		@DisplayName("should throw exception when location not found")
		void shouldThrowExceptionWhenLocationNotFound() {
			when(locationRepository.existsById(1L)).thenReturn(false);

			assertThatThrownBy(() -> bookCopyService.getBookCopyQuantity(1L, 1L))
					.isInstanceOf(LocationService.LocationNotFoundException.class)
					.hasMessageContaining("Location not found with id: 1");

			verify(locationRepository).existsById(1L);
			verifyNoInteractions(bookRepository, bookCopyRepository);
		}

		@Test
		@DisplayName("should throw exception when book not found")
		void shouldThrowExceptionWhenBookNotFound() {
			when(locationRepository.existsById(1L)).thenReturn(true);
			when(bookRepository.existsById(1L)).thenReturn(false);

			assertThatThrownBy(() -> bookCopyService.getBookCopyQuantity(1L, 1L))
					.isInstanceOf(BookService.BookNotFoundException.class)
					.hasMessageContaining("Book not found with id: 1");

			verify(locationRepository).existsById(1L);
			verify(bookRepository).existsById(1L);
			verifyNoInteractions(bookCopyRepository);
		}
	}

	@Nested
	@DisplayName("getBooksWithQuantitiesAtLocation")
	class GetBooksWithQuantitiesAtLocation {

		@Test
		@DisplayName("should return books with quantities when location exists")
		void shouldReturnBooksWithQuantitiesWhenLocationExists() throws LocationService.LocationNotFoundException {
			Pageable pageable = PageRequest.of(0, 10);
			BookWithQuantityDTO bookWithQuantityDTO = new BookWithQuantityDTO("Test Book", "Test Author", "1234567890", 5);
			Page<BookWithQuantityDTO> expectedPage = new PageImpl<>(List.of(bookWithQuantityDTO), pageable, 1);

			when(locationRepository.existsById(1L)).thenReturn(true);
			when(bookCopyRepository.findBooksWithQuantitiesByLocationId(1L, pageable)).thenReturn(expectedPage);

			Page<BookWithQuantityDTO> result = bookCopyService.getBooksWithQuantitiesAtLocation(1L, pageable);

			assertThat(result).isEqualTo(expectedPage);
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0)).isEqualTo(bookWithQuantityDTO);
			verify(locationRepository).existsById(1L);
			verify(bookCopyRepository).findBooksWithQuantitiesByLocationId(1L, pageable);
		}

		@Test
		@DisplayName("should throw exception when location not found")
		void shouldThrowExceptionWhenLocationNotFound() {
			Pageable pageable = PageRequest.of(0, 10);
			when(locationRepository.existsById(1L)).thenReturn(false);

			assertThatThrownBy(() -> bookCopyService.getBooksWithQuantitiesAtLocation(1L, pageable))
					.isInstanceOf(LocationService.LocationNotFoundException.class)
					.hasMessageContaining("Location not found with id: 1");

			verify(locationRepository).existsById(1L);
			verifyNoInteractions(bookCopyRepository);
		}
	}
}