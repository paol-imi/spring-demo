package com.example.library.controller;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.service.BookCopyService;
import com.example.library.service.BookService;
import com.example.library.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the Book Copy entity.
 */
@RestController
@RequestMapping("/api/locations/{locationId}/book-copies")
@Tag(name = "BookCopy", description = "The Book Copy API")
public class BookCopyController {
	/**
	 * The BookCopyService instance.
	 */
	private final BookCopyService bookCopyService;

	/**
	 * Create a new BookCopiesController.
	 *
	 * @param bookCopyService the BookCopyService instance
	 */
	public BookCopyController(BookCopyService bookCopyService) {
		this.bookCopyService = bookCopyService;
	}

	/**
	 * Get a paginated list of all book copies at a location.
	 *
	 * @param locationId the ID of the location
	 * @param pageable   the Pageable information for pagination
	 * @return a paginated list of book copies
	 */
	@GetMapping
	@Operation(summary = "List all book copies at a location", description = "Get a paginated list of all book copies at a location.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved list of book copies",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Location not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<?> getBookCopies(
			@Parameter(description = "ID of the location", required = true) @NonNull
			@PathVariable Long locationId,
			@Parameter(description = "Pageable information for pagination") @ParameterObject
			@PageableDefault(size = 20, direction = Sort.Direction.ASC) Pageable pageable
	) {
		try {
			// Get a paginated list of all book copies at a location.
			Page<BookWithQuantityDTO> bookCopies = this.bookCopyService.getBooksWithQuantitiesAtLocation(locationId, pageable);
			// Return the paginated list of book copies.
			return ResponseEntity.ok(bookCopies);
		} catch (LocationService.LocationNotFoundException e) {
			// Return a 404 Not Found response if the location is not found.
			return e.toResponseEntity();
		}
	}

	/**
	 * Update the quantity of a book at a location.
	 * Positive values add copies, negative values remove copies.
	 *
	 * @param locationId     the id of the location
	 * @param bookId         the id of the book
	 * @param quantityChange the change in quantity (positive to add, negative to remove)
	 * @return the updated location DTO
	 */
	@PutMapping("/{bookId}")
	@Operation(summary = "Update the quantity of a book at a location", description = "Positive values add copies, negative values remove copies.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully updated the quantity of the book at the location",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Location or book not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Insufficient copies",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<?> updateBookCopyQuantity(
			@Parameter(description = "ID of the location", required = true) @NonNull
			@PathVariable Long locationId,
			@Parameter(description = "ID of the book", required = true) @NonNull
			@PathVariable Long bookId,
			@Parameter(description = "Change in quantity (positive to add, negative to remove)", required = true) @NonNull
			@RequestParam Integer quantityChange
	) {
		try {
			// Update the quantity of the book at the location.
			Integer updatedQuantity = this.bookCopyService.updateBookCopyQuantity(locationId, bookId, quantityChange);
			// Return the updated quantity.
			return ResponseEntity.ok(updatedQuantity);
		} catch (BookCopyService.InsufficientCopiesException |
				 LocationService.LocationNotFoundException |
				 BookService.BookNotFoundException e) {
			// Return an error response if there are insufficient copies, the location is not found, or the book is not found.
			return e.toResponseEntity();
		}
	}
}