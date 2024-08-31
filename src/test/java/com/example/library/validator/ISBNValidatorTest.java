package com.example.library.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@DisplayName("ISBNValidator Tests")
class ISBNValidatorTest {

	private ISBNValidator validator;
	private ConstraintValidatorContext context;

	@BeforeEach
	void setUp() {
		validator = new ISBNValidator();
		context = mock(ConstraintValidatorContext.class);
	}

	@Test
	@DisplayName("should return true for null ISBN")
	void shouldReturnTrueForNullISBN() {
		assertThat(validator.isValid(null, context)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"1234567890",    // 10-digit ISBN
			"1234567890123", // 13-digit ISBN
			"0-306-40615-2", // 10-digit ISBN with hyphens
			"978-3-16-148410-0" // 13-digit ISBN with hyphens
	})
	@DisplayName("should return true for valid ISBN")
	void shouldReturnTrueForValidISBN(String isbn) {
		assertThat(validator.isValid(isbn, context)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"123456789",     // 9 digits
			"12345678901",   // 11 digits
			"123456789012",  // 12 digits
			"12345678901234", // 14 digits
			"abcdefghij",    // 10 non-numeric characters
			"abc-def-ghi-j", // 10 non-numeric characters with hyphens
	})
	@DisplayName("should return false for invalid ISBN")
	void shouldReturnFalseForInvalidISBN(String isbn) {
		assertThat(validator.isValid(isbn, context)).isFalse();
	}
}