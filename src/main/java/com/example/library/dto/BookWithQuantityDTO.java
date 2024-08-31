package com.example.library.dto;

import com.example.library.validator.ISBN;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A DTO representing a book with a quantity.
 */
public class BookWithQuantityDTO {
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
	 * The quantity of the book in the library.
	 */
	@Min(0)
	private int quantity;

	/**
	 * Creates a new book quantity DTO.
	 */
	public BookWithQuantityDTO() {
	}

	/**
	 * Creates a new book quantity DTO with the given title, author, ISBN, publication year, and quantity.
	 *
	 * @param title    the title of the book
	 * @param author   the author of the book
	 * @param isbn     the ISBN of the book
	 * @param quantity the quantity of the book in the library
	 */
	public BookWithQuantityDTO(String title, String author, String isbn, int quantity) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.quantity = quantity;
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
	 * Returns the quantity of the book in the library.
	 *
	 * @return the quantity of the book in the library
	 */
	public int getQuantity() {
		return this.quantity;
	}

	/**
	 * Sets the quantity of the book in the library.
	 *
	 * @param quantity the quantity of the book in the library
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
