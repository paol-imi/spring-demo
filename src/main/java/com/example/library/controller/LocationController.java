package com.example.library.controller;

import com.example.library.dto.LocationDTO;
import com.example.library.service.LocationService;
import com.example.library.specification.LocationSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for the Location entity.
 */
@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location", description = "The Location API")
public class LocationController {
	/**
	 * The LocationService instance.
	 */
	private final LocationService locationService;

	/**
	 * Create a new LocationController.
	 *
	 * @param locationService the LocationService instance
	 */
	public LocationController(LocationService locationService) {
		this.locationService = locationService;
	}

	/**
	 * Get a paginated list of all locations in the library. Optional filtering by name.
	 *
	 * @param name     the name to filter by (case-insensitive, partial match, optional)
	 * @param pageable the Pageable information for pagination (optional, default page: 0, size: 20, sort: name, direction: ASC)
	 * @return a paginated list of locations
	 */
	@GetMapping
	@Operation(summary = "List all locations", description = "Get a paginated list of all locations in the library. Optional filtering by name.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved list of locations",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Page<LocationDTO>> getAllLocations(
			@Parameter(description = "Filter locations by name (case-insensitive, partial match)")
			@RequestParam(required = false) @Nullable
			String name,
			@Parameter(description = "Pageable information for pagination") @ParameterObject
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) @NonNull
			Pageable pageable
	) {
		// Return a paginated list of locations.
		return ResponseEntity.ok(this.locationService.getLocations(LocationSpecification.nameLike(name), pageable));
	}

	/**
	 * Get a single location by its id.
	 *
	 * @param id the id of the location
	 * @return the location if found, empty otherwise
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Get a location by id", description = "Get a single location by its id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the location",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationDTO.class))),
			@ApiResponse(responseCode = "404", description = "Location not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<LocationDTO> getLocationById(
			@Parameter(description = "ID of the location to retrieve", required = true) @NonNull
			@PathVariable
			Long id
	) {
		// Get the location by its ID.
		return this.locationService.getLocationById(id)
				// Return the location if found.
				.map(ResponseEntity::ok)
				// Return a 404 Not Found response if the location is not found.
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Create a new location in the library.
	 *
	 * @param locationDTO the location to create
	 * @return the created location
	 */
	@PostMapping
	@Operation(summary = "Create a new location", description = "Create a new location in the library")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully created the location",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<?> createLocation(
			@Parameter(description = "Location to add to the library", required = true) @NonNull
			@Valid @RequestBody
			LocationDTO locationDTO
	) {
		// Create the location and return it.
		return ResponseEntity.ok(this.locationService.createLocation(locationDTO));
	}

	/**
	 * Update an existing location in the library.
	 *
	 * @param id          the id of the location to update
	 * @param locationDTO the location data to update
	 * @return the updated location
	 */
	@PutMapping("/{id}")
	@Operation(summary = "Update a location", description = "Update an existing location in the library")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully updated the location",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocationDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Location not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<?> updateLocation(
			@Parameter(description = "ID of the location to update", required = true) @NonNull
			@PathVariable
			Long id,
			@Parameter(description = "Updated location information", required = true) @NonNull
			@Valid @RequestBody
			LocationDTO locationDTO
	) {
		try {
			// Update the location by its ID and return it.
			return ResponseEntity.ok(this.locationService.updateLocation(id, locationDTO));
		} catch (LocationService.LocationNotFoundException e) {
			// Return a 404 Not Found response if the location is not found.
			return e.toResponseEntity();
		}
	}

	/**
	 * Delete a location from the library.
	 *
	 * @param id the id of the location to delete
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a location", description = "Delete a location from the library")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Successfully deleted the location"),
			@ApiResponse(responseCode = "404", description = "Location not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<?> deleteLocation(
			@Parameter(description = "ID of the location to delete", required = true) @NonNull
			@PathVariable Long id
	) {
		try {
			// Delete the location by its ID.
			this.locationService.deleteLocation(id);
			// Return a 204 No Content response.
			return ResponseEntity.noContent().build();
		} catch (LocationService.LocationNotFoundException e) {
			// Return a 404 Not Found response.
			return e.toResponseEntity();
		}
	}
}