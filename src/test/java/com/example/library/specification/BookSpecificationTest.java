package com.example.library.specification;

import com.example.library.entity.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BookSpecification Tests")
class BookSpecificationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("titleLike should filter books by title")
	void titleLikeShouldFilterBooksByTitle() {
		Book book1 = new Book(null, "Java Programming", "John Doe", "1234567890", LocalDate.now());
		Book book2 = new Book(null, "Python Basics", "Jane Smith", "0987654321", LocalDate.now());
		entityManager.persist(book1);
		entityManager.persist(book2);
		entityManager.flush();

		CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Book> query = cb.createQuery(Book.class);
		Root<Book> root = query.from(Book.class);

		query.where(BookSpecification.titleLike("Java").toPredicate(root, query, cb));

		List<Book> result = entityManager.getEntityManager().createQuery(query).getResultList();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo("Java Programming");
	}

	@Test
	@DisplayName("authorLike should filter books by author")
	void authorLikeShouldFilterBooksByAuthor() {
		Book book1 = new Book(null, "Java Programming", "John Doe", "1234567890", LocalDate.now());
		Book book2 = new Book(null, "Python Basics", "Jane Smith", "0987654321", LocalDate.now());
		entityManager.persist(book1);
		entityManager.persist(book2);
		entityManager.flush();

		CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Book> query = cb.createQuery(Book.class);
		Root<Book> root = query.from(Book.class);

		query.where(BookSpecification.authorLike("Jane").toPredicate(root, query, cb));

		List<Book> result = entityManager.getEntityManager().createQuery(query).getResultList();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getAuthor()).isEqualTo("Jane Smith");
	}
}