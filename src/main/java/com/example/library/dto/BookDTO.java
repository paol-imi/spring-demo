package com.example.library.dto;


import com.example.library.validator.ISBN;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO representing a book.
 */
public class BookDTO {
	/**
	 * The unique identifier of the book.
	 */
	private Long id;

	/**
	 * The title of the book.
	 */
	@NotNull
	@Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
	private String title;

	/**
	 * The author of the book.
	 */
	@NotNull
	@Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
	private String author;

	/**
	 * The ISBN of the book.
	 */
	@NotNull
	@ISBN
	private String isbn;

	/**
	 * The publication date of the book.
	 */
	@NotNull
	@Past(message = "Publication date must be in the past")
	private LocalDate publicationDate;

	/**
	 * Creates a new book DTO.
	 */
	public BookDTO() {
	}

	/**
	 * Creates a new book DTO with the given title, author, ISBN, and publication year.
	 *
	 * @param id              the unique identifier of the book
	 * @param title           the title of the book
	 * @param author          the author of the book
	 * @param isbn            the ISBN of the book
	 * @param publicationDate the publication date of the book
	 */
	public BookDTO(Long id, String title, String author, String isbn, LocalDate publicationDate) {
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
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title of the book.
	 *
	 * @param title the title of the book
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the author of the book.
	 *
	 * @return the author of the book
	 */

	public String getAuthor() {
		return this.author;
	}

	/**
	 * Sets the author of the book.
	 *
	 * @param author the author of the book
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns the ISBN of the book.
	 *
	 * @return the ISBN of the book
	 */

	public String getIsbn() {
		return this.isbn;
	}

	/**
	 * Sets the ISBN of the book.
	 *
	 * @param isbn the ISBN of the book
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * Returns the publication date of the book.
	 *
	 * @return the publication date of the book
	 */
	public LocalDate getPublicationDate() {
		return this.publicationDate;
	}

	/**
	 * Sets the publication date of the book.
	 *
	 * @param publicationDate the publication date of the book
	 */
	public void setPublicationDate(LocalDate publicationDate) {
		this.publicationDate = publicationDate;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param o the reference object with which to compare
	 * @return true if this object is the same as the obj argument; false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		BookDTO bookDTO = (BookDTO) o;

		if (!this.id.equals(bookDTO.id)) return false;
		if (!this.title.equals(bookDTO.title)) return false;
		if (!this.author.equals(bookDTO.author)) return false;
		if (!this.isbn.equals(bookDTO.isbn)) return false;
		return this.publicationDate.equals(bookDTO.publicationDate);
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
	 * Returns the string representation of the book.
	 *
	 * @return the string representation of the book
	 */
	@Override
	public String toString() {
		return "BookDTO{" +
				"id=" + this.id +
				", title='" + this.title + '\'' +
				", author='" + this.author + '\'' +
				", isbn='" + this.isbn + '\'' +
				", publicationDate=" + this.publicationDate +
				'}';
	}
}