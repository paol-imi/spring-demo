package com.example.library.repository;

import com.example.library.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Location Repository Tests")
class LocationRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private LocationRepository locationRepository;

	private Location testLocation;

	@BeforeEach
	void setUp() {
		testLocation = new Location(null, "Location", "Address");
		testLocation = entityManager.persist(testLocation);
		entityManager.flush();
	}

	@Nested
	@DisplayName("Find operations")
	class FindOperations {

		@Test
		@DisplayName("Find location by ID")
		void whenFindById_thenReturnLocation() {
			Optional<Location> found = locationRepository.findById(testLocation.getId());

			assertThat(found).isPresent();
			assertThat(found.get().getName()).isEqualTo(testLocation.getName());
			assertThat(found.get().getAddress()).isEqualTo(testLocation.getAddress());
		}

		@Test
		@DisplayName("Find location by non-existent ID")
		void whenFindByNonExistentId_thenReturnEmpty() {
			Optional<Location> found = locationRepository.findById(999L);

			assertThat(found).isEmpty();
		}

		@Test
		@DisplayName("Find all locations")
		void whenFindAll_thenReturnAllLocations() {
			Location anotherLocation = new Location(null, "Another Location", "Another Address");
			entityManager.persist(anotherLocation);
			entityManager.flush();

			List<Location> locations = locationRepository.findAll();

			assertThat(locations).hasSize(2);
			assertThat(locations).extracting(Location::getName).containsExactlyInAnyOrder("Location", "Another Location");
		}
	}

	@Nested
	@DisplayName("Save operations")
	class SaveOperations {

		@Test
		@DisplayName("Save new location")
		void whenSaveLocation_thenLocationIsPersisted() {
			Location newLocation = new Location(null, "New Location", "New Address");
			Location saved = locationRepository.save(newLocation);

			assertThat(saved.getId()).isNotNull();
			Location found = entityManager.find(Location.class, saved.getId());
			assertThat(found).isNotNull();
			assertThat(found.getName()).isEqualTo("New Location");
			assertThat(found.getAddress()).isEqualTo("New Address");
		}

		@Test
		@DisplayName("Update existing location")
		void whenUpdateLocation_thenLocationIsUpdated() {
			testLocation.setName("Updated Name");
			testLocation.setAddress("Updated Address");
			Location updated = locationRepository.save(testLocation);

			assertThat(updated.getName()).isEqualTo("Updated Name");
			assertThat(updated.getAddress()).isEqualTo("Updated Address");
			Location found = entityManager.find(Location.class, testLocation.getId());
			assertThat(found.getName()).isEqualTo("Updated Name");
			assertThat(found.getAddress()).isEqualTo("Updated Address");
		}
	}

	@Nested
	@DisplayName("Delete operations")
	class DeleteOperations {

		@Test
		@DisplayName("Delete location")
		void whenDeleteLocation_thenLocationIsRemoved() {
			locationRepository.delete(testLocation);
			entityManager.flush();

			Location found = entityManager.find(Location.class, testLocation.getId());
			assertThat(found).isNull();
		}

		@Test
		@DisplayName("Delete location by ID")
		void whenDeleteLocationById_thenLocationIsRemoved() {
			locationRepository.deleteById(testLocation.getId());
			entityManager.flush();

			Location found = entityManager.find(Location.class, testLocation.getId());
			assertThat(found).isNull();
		}
	}

	@Nested
	@DisplayName("Pagination and sorting")
	class PaginationAndSorting {

		@BeforeEach
		void setUpMultipleLocations() {
			for (int i = 0; i < 20; i++) {
				Location location = new Location(null, "Location " + i, "Address " + i);
				entityManager.persist(location);
			}
			entityManager.flush();
		}

		@Test
		@DisplayName("Find locations with pagination")
		void whenFindAllWithPagination_thenReturnPagedResult() {
			Page<Location> locationPage = locationRepository.findAll(PageRequest.of(0, 10));

			assertThat(locationPage.getContent()).hasSize(10);
			assertThat(locationPage.getTotalElements()).isEqualTo(21); // 20 new locations + 1 test location
			assertThat(locationPage.getTotalPages()).isEqualTo(3);
			assertThat(locationPage.getNumber()).isZero();
			assertThat(locationPage.getSize()).isEqualTo(10);
		}

		@Test
		@DisplayName("Find locations with pagination and sorting")
		void whenFindAllWithPaginationAndSorting_thenReturnSortedPagedResult() {
			Page<Location> locationPage = locationRepository.findAll(
					PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name")));

			assertThat(locationPage.getContent()).hasSize(10);
			assertThat(locationPage.getContent().get(0).getName()).startsWith("Location");
			assertThat(locationPage.getContent().get(9).getName()).startsWith("Location");
		}
	}
}