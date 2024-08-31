package com.example.library.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LocationDTO Validation Tests")
class LocationDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("Should validate valid LocationDTO")
	void shouldValidateValidLocationDTO() {
		LocationDTO locationDTO = new LocationDTO(1L, "Valid Name", "Valid Address");
		assertThat(validator.validate(locationDTO)).isEmpty();
	}

	@Test
	@DisplayName("Should not validate LocationDTO with null name")
	void shouldNotValidateLocationDTOWithNullName() {
		LocationDTO locationDTO = new LocationDTO(1L, null, "Valid Address");
		assertThat(validator.validate(locationDTO)).isNotEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {""})
	@DisplayName("Should not validate LocationDTO with blank name")
	void shouldNotValidateLocationDTOWithBlankName(String name) {
		LocationDTO locationDTO = new LocationDTO(1L, name, "Valid Address");
		assertThat(validator.validate(locationDTO)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate LocationDTO with null address")
	void shouldNotValidateLocationDTOWithNullAddress() {
		LocationDTO locationDTO = new LocationDTO(1L, "Valid Name", null);
		assertThat(validator.validate(locationDTO)).isNotEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {""})
	@DisplayName("Should not validate LocationDTO with blank address")
	void shouldNotValidateLocationDTOWithBlankAddress(String address) {
		LocationDTO locationDTO = new LocationDTO(1L, "Valid Name", address);
		assertThat(validator.validate(locationDTO)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate LocationDTO with name exceeding max length")
	void shouldNotValidateLocationDTOWithNameExceedingMaxLength() {
		String longName = "a".repeat(256);
		LocationDTO locationDTO = new LocationDTO(1L, longName, "Valid Address");
		assertThat(validator.validate(locationDTO)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate LocationDTO with address exceeding max length")
	void shouldNotValidateLocationDTOWithAddressExceedingMaxLength() {
		String longAddress = "a".repeat(256);
		LocationDTO locationDTO = new LocationDTO(1L, "Valid Name", longAddress);
		assertThat(validator.validate(locationDTO)).isNotEmpty();
	}
}