package com.example.library.service;

import com.example.library.dto.LocationDTO;
import com.example.library.entity.Location;
import com.example.library.lib.RepositoryException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Service for the Location entity.
 */
public interface LocationService {
	/**
	 * Get a single location by its id.
	 *
	 * @param id the id of the location
	 * @return an optional with the location if found, empty otherwise
	 */
	@NonNull
	Optional<LocationDTO> getLocationById(@NonNull Long id);

	/**
	 * Get a list of all locations in the library using a specification and pageable.
	 *
	 * @param locationSpecification the specification to filter the locations
	 * @param pageable              the pagination information
	 * @return a page of locations
	 */
	@NonNull
	Page<LocationDTO> getLocations(@Nullable Specification<Location> locationSpecification, Pageable pageable);

	/**
	 * Create a new location in the library.
	 *
	 * @param locationDTO the location to create
	 * @return the created location
	 */
	@NonNull
	LocationDTO createLocation(@NonNull LocationDTO locationDTO);

	/**
	 * Update an existing location in the library.
	 *
	 * @param id          the id of the location to update
	 * @param locationDTO the location data to update
	 * @return the updated location
	 * @throws LocationNotFoundException if the location is not found
	 */
	@NonNull
	LocationDTO updateLocation(@NonNull Long id, @NonNull LocationDTO locationDTO) throws LocationNotFoundException;

	/**
	 * Delete a location from the library.
	 *
	 * @param id the id of the location to delete
	 * @throws LocationNotFoundException if the location is not found
	 */
	void deleteLocation(@NonNull Long id) throws LocationNotFoundException;

	/**
	 * Exception thrown when a location is not found.
	 */
	class LocationNotFoundException extends RepositoryException.NotFound {
		/**
		 * Creates a new LocationNotFoundException with the given id.
		 *
		 * @param id - the id of the location
		 */
		public LocationNotFoundException(@NotNull Long id) {
			super("Location not found with id: " + id);
		}
	}
}