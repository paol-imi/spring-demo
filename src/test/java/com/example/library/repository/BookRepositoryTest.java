package com.example.library.repository;

import com.example.library.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Book Repository Tests")
class BookRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BookRepository bookRepository;

	private Book testBook;

	@BeforeEach
	void setUp() {
		testBook = new Book(null, "Book", "Author", "1234567890", LocalDate.of(2023, 1, 1));
		testBook = entityManager.persist(testBook);
		entityManager.flush();
	}

	@Nested
	@DisplayName("Find operations")
	class FindOperations {

		@Test
		@DisplayName("Find book by ID")
		void whenFindById_thenReturnBook() {
			Optional<Book> found = bookRepository.findById(testBook.getId());

			assertThat(found).isPresent();
			assertThat(found.get().getTitle()).isEqualTo(testBook.getTitle());
			assertThat(found.get().getAuthor()).isEqualTo(testBook.getAuthor());
			assertThat(found.get().getIsbn()).isEqualTo(testBook.getIsbn());
			assertThat(found.get().getPublicationDate()).isEqualTo(testBook.getPublicationDate());
		}

		@Test
		@DisplayName("Find book by non-existent ID")
		void whenFindByNonExistentId_thenReturnEmpty() {
			Optional<Book> found = bookRepository.findById(999L);

			assertThat(found).isEmpty();
		}

		@Test
		@DisplayName("Find book by ISBN")
		void whenFindByIsbn_thenReturnBook() {
			Optional<Book> found = bookRepository.findByIsbn("1234567890");

			assertThat(found).isPresent();
			assertThat(found.get().getTitle()).isEqualTo(testBook.getTitle());
		}

		@Test
		@DisplayName("Find book by non-existent ISBN")
		void whenFindByNonExistentIsbn_thenReturnEmpty() {
			Optional<Book> found = bookRepository.findByIsbn("9999999999");

			assertThat(found).isEmpty();
		}

		@Test
		@DisplayName("Find all books")
		void whenFindAll_thenReturnAllBooks() {
			Book anotherBook = new Book(null, "Another Book", "Another Author", "0987654321", LocalDate.of(2023, 2, 1));
			entityManager.persist(anotherBook);
			entityManager.flush();

			List<Book> books = bookRepository.findAll();

			assertThat(books).hasSize(2);
			assertThat(books).extracting(Book::getTitle).containsExactlyInAnyOrder("Book", "Another Book");
		}
	}

	@Nested
	@DisplayName("Save operations")
	class SaveOperations {

		@Test
		@DisplayName("Save new book")
		void whenSaveBook_thenBookIsPersisted() {
			Book newBook = new Book(null, "New Book", "New Author", "1111111111", LocalDate.of(2023, 3, 1));
			Book saved = bookRepository.save(newBook);

			assertThat(saved.getId()).isNotNull();
			Book found = entityManager.find(Book.class, saved.getId());
			assertThat(found).isNotNull();
			assertThat(found.getTitle()).isEqualTo("New Book");
			assertThat(found.getAuthor()).isEqualTo("New Author");
			assertThat(found.getIsbn()).isEqualTo("1111111111");
			assertThat(found.getPublicationDate()).isEqualTo(LocalDate.of(2023, 3, 1));
		}

		@Test
		@DisplayName("Update existing book")
		void whenUpdateBook_thenBookIsUpdated() {
			testBook.setTitle("Updated Title");
			testBook.setAuthor("Updated Author");
			Book updated = bookRepository.save(testBook);

			assertThat(updated.getTitle()).isEqualTo("Updated Title");
			assertThat(updated.getAuthor()).isEqualTo("Updated Author");
			Book found = entityManager.find(Book.class, testBook.getId());
			assertThat(found.getTitle()).isEqualTo("Updated Title");
			assertThat(found.getAuthor()).isEqualTo("Updated Author");
		}
	}

	@Nested
	@DisplayName("Delete operations")
	class DeleteOperations {

		@Test
		@DisplayName("Delete book")
		void whenDeleteBook_thenBookIsRemoved() {
			bookRepository.delete(testBook);
			entityManager.flush();

			Book found = entityManager.find(Book.class, testBook.getId());
			assertThat(found).isNull();
		}

		@Test
		@DisplayName("Delete book by ID")
		void whenDeleteBookById_thenBookIsRemoved() {
			bookRepository.deleteById(testBook.getId());
			entityManager.flush();

			Book found = entityManager.find(Book.class, testBook.getId());
			assertThat(found).isNull();
		}
	}

	@Nested
	@DisplayName("Pagination and sorting")
	class PaginationAndSorting {

		@BeforeEach
		void setUpMultipleBooks() {
			for (int i = 0; i < 20; i++) {
				Book book = new Book(null, "Book " + i, "Author " + (20 - i), String.format("%010d", i), LocalDate.now().minusDays(i));
				entityManager.persist(book);
			}
			entityManager.flush();
		}

		@Test
		@DisplayName("Find books with pagination")
		void whenFindAllWithPagination_thenReturnPagedResult() {
			Page<Book> bookPage = bookRepository.findAll(PageRequest.of(0, 10));

			assertThat(bookPage.getContent()).hasSize(10);
			assertThat(bookPage.getTotalElements()).isEqualTo(21); // 20 new books + 1 test book
			assertThat(bookPage.getTotalPages()).isEqualTo(3);
			assertThat(bookPage.getNumber()).isZero();
			assertThat(bookPage.getSize()).isEqualTo(10);
		}

		@Test
		@DisplayName("Find books with pagination and sorting")
		void whenFindAllWithPaginationAndSorting_thenReturnSortedPagedResult() {
			Page<Book> bookPage = bookRepository.findAll(
					PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "author")));

			assertThat(bookPage.getContent()).hasSize(10);
			assertThat(bookPage.getContent().get(0).getAuthor()).isEqualTo("Author 9");
			assertThat(bookPage.getContent().get(1).getAuthor()).isEqualTo("Author 8");
			assertThat(bookPage.getContent().get(9).getAuthor()).isEqualTo("Author 19");
		}
	}
}