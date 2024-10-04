package com.example.library.integration;

import com.example.library.dto.LocationDTO;
import com.example.library.entity.Location;
import com.example.library.repository.LocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("integration")
public class LocationIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private Location testLocation;

	@BeforeEach
	void setUp() {
		testLocation = new Location(null, "Test Location", "Test Address");
		testLocation = locationRepository.save(testLocation);
	}

	@AfterEach
	void tearDown() {
		locationRepository.deleteAll();
	}

	@Test
	void testGetAllLocations() throws Exception {
		mockMvc.perform(get("/api/locations"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value("Test Location"));
	}

	@Test
	void testGetLocationById() throws Exception {
		mockMvc.perform(get("/api/locations/" + testLocation.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test Location"));
	}

	@Test
	void testCreateLocation() throws Exception {
		LocationDTO newLocation = new LocationDTO(null, "New Location", "New Address");

		mockMvc.perform(post("/api/locations")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newLocation)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("New Location"));
	}

	@Test
	void testUpdateLocation() throws Exception {
		LocationDTO updatedLocation = new LocationDTO(testLocation.getId(), "Updated Location", "Updated Address");

		mockMvc.perform(put("/api/locations/" + testLocation.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedLocation)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Location"));
	}

	@Test
	void testDeleteLocation() throws Exception {
		mockMvc.perform(delete("/api/locations/" + testLocation.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/locations/" + testLocation.getId()))
				.andExpect(status().isNotFound());
	}
}