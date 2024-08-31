package com.example.library.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookDTO Validation Tests")
class BookDTOTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("Should validate valid BookDTO")
	void shouldValidateValidBookDTO() {
		BookDTO bookDTO = new BookDTO(1L, "Valid Title", "Valid Author", "1234567890", LocalDate.now().minusDays(1));
		assertThat(validator.validate(bookDTO)).isEmpty();
	}

	@Test
	@DisplayName("Should not validate BookDTO with null title")
	void shouldNotValidateBookDTOWithNullTitle() {
		BookDTO bookDTO = new BookDTO(1L, null, "Valid Author", "1234567890", LocalDate.now().minusDays(1));
		assertThat(validator.validate(bookDTO)).isNotEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {""})
	@DisplayName("Should not validate BookDTO with blank title")
	void shouldNotValidateBookDTOWithBlankTitle(String title) {
		BookDTO bookDTO = new BookDTO(1L, title, "Valid Author", "1234567890", LocalDate.now().minusDays(1));
		assertThat(validator.validate(bookDTO)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate BookDTO with null author")
	void shouldNotValidateBookDTOWithNullAuthor() {
		BookDTO bookDTO = new BookDTO(1L, "Valid Title", null, "1234567890", LocalDate.now().minusDays(1));
		assertThat(validator.validate(bookDTO)).isNotEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {""})
	@DisplayName("Should not validate BookDTO with blank author")
	void shouldNotValidateBookDTOWithBlankAuthor(String author) {
		BookDTO bookDTO = new BookDTO(1L, "Valid Title", author, "1234567890", LocalDate.now().minusDays(1));
		assertThat(validator.validate(bookDTO)).isNotEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {"123456789", "12345678901", "123456789012"})
	@DisplayName("Should not validate BookDTO with invalid ISBN")
	void shouldNotValidateBookDTOWithInvalidISBN(String isbn) {
		BookDTO bookDTO = new BookDTO(1L, "Valid Title", "Valid Author", isbn, LocalDate.now().minusDays(1));
		assertThat(validator.validate(bookDTO)).isNotEmpty();
	}

	@Test
	@DisplayName("Should not validate BookDTO with future publication date")
	void shouldNotValidateBookDTOWithFuturePublicationDate() {
		BookDTO bookDTO = new BookDTO(1L, "Valid Title", "Valid Author", "1234567890", LocalDate.now().plusDays(1));
		assertThat(validator.validate(bookDTO)).isNotEmpty();
	}
}