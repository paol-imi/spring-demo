package com.example.library.service;

import com.example.library.dto.LocationDTO;
import com.example.library.entity.Location;
import com.example.library.repository.LocationRepository;
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
 * Service implementation for the Location entity.
 */
@Service
public class LocationServiceImpl implements LocationService {
	/**
	 * The repository for the Location entity.
	 */
	private final @NonNull LocationRepository locationRepository;

	/**
	 * The ModelMapper instance.
	 */
	private final @NonNull ModelMapper modelMapper;

	/**
	 * Create a new LocationServiceImpl.
	 *
	 * @param locationRepository the repository for the Location entity
	 * @param modelMapper        the ModelMapper instance
	 */
	public LocationServiceImpl(@NonNull LocationRepository locationRepository, @NonNull ModelMapper modelMapper) {
		this.locationRepository = locationRepository;
		this.modelMapper = modelMapper;
	}

	/**
	 * Get a single location by its id.
	 *
	 * @param id the id of the location
	 * @return an optional with the location if found, empty otherwise
	 */
	@Override
	public @NonNull Optional<LocationDTO> getLocationById(@NonNull Long id) {
		// Find the location by its ID and map it to a DTO.
		return this.locationRepository.findById(id)
				.map(location -> this.modelMapper.map(location, LocationDTO.class));
	}

	/**
	 * Get a list of all locations in the library using a specification and pageable.
	 *
	 * @param locationSpecification the specification to filter the locations
	 * @param pageable              the pagination information
	 * @return a page of locations
	 */
	@Override
	public @NonNull Page<LocationDTO> getLocations(@Nullable Specification<Location> locationSpecification, @NonNull Pageable pageable) {
		// Find all locations using the specification and map them to DTOs.
		return this.locationRepository.findAll(locationSpecification, pageable)
				.map(location -> this.modelMapper.map(location, LocationDTO.class));
	}

	/**
	 * Create a new location in the library.
	 *
	 * @param locationDTO the location to create
	 * @return the created location
	 */
	@Override
	@Transactional
	public @NonNull LocationDTO createLocation(@NonNull LocationDTO locationDTO) {
		// Ensure the ID is not set
		locationDTO.setId(null);
		// Map the DTO to the entity and save it.
		Location location = this.modelMapper.map(locationDTO, Location.class);
		// Save the location.
		Location savedLocation = this.locationRepository.save(location);
		// Map the entity back to a DTO and return it.
		return this.modelMapper.map(savedLocation, LocationDTO.class);
	}

	/**
	 * Update an existing location in the library.
	 *
	 * @param id          the id of the location to update
	 * @param locationDTO the location data to update
	 * @return the updated location
	 * @throws LocationNotFoundException if the location is not found
	 */
	@Override
	@Transactional
	public @NonNull LocationDTO updateLocation(@NonNull Long id, @NonNull LocationDTO locationDTO) throws LocationNotFoundException {
		// Find the location by its ID.
		Location location = this.locationRepository.findById(id)
				// Throw an exception if the location is not found.
				.orElseThrow(() -> new LocationNotFoundException(id));
		// Map the DTO to the entity.
		this.modelMapper.map(locationDTO, location);
		// Ensure the ID is not overwritten
		location.setId(id);
		// Save the updated location.
		Location updatedLocation = this.locationRepository.save(location);
		// Map the entity back to a DTO and return it.
		LocationDTO locationDTO1 = this.modelMapper.map(updatedLocation, LocationDTO.class);
		return locationDTO1;
	}

	/**
	 * Delete a location from the library.
	 *
	 * @param id the id of the location to delete
	 * @throws LocationNotFoundException if the location is not found
	 */
	@Override
	@Transactional
	public void deleteLocation(@NonNull Long id) throws LocationNotFoundException {
		// Check if the location exists.
		if (!this.locationRepository.existsById(id)) {
			// Throw an exception if the location is not found.
			throw new LocationNotFoundException(id);
		}
		// Delete the location by its ID.
		this.locationRepository.deleteById(id);
	}
}