package com.example.library.service;

import com.example.library.dto.LocationDTO;
import com.example.library.entity.Location;
import com.example.library.lib.RepositoryException;
import com.example.library.mapper.LocationMapper;
import com.example.library.repository.LocationRepository;
import jakarta.validation.constraints.NotNull;
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
public class LocationService {
    /**
     * The repository for the Location entity.
     */
    private final @NonNull LocationRepository locationRepository;

    /**
     * The ModelMapper instance.
     */
    private final @NonNull LocationMapper locationMapper;

    /**
     * Create a new LocationServiceImpl.
     *
     * @param locationRepository the repository for the Location entity
     * @param modelMapper        the ModelMapper instance
     */
    public LocationService(@NonNull LocationRepository locationRepository,@NonNull LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    /**
     * Get a single location by its id.
     *
     * @param id the id of the location
     * @return an optional with the location if found, empty otherwise
     */
    public @NonNull Optional<LocationDTO> getLocationById(@NonNull Long id) {
        // Find the location by its ID and map it to a DTO.
        return this.locationRepository.findById(id).map(this.locationMapper::toDto);
    }

    /**
     * Get a list of all locations in the library using a specification and pageable.
     *
     * @param locationSpecification the specification to filter the locations
     * @param pageable              the pagination information
     * @return a page of locations
     */
    public @NonNull Page<LocationDTO> getLocations(@Nullable Specification<Location> locationSpecification, @NonNull Pageable pageable) {
        // Find all locations using the specification and map them to DTOs.
        return this.locationRepository.findAll(locationSpecification, pageable).map(this.locationMapper::toDto);
    }

    /**
     * Create a new location in the library.
     *
     * @param locationDTO the location to create
     * @return the created location
     */
    @Transactional
    public @NonNull LocationDTO createLocation(@NonNull LocationDTO locationDTO) {
        // Ensure the ID is not set
        locationDTO.setId(null);
        // Map the DTO to the entity and save it.
        Location location = this.locationMapper.toEntity(locationDTO);
        // Save the location.
        Location savedLocation = this.locationRepository.save(location);
        // Map the entity back to a DTO and return it.
        return this.locationMapper.toDto(savedLocation);
    }

    /**
     * Update an existing location in the library.
     *
     * @param id          the id of the location to update
     * @param locationDTO the location data to update
     * @return the updated location
     * @throws LocationNotFoundException if the location is not found
     */
    @Transactional
    public @NonNull LocationDTO updateLocation(@NonNull Long id, @NonNull LocationDTO locationDTO) throws LocationNotFoundException {
        // Find the location by its ID.
        Location location = this.locationRepository.findById(id).orElseThrow(() -> new LocationNotFoundException(id));
        // Map the DTO to the entity.
        this.locationMapper.updateLocation(locationDTO, location);
        // Save the updated location.
        return this.locationMapper.toDto(location);
    }

    /**
     * Delete a location from the library.
     *
     * @param id the id of the location to delete
     * @throws LocationNotFoundException if the location is not found
     */
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

    /**
     * Exception thrown when a location is not found.
     */
    public static class LocationNotFoundException extends RepositoryException.NotFound {
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