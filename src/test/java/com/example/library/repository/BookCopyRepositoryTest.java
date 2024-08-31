// java
package com.example.library.repository;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BookCopy Repository Tests")
class BookCopyRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BookCopyRepository bookCopyRepository;

	private Book testBook;
	private Location testLocation;
	private BookCopy testBookCopy;

	@BeforeEach
	void setUp() {
		testBook = new Book(null, "Test Book", "Test Author", "1234567890", LocalDate.of(2023, 1, 1));
		testBook = entityManager.persist(testBook);

		testLocation = new Location(null, "Test Location", "Test Address");
		testLocation = entityManager.persist(testLocation);

		testBookCopy = new BookCopy(testBook, testLocation, 5);
		testBookCopy = entityManager.persist(testBookCopy);

		entityManager.flush();
	}

	@Nested
	@DisplayName("Find operations")
	class FindOperations {

		@Test
		@DisplayName("Find book copy by ID")
		void whenFindById_thenReturnBookCopy() {
			Optional<BookCopy> found = bookCopyRepository.findById(testBookCopy.getId());

			assertThat(found).isPresent();
			assertThat(found.get().getBook().getTitle()).isEqualTo(testBook.getTitle());
			assertThat(found.get().getLocation().getName()).isEqualTo(testLocation.getName());
			assertThat(found.get().getQuantity()).isEqualTo(5);
		}

		@Test
		@DisplayName("Find book copy by non-existent ID")
		void whenFindByNonExistentId_thenReturnEmpty() {
			BookCopy.BookCopyId nonExistentId = new BookCopy.BookCopyId(999L, 999L);
			Optional<BookCopy> found = bookCopyRepository.findById(nonExistentId);

			assertThat(found).isEmpty();
		}

		@Test
		@DisplayName("Find book quantity by location and book")
		void whenFindBookQuantityByLocationIdAndBookId_thenReturnQuantity() {
			Integer quantity = bookCopyRepository.findBookQuantityByLocationIdAndBookId(
					testLocation.getId(), testBook.getId());

			assertThat(quantity).isEqualTo(5);
		}

		@Test
		@DisplayName("Find books with quantities at location")
		void whenFindBooksWithQuantitiesByLocationId_thenReturnBooks() {
			Page<BookWithQuantityDTO> booksWithQuantities = bookCopyRepository
					.findBooksWithQuantitiesByLocationId(testLocation.getId(), PageRequest.of(0, 10));

			assertThat(booksWithQuantities.getContent()).hasSize(1);
			assertThat(booksWithQuantities.getContent().get(0).getTitle()).isEqualTo("Test Book");
			assertThat(booksWithQuantities.getContent().get(0).getQuantity()).isEqualTo(5);
		}
	}

	@Nested
	@DisplayName("Save operations")
	class SaveOperations {

		@Test
		@DisplayName("Save new book copy")
		void whenSaveBookCopy_thenBookCopyIsPersisted() {
			Book newBook = new Book(null, "New Book", "New Author", "0987654321", LocalDate.of(2023, 2, 1));
			newBook = entityManager.persist(newBook);

			Location newLocation = new Location(null, "New Location", "New Address");
			newLocation = entityManager.persist(newLocation);

			BookCopy newBookCopy = new BookCopy(newBook, newLocation, 3);
			BookCopy saved = bookCopyRepository.save(newBookCopy);

			assertThat(saved.getId()).isNotNull();
			BookCopy found = entityManager.find(BookCopy.class, saved.getId());
			assertThat(found).isNotNull();
			assertThat(found.getBook().getTitle()).isEqualTo("New Book");
			assertThat(found.getLocation().getName()).isEqualTo("New Location");
			assertThat(found.getQuantity()).isEqualTo(3);
		}

		@Test
		@DisplayName("Update existing book copy")
		void whenUpdateBookCopy_thenBookCopyIsUpdated() {
			testBookCopy.setQuantity(10);
			BookCopy updated = bookCopyRepository.save(testBookCopy);

			assertThat(updated.getQuantity()).isEqualTo(10);
			BookCopy found = entityManager.find(BookCopy.class, testBookCopy.getId());
			assertThat(found.getQuantity()).isEqualTo(10);
		}
	}

	@Nested
	@DisplayName("Delete operations")
	class DeleteOperations {

		@Test
		@DisplayName("Delete book copy")
		void whenDeleteBookCopy_thenBookCopyIsRemoved() {
			bookCopyRepository.delete(testBookCopy);
			entityManager.flush();

			BookCopy found = entityManager.find(BookCopy.class, testBookCopy.getId());
			assertThat(found).isNull();
		}

		@Test
		@DisplayName("Delete book copy by ID")
		void whenDeleteBookCopyById_thenBookCopyIsRemoved() {
			bookCopyRepository.deleteById(testBookCopy.getId());
			entityManager.flush();

			BookCopy found = entityManager.find(BookCopy.class, testBookCopy.getId());
			assertThat(found).isNull();
		}
	}

	@Nested
	@DisplayName("Complex scenarios")
	class ComplexScenarios {

		@Test
		@DisplayName("Find books with quantities at location with multiple books")
		void whenFindBooksWithQuantitiesAtLocationWithMultipleBooks_thenReturnAllBooks() {
			Book anotherBook = new Book(null, "Another Book", "Another Author", "1111111111", LocalDate.of(2023, 3, 1));
			anotherBook = entityManager.persist(anotherBook);

			BookCopy anotherBookCopy = new BookCopy(anotherBook, testLocation, 3);
			entityManager.persist(anotherBookCopy);
			entityManager.flush();

			Page<BookWithQuantityDTO> booksWithQuantities = bookCopyRepository
					.findBooksWithQuantitiesByLocationId(testLocation.getId(), PageRequest.of(0, 10));

			assertThat(booksWithQuantities.getContent()).hasSize(2);
			assertThat(booksWithQuantities.getContent())
					.extracting(BookWithQuantityDTO::getTitle)
					.containsExactlyInAnyOrder("Test Book", "Another Book");
			assertThat(booksWithQuantities.getContent())
					.extracting(BookWithQuantityDTO::getQuantity)
					.containsExactlyInAnyOrder(5, 3);
		}

		@Test
		@DisplayName("Update quantity of non-existent book copy")
		void whenUpdateQuantityOfNonExistentBookCopy_thenCreateNewBookCopy() {
			Book newBook = new Book(null, "New Book", "New Author", "2222222222", LocalDate.of(2023, 4, 1));
			newBook = entityManager.persist(newBook);

			BookCopy newBookCopy = new BookCopy(newBook, testLocation, 2);
			BookCopy saved = bookCopyRepository.save(newBookCopy);

			assertThat(saved.getId()).isNotNull();
			assertThat(saved.getQuantity()).isEqualTo(2);
		}
	}
}