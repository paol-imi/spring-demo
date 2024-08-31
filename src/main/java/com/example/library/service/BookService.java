package com.example.library.service;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.lib.RepositoryException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Service for the Book entity.
 */
public interface BookService {
	/**
	 * Get a single book by its id.
	 *
	 * @param id the id of the book
	 * @return an optional with the book if found, empty otherwise
	 */
	@NonNull
	Optional<BookDTO> getBookById(@NonNull Long id);

	/**
	 * Get a list of all books in the library using a specification and pageable.
	 *
	 * @param bookSpecification the specification to filter the books
	 * @param pageable          the pagination information
	 * @return a page of books
	 */
	@NonNull
	Page<BookDTO> getBooks(@Nullable Specification<Book> bookSpecification, Pageable pageable);

	/**
	 * Create a new book in the library.
	 *
	 * @param bookDTO the book to create
	 * @return the created book
	 */
	@NonNull
	BookDTO createBook(@NonNull BookDTO bookDTO) throws BookAlreadyExistsException;

	/**
	 * Update an existing book in the library.
	 *
	 * @param id      the id of the book to update
	 * @param bookDTO the book data to update
	 * @return the updated book
	 */

	@NonNull
	BookDTO updateBook(@NonNull Long id, @NonNull BookDTO bookDTO) throws BookNotFoundException;

	/**
	 * Delete a book from the library.
	 *
	 * @param id the id of the book to delete
	 */
	void deleteBook(@NonNull Long id) throws BookNotFoundException;

	/**
	 * Exception thrown when a book is not found.
	 */
	class BookNotFoundException extends RepositoryException.NotFound {
		/**
		 * Creates a new BookNotFoundException with the given id.
		 *
		 * @param id - the id of the book
		 */
		public BookNotFoundException(@NotNull Long id) {
			super("Book not found with id: " + id);
		}
	}

	/**
	 * Exception thrown when a book already exists.
	 */
	class BookAlreadyExistsException extends RepositoryException.Conflict {
		/**
		 * Creates a new BookAlreadyExistsException with the given ISBN.
		 *
		 * @param isbn - the ISBN of the book
		 */
		public BookAlreadyExistsException(@NotNull String isbn) {
			super("Book already exists with ISBN: " + isbn);
		}
	}
}