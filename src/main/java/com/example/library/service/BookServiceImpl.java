package com.example.library.service;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.modelmapper.ModelMapper;
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
public class BookServiceImpl implements BookService {
	/**
	 * The repository for the Book entity.
	 */
	private final @NonNull BookRepository bookRepository;

	/**
	 * The ModelMapper instance.
	 */
	private final @NonNull ModelMapper modelMapper;

	/**
	 * Create a new BookServiceImpl.
	 *
	 * @param bookRepository the repository for the Book entity
	 * @param modelMapper    the ModelMapper instance
	 */
	public BookServiceImpl(@NonNull BookRepository bookRepository, @NonNull ModelMapper modelMapper) {
		this.bookRepository = bookRepository;
		this.modelMapper = modelMapper;
	}

	/**
	 * Get a single book by its id.
	 *
	 * @param id the id of the book
	 * @return an optional with the book if found, empty otherwise
	 */
	@Override
	public @NonNull Optional<BookDTO> getBookById(@NonNull Long id) {
		// Find the book by its ID and map it to a DTO.
		return this.bookRepository.findById(id)
				.map(book -> this.modelMapper.map(book, BookDTO.class));
	}

	/**
	 * Get a list of all books in the library using a specification and pageable.
	 *
	 * @param bookSpecification the specification to filter the books
	 * @param pageable          the pagination information
	 * @return a page of books
	 */
	@Override
	public @NonNull Page<BookDTO> getBooks(@Nullable Specification<Book> bookSpecification, @NonNull Pageable pageable) {
		// Find all books using the specification and map them to DTOs.
		return this.bookRepository.findAll(bookSpecification, pageable)
				.map(book -> this.modelMapper.map(book, BookDTO.class));
	}

	/**
	 * Create a new book in the library.
	 *
	 * @param bookDTO the book to create
	 * @return the created book
	 * @throws BookAlreadyExistsException if the book already exists
	 */
	@Override
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
		Book book = this.modelMapper.map(bookDTO, Book.class);
		// Save the book.
		Book savedBook = this.bookRepository.save(book);
		// Map the entity back to a DTO and return it.
		return this.modelMapper.map(savedBook, BookDTO.class);
	}

	/**
	 * Update an existing book in the library.
	 *
	 * @param id      the id of the book to update
	 * @param bookDTO the book data to update
	 * @return the updated book
	 * @throws BookNotFoundException if the book is not found
	 */
	@Override
	@Transactional
	public @NonNull BookDTO updateBook(@NonNull Long id, @NonNull BookDTO bookDTO) throws BookNotFoundException {
		// Find the book by its ID.
		Book book = this.bookRepository.findById(id)
				// Throw an exception if the book is not found.
				.orElseThrow(() -> new BookNotFoundException(id));
		// Map the DTO to the entity.
		this.modelMapper.map(bookDTO, book);
		// Ensure the ID is not overwritten
		book.setId(id);
		// Save the updated book.
		Book updatedBook = this.bookRepository.save(book);
		// Map the entity back to a DTO and return it.
		return this.modelMapper.map(updatedBook, BookDTO.class);
	}

	/**
	 * Delete a book from the library.
	 *
	 * @param id the id of the book to delete
	 * @throws BookNotFoundException if the book is not found
	 */
	@Override
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
}