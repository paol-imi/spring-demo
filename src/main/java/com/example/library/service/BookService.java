package com.example.library.service;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.lib.RepositoryException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookRepository;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for the Book entity.
 */
@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final @NonNull BookRepository bookRepository;
    private final @NonNull BookMapper bookMapper;

    public BookService(@NonNull BookRepository bookRepository, @NonNull BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Get a single book by its id.
     *
     * @param id the id of the book
     * @return an optional with the book if found, empty otherwise
     */
    public @NonNull Optional<BookDTO> getBookById(@NonNull Long id) {
        // Find the book by its ID and map it to a DTO.
        return this.bookRepository.findById(id).map(this.bookMapper::toDto);
    }

    /**
     * Get a list of all books in the library using a specification and pageable.
     *
     * @param bookSpecification the specification to filter the books
     * @param pageable          the pagination information
     * @return a page of books
     */
    public @NonNull Page<BookDTO> getBooks(@Nullable Specification<Book> bookSpecification, @NonNull Pageable pageable) {
        // Find all books using the specification and map them to DTOs.
        return this.bookRepository.findAll(bookSpecification, pageable).map(this.bookMapper::toDto);
    }

    /**
     * Create a new book in the library.
     *
     * @param bookDTO the book to create
     * @return the created book
     * @throws BookAlreadyExistsException if the book already exists
     */
    @Transactional
    public @NonNull BookDTO createBook(@NonNull BookDTO bookDTO) throws BookAlreadyExistsException {
        // Check if the book already exists.
        if (this.bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
            // Throw an exception if the book already exists.
            throw new BookAlreadyExistsException(bookDTO.getIsbn());
        }

        // Ensure the ID is not set
        bookDTO.setId(null);
        // Map the DTO to the entity and save it.
        Book book = this.bookMapper.toEntity(bookDTO);
        // Save the book.
        Book savedBook = this.bookRepository.save(book);
        // Map the entity back to a DTO and return it.
        return this.bookMapper.toDto(savedBook);
    }

    /**
     * Update an existing book in the library.
     *
     * @param id      the id of the book to update
     * @param bookDTO the book data to update
     * @return the updated book
     * @throws BookNotFoundException if the book is not found
     */
    @Transactional
    public @NonNull BookDTO updateBook(@NonNull Long id, @NonNull BookDTO bookDTO) throws BookNotFoundException {
        // Fetch the existing book entity
        Book book = this.bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        // Update the book entity using the mapper
        this.bookMapper.updateBook(bookDTO, book);
        // Convert the updated entity back to DTO and return
        return this.bookMapper.toDto(book);
    }

    /**
     * Delete a book from the library.
     *
     * @param id the id of the book to delete
     * @throws BookNotFoundException if the book is not found
     */
    @Transactional
    public void deleteBook(@NonNull Long id) throws BookNotFoundException {
        // Check if the book exists.
        if (!this.bookRepository.existsById(id)) {
            // Throw an exception if the book is not found.
            throw new BookNotFoundException(id);
        }
        // Delete the book by its ID.
        this.bookRepository.deleteById(id);
    }

    /**
     * Exception thrown when a book is not found.
     */
    public static class BookNotFoundException extends RepositoryException.NotFound {
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
    public static class BookAlreadyExistsException extends RepositoryException.Conflict {
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