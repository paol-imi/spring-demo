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

@DisplayName("BookWithQuantityDTO Validation Tests")
class BookWithQuantityDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("Should validate valid BookWithQuantityDTO")
	void shouldValidateValidBookWithQuantityDTO() {
		BookWithQuantityDTO dto = new BookWithQuantityDTO("Valid Title", "Valid Author", "1234567890", 5);
		assertThat(validator.validate(dto)).isEmpty();
	}

	@Test
	@DisplayName("Should not validate BookWithQuantityDTO with null title")
	void shouldNotValidateBookWithQuantityDTOWithNullTitle() {
		BookWithQuantityDTO dto = new BookWithQuantityDTO(null, "Valid Author", "1234567890", 5);
		assertThat(validator.validate(dto)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate BookWithQuantityDTO with null author")
	void shouldNotValidateBookWithQuantityDTOWithNullAuthor() {
		BookWithQuantityDTO dto = new BookWithQuantityDTO("Valid Title", null, "1234567890", 5);
		assertThat(validator.validate(dto)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate BookWithQuantityDTO with null ISBN")
	void shouldNotValidateBookWithQuantityDTOWithNullISBN() {
		BookWithQuantityDTO dto = new BookWithQuantityDTO("Valid Title", "Valid Author", null, 5);
		assertThat(validator.validate(dto)).isNotEmpty();
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, -5, -100})
	@DisplayName("Should not validate BookWithQuantityDTO with negative quantity")
	void shouldNotValidateBookWithQuantityDTOWithNegativeQuantity(int quantity) {
		BookWithQuantityDTO dto = new BookWithQuantityDTO("Valid Title", "Valid Author", "1234567890", quantity);
		assertThat(validator.validate(dto)).isNotEmpty();
	}
}