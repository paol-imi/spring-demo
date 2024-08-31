package com.example.library.service;

import com.example.library.dto.LocationDTO;
import com.example.library.entity.Location;
import com.example.library.repository.LocationRepository;
import com.example.library.specification.LocationSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationService Tests")
class LocationServiceTest {

	@Mock
	private LocationRepository locationRepository;

	private ModelMapper modelMapper;
	private LocationService locationService;

	private Location testLocation;
	private LocationDTO testLocationDTO;

	@BeforeEach
	void setUp() {
		modelMapper = new ModelMapper();
		locationService = new LocationServiceImpl(locationRepository, modelMapper);

		testLocation = new Location(1L, "Test Location", "Test Address");
		testLocationDTO = modelMapper.map(testLocation, LocationDTO.class);
	}

	@Nested
	@DisplayName("getLocationById")
	class GetLocationById {

		@Test
		@DisplayName("should return location when found")
		void shouldReturnLocationWhenFound() {
			when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));

			Optional<LocationDTO> result = locationService.getLocationById(1L);

			assertThat(result).isPresent();
			assertThat(result.get()).usingRecursiveComparison().isEqualTo(testLocationDTO);
			verify(locationRepository).findById(1L);
		}

		@Test
		@DisplayName("should return empty when location not found")
		void shouldReturnEmptyWhenLocationNotFound() {
			when(locationRepository.findById(1L)).thenReturn(Optional.empty());

			Optional<LocationDTO> result = locationService.getLocationById(1L);

			assertThat(result).isEmpty();
			verify(locationRepository).findById(1L);
		}
	}

	@Nested
	@DisplayName("getLocations")
	class GetLocations {

		@Test
		@DisplayName("should return paged locations")
		void shouldReturnPagedLocations() {
			Pageable pageable = PageRequest.of(0, 10);
			LocationSpecification spec = mock(LocationSpecification.class);
			List<Location> locations = Arrays.asList(testLocation, new Location(2L, "Another Location", "Another Address"));
			Page<Location> locationPage = new PageImpl<>(locations, pageable, locations.size());

			when(locationRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(locationPage);

			Page<LocationDTO> result = locationService.getLocations(spec, pageable);

			assertThat(result).isNotNull();
			assertThat(result.getContent()).hasSize(2);
			assertThat(result.getContent().get(0)).usingRecursiveComparison().isEqualTo(testLocationDTO);
			verify(locationRepository).findAll(any(Specification.class), eq(pageable));
		}
	}

	@Nested
	@DisplayName("createLocation")
	class CreateLocation {

		@Test
		@DisplayName("should create location successfully")
		void shouldCreateLocationSuccessfully() {
			when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

			LocationDTO result = locationService.createLocation(testLocationDTO);

			testLocationDTO.setId(1L);
			assertThat(result).usingRecursiveComparison().isEqualTo(testLocationDTO);
			verify(locationRepository).save(any(Location.class));
		}
	}

	@Nested
	@DisplayName("updateLocation")
	class UpdateLocation {

		@Test
		@DisplayName("should update location successfully")
		void shouldUpdateLocationSuccessfully() throws LocationService.LocationNotFoundException {
			when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
			when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

			LocationDTO updatedLocationDTO = new LocationDTO(1L, "Updated Location", "Updated Address");
			LocationDTO result = locationService.updateLocation(1L, updatedLocationDTO);

			assertThat(result).usingRecursiveComparison().isEqualTo(updatedLocationDTO);
			verify(locationRepository).findById(1L);
			verify(locationRepository).save(any(Location.class));
		}

		@Test
		@DisplayName("should throw exception when location not found")
		void shouldThrowExceptionWhenLocationNotFound() {
			when(locationRepository.findById(1L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> locationService.updateLocation(1L, testLocationDTO))
					.isInstanceOf(LocationService.LocationNotFoundException.class)
					.hasMessageContaining("Location not found with id: 1");

			verify(locationRepository).findById(1L);
			verifyNoMoreInteractions(locationRepository);
		}
	}

	@Nested
	@DisplayName("deleteLocation")
	class DeleteLocation {

		@Test
		@DisplayName("should delete location successfully")
		void shouldDeleteLocationSuccessfully() throws LocationService.LocationNotFoundException {
			when(locationRepository.existsById(1L)).thenReturn(true);

			locationService.deleteLocation(1L);

			verify(locationRepository).existsById(1L);
			verify(locationRepository).deleteById(1L);
		}

		@Test
		@DisplayName("should throw exception when location not found")
		void shouldThrowExceptionWhenLocationNotFound() {
			when(locationRepository.existsById(1L)).thenReturn(false);

			assertThatThrownBy(() -> locationService.deleteLocation(1L))
					.isInstanceOf(LocationService.LocationNotFoundException.class)
					.hasMessageContaining("Location not found with id: 1");

			verify(locationRepository).existsById(1L);
			verifyNoMoreInteractions(locationRepository);
		}
	}
}