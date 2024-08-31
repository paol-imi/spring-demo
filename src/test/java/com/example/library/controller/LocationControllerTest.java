package com.example.library.controller;

import com.example.library.dto.LocationDTO;
import com.example.library.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
@DisplayName("Location Controller Tests")
class LocationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LocationService locationService;

	@Autowired
	private ObjectMapper objectMapper;

	private LocationDTO testLocationDTO;

	@BeforeEach
	void setUp() {
		testLocationDTO = new LocationDTO(1L, "Test Location", "123 Test St");
	}

	@Nested
	@DisplayName("GET /api/locations")
	class GetAllLocations {

		@Test
		@DisplayName("should return all locations")
		void shouldReturnAllLocations() throws Exception {
			Page<LocationDTO> locationPage = new PageImpl<>(Collections.singletonList(testLocationDTO));
			when(locationService.getLocations(any(), any(Pageable.class))).thenReturn(locationPage);

			mockMvc.perform(get("/api/locations"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.content[0].name").value("Test Location"));

			verify(locationService).getLocations(any(), any(Pageable.class));
		}

		@Test
		@DisplayName("should filter locations by name")
		void shouldFilterLocationsByName() throws Exception {
			Page<LocationDTO> locationPage = new PageImpl<>(Collections.singletonList(testLocationDTO));
			when(locationService.getLocations(any(), any(Pageable.class))).thenReturn(locationPage);

			mockMvc.perform(get("/api/locations")
							.param("name", "Test"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.content[0].name").value("Test Location"));

			verify(locationService).getLocations(any(), any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("GET /api/locations/{id}")
	class GetLocationById {

		@Test
		@DisplayName("should return location when found")
		void shouldReturnLocationWhenFound() throws Exception {
			when(locationService.getLocationById(1L)).thenReturn(Optional.of(testLocationDTO));

			mockMvc.perform(get("/api/locations/1"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.name").value("Test Location"));

			verify(locationService).getLocationById(1L);
		}

		@Test
		@DisplayName("should return 404 when location not found")
		void shouldReturn404WhenLocationNotFound() throws Exception {
			when(locationService.getLocationById(1L)).thenReturn(Optional.empty());

			mockMvc.perform(get("/api/locations/1"))
					.andExpect(status().isNotFound());

			verify(locationService).getLocationById(1L);
		}
	}

	@Nested
	@DisplayName("POST /api/locations")
	class CreateLocation {

		@Test
		@DisplayName("should create location successfully")
		void shouldCreateLocationSuccessfully() throws Exception {
			when(locationService.createLocation(any(LocationDTO.class))).thenReturn(testLocationDTO);

			mockMvc.perform(post("/api/locations")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testLocationDTO)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.name").value("Test Location"));

			verify(locationService).createLocation(any(LocationDTO.class));
		}
	}

	@Nested
	@DisplayName("PUT /api/locations/{id}")
	class UpdateLocation {

		@Test
		@DisplayName("should update location successfully")
		void shouldUpdateLocationSuccessfully() throws Exception {
			when(locationService.updateLocation(eq(1L), any(LocationDTO.class))).thenReturn(testLocationDTO);

			mockMvc.perform(put("/api/locations/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testLocationDTO)))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.name").value("Test Location"));

			verify(locationService).updateLocation(eq(1L), any(LocationDTO.class));
		}

		@Test
		@DisplayName("should return 404 when location not found")
		void shouldReturn404WhenLocationNotFound() throws Exception {
			when(locationService.updateLocation(eq(1L), any(LocationDTO.class)))
					.thenThrow(new LocationService.LocationNotFoundException(1L));

			mockMvc.perform(put("/api/locations/1")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(testLocationDTO)))
					.andExpect(status().isNotFound());

			verify(locationService).updateLocation(eq(1L), any(LocationDTO.class));
		}
	}

	@Nested
	@DisplayName("DELETE /api/locations/{id}")
	class DeleteLocation {

		@Test
		@DisplayName("should delete location successfully")
		void shouldDeleteLocationSuccessfully() throws Exception {
			doNothing().when(locationService).deleteLocation(1L);

			mockMvc.perform(delete("/api/locations/1"))
					.andExpect(status().isNoContent());

			verify(locationService).deleteLocation(1L);
		}

		@Test
		@DisplayName("should return 404 when location not found")
		void shouldReturn404WhenLocationNotFound() throws Exception {
			doThrow(new LocationService.LocationNotFoundException(1L)).when(locationService).deleteLocation(1L);

			mockMvc.perform(delete("/api/locations/1"))
					.andExpect(status().isNotFound());

			verify(locationService).deleteLocation(1L);
		}
	}
}