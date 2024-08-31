package com.example.library.service;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.lib.RepositoryException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

/**
 * Service for the Book entity.
 */
public interface BookCopyService {
	/**
	 * Update the quantity of a book at a location. Positive values add copies, negative values remove copies.
	 *
	 * @param locationId     the id of the location
	 * @param bookId         the id of the book
	 * @param quantityChange the change in quantity (positive to add, negative to remove)
	 * @return the updated location DTO
	 * @throws LocationService.LocationNotFoundException if the location is not found
	 * @throws BookService.BookNotFoundException         if the book is not found
	 * @throws InsufficientCopiesException               if there are not enough copies to remove
	 */
	@NonNull
	Integer updateBookCopyQuantity(@NonNull Long locationId, @NonNull Long bookId, @NonNull Integer quantityChange)
			throws LocationService.LocationNotFoundException, BookService.BookNotFoundException, InsufficientCopiesException;

	/**
	 * Get the quantity of a book at a location.
	 *
	 * @param locationId the id of the location
	 * @param bookId     the id of the book
	 * @return the quantity of the book at the location
	 * @throws LocationService.LocationNotFoundException if the location is not found
	 * @throws BookService.BookNotFoundException         if the book is not found
	 */
	@NonNull
	Integer getBookCopyQuantity(@NonNull Long locationId, @NonNull Long bookId) throws LocationService.LocationNotFoundException, BookService.BookNotFoundException;

	/**
	 * Get a list of all books in the library at a location using a pageable.
	 *
	 * @param locationId the id of the location
	 * @param pageable   the pagination information
	 * @return a page of books with quantities at the location
	 * @throws LocationService.LocationNotFoundException if the location is not found
	 */
	@NonNull
	Page<BookWithQuantityDTO> getBooksWithQuantitiesAtLocation(@NonNull Long locationId, Pageable pageable) throws LocationService.LocationNotFoundException;

	/**
	 * Exception thrown when there are not enough copies of a book to remove.
	 */
	class InsufficientCopiesException extends RepositoryException.Conflict {
		/**
		 * Creates a new InsufficientCopiesException with the given location, book, quantity, and requested quantity.
		 *
		 * @param locationId        the id of the location
		 * @param bookId            the id of the book
		 * @param quantity          the quantity of books at the location
		 * @param requestedQuantity the requested quantity
		 */
		public InsufficientCopiesException(@NotNull Long locationId, @NotNull Long bookId, @NotNull Integer quantity, @NotNull Integer requestedQuantity) {
			super("Insufficient copies of book " + bookId + " at location " + locationId + ". Found " + quantity + ", requested " + requestedQuantity);
		}
	}
}