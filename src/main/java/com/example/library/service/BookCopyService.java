package com.example.library.service;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.entity.Book;
import com.example.library.entity.BookCopy;
import com.example.library.entity.Location;
import com.example.library.lib.RepositoryException;
import com.example.library.metrics.LibraryMetrics;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LocationRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for the Book entity.
 */
@Service
public class BookCopyService {
    /**
     * The logger for this class.
     */
    public static final Logger logger = LoggerFactory.getLogger(BookCopyService.class);

    private final LibraryMetrics metrics;

    /**
     * The repository for the Book entity.
     */
    private final @NonNull BookCopyRepository bookCopyRepository;

    /**
     * The repository for the Location entity.
     */
    private final @NonNull LocationRepository locationRepository;

    /**
     * The repository for the Book entity.
     */
    private final @NonNull BookRepository bookRepository;

    /**
     * Create a new BookServiceImpl.
     *
     * @param bookCopyRepository the repository for the Book entity
     * @param locationRepository the repository for the Location entity
     * @param bookRepository     the repository for the Book entity
     */
    public BookCopyService(
            @NonNull BookCopyRepository bookCopyRepository,
            @NonNull LocationRepository locationRepository,
            @NonNull BookRepository bookRepository,
            @NonNull LibraryMetrics metrics
    ) {
        this.bookCopyRepository = bookCopyRepository;
        this.locationRepository = locationRepository;
        this.bookRepository = bookRepository;
        this.metrics = metrics;
    }

    /**
     * Update the quantity of a book at a location.
     * Positive values add copies, negative values remove copies.
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
    @Transactional
    public Integer updateBookCopyQuantity(@NonNull Long locationId, @NonNull Long bookId, @NonNull Integer quantityChange) throws LocationService.LocationNotFoundException, BookService.BookNotFoundException, InsufficientCopiesException {
        Optional<BookCopy> optionalBookCopy = this.bookCopyRepository.findById(new BookCopy.BookCopyId(locationId, bookId));
        // Check if the book copy exists.
        if (optionalBookCopy.isEmpty()) {
            // Find the location by its ID.
            Location location = this.locationRepository.findById(locationId)
                    // Throw an exception if the location is not found.
                    .orElseThrow(() -> new LocationService.LocationNotFoundException(locationId));

            // Check if the book exists.
            Book book = this.bookRepository.findById(bookId)
                    // Throw an exception if the book is not found.
                    .orElseThrow(() -> new BookService.BookNotFoundException(bookId));

            // Create a new book copy.
            BookCopy bookCopy = new BookCopy(book, location, 0);
            optionalBookCopy = Optional.of(bookCopy);
        }
        // Get the book copy.
        BookCopy bookCopy = optionalBookCopy.get();
        // Check if the quantity is negative.
        if (bookCopy.getQuantity() + quantityChange < 0) {
            // If the quantity is negative, throw an exception.
            throw new InsufficientCopiesException(locationId, bookId, bookCopy.getQuantity(), -quantityChange);
        }

        logger.info("Updating book copy quantity at location {} for book {} by {}", locationId, bookId, quantityChange);
        // Update the metrics.
        if (quantityChange > 0) {
            this.metrics.recordBookAdded();
        } else {
            this.metrics.recordBookRemoved();
        }

        // Update the quantity of the book at the location.
        bookCopy.setQuantity(bookCopy.getQuantity() + quantityChange);
        // Save the updated location.
        BookCopy savedBookCopy = this.bookCopyRepository.save(bookCopy);
        // Return the updated quantity.
        return savedBookCopy.getQuantity();
    }

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
    @Transactional(readOnly = true)
    public Integer getBookCopyQuantity(@NonNull Long locationId, @NonNull Long bookId) throws LocationService.LocationNotFoundException, BookService.BookNotFoundException {
        // Check if the location exists.
        if (!this.locationRepository.existsById(locationId)) {
            // Throw an exception if the location is not found.
            throw new LocationService.LocationNotFoundException(locationId);
        }
        // Check if the book exists.
        if (!this.bookRepository.existsById(bookId)) {
            // Throw an exception if the book is not found.
            throw new BookService.BookNotFoundException(bookId);
        }
        // Find the quantity of the book at the location.
        return this.bookCopyRepository.getByLocationIdAndBookId(locationId, bookId).getQuantity();
    }

    /**
     * Get a list of all books in the library at a location using a pageable.
     *
     * @param locationId the id of the location
     * @param pageable   the pagination information
     * @return a page of books with quantities at the location
     * @throws LocationService.LocationNotFoundException if the location is not found
     */
    @NonNull
    @Transactional(readOnly = true)
    public Page<BookWithQuantityDTO> getBooksWithQuantitiesAtLocation(@NonNull Long locationId, Pageable pageable) throws LocationService.LocationNotFoundException {
        // Check if the location exists.
        if (!this.locationRepository.existsById(locationId)) {
            throw new LocationService.LocationNotFoundException(locationId);
        }
        // Find the books with quantities at the location.
        return this.bookCopyRepository.findBooksWithQuantitiesByLocationId(locationId, pageable);
    }

    /**
     * Exception thrown when there are not enough copies of a book to remove.
     */
    public static class InsufficientCopiesException extends RepositoryException.Conflict {
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