package com.example.library.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a book in the library system.
 * This entity is mapped to the 'books' table in the database.
 */
@Entity
@Table(name = "books", indexes = {
		// Index to allow searching for books by title.
		@Index(name = "idx_book_title", columnList = "title"),
		// Index to allow searching for books by author, or by author and title. The composite index is built with
		// the author column first, as it is more selective than the title column.
		@Index(name = "idx_book_author_title", columnList = "author, title")
})
public class Book {
	/**
	 * The locations where the book is available.
	 */
	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<BookCopy> bookCopies = new HashSet<>();
	/**
	 * The unique identifier of the book.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * The title of the book.
	 */
	@Column(nullable = false)
	private String title;
	/**
	 * The author of the book.
	 */
	@Column(nullable = false)
	private String author;
	/**
	 * The ISBN of the book.
	 */
	@Column(nullable = false, unique = true)
	private String isbn;
	/**
	 * The publication date of the book (based on the UTC timezone).
	 */
	@Column(nullable = false)
	private LocalDate publicationDate;

	/**
	 * Creates a new book.
	 */
	public Book() {
	}

	/**
	 * Creates a new book with the given title, author, ISBN, and publication year.
	 *
	 * @param id              the unique identifier of the book
	 * @param title           the title of the book
	 * @param author          the author of the book
	 * @param isbn            the ISBN of the book
	 * @param publicationDate the publication date of the book
	 */
	public Book(
			Long id,
			@NonNull String title,
			@NonNull String author,
			@NonNull String isbn,
			@NonNull LocalDate publicationDate
	) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.publicationDate = publicationDate;
	}

	/**
	 * Returns the unique identifier of the book.
	 *
	 * @return the unique identifier of the book
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the unique identifier of the book.
	 *
	 * @param id the unique identifier of the book
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the title of the book.
	 *
	 * @return the title of the book
	 */
	@NonNull
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title of the book.
	 *
	 * @param title the title of the book
	 */
	public void setTitle(@NonNull String title) {
		this.title = title;
	}

	/**
	 * Returns the author of the book.
	 *
	 * @return the author of the book
	 */
	@NonNull
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Sets the author of the book.
	 *
	 * @param author the author of the book
	 */
	public void setAuthor(@NonNull String author) {
		this.author = author;
	}

	/**
	 * Returns the ISBN of the book.
	 *
	 * @return the ISBN of the book
	 */
	@NonNull
	public String getIsbn() {
		return this.isbn;
	}

	/**
	 * Sets the ISBN of the book.
	 *
	 * @param isbn the ISBN of the book
	 */
	public void setIsbn(@NonNull String isbn) {
		this.isbn = isbn;
	}

	/**
	 * Returns the publication date of the book.
	 *
	 * @return the publication date of the book
	 */
	@NonNull
	public LocalDate getPublicationDate() {
		return this.publicationDate;
	}

	/**
	 * Sets the publication date of the book.
	 *
	 * @param publicationDate the publication date of the book
	 */
	public void setPublicationDate(@NonNull LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * Returns the copies of the book.
	 *
	 * @return the copies of the book
	 */
	public Set<BookCopy> getBookCopies() {
		return Collections.unmodifiableSet(this.bookCopies);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param obj the reference object with which to compare
	 * @return true if this object is the same as the obj argument; false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		Book book = (Book) obj;
		return Objects.equals(this.id, book.id) &&
				Objects.equals(this.title, book.title) &&
				Objects.equals(this.author, book.author) &&
				Objects.equals(this.isbn, book.isbn) &&
				Objects.equals(this.publicationDate, book.publicationDate);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.title, this.author, this.isbn, this.publicationDate);
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object
	 */
	@Override
	public String toString() {
		return "Book{" +
				"id=" + this.id +
				", title='" + this.title + '\'' +
				", author='" + this.author + '\'' +
				", isbn='" + this.isbn + '\'' +
				", publicationDate=" + this.publicationDate +
				'}';
	}
}