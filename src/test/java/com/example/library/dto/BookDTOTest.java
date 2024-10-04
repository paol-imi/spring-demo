package com.example.library.dto;

import com.example.library.lib.NullabilityValidationGroups.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookDTO Test Suite")
class BookDTOTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Nested
	@DisplayName("Validation Tests")
	class ValidationTests {

		@Nested
		@DisplayName("Create Operation Tests")
		class CreateOperationTests {

			@Test
			@DisplayName("Valid BookDTO for Create operation")
			void testValidBookDTOForCreate() {
				BookDTO bookDTO = new BookDTO(null, "Title", "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1));
				Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDTO, Create.class);
				assertTrue(violations.isEmpty());
			}

			@ParameterizedTest
			@MethodSource("invalidCreateFieldsProvider")
			@DisplayName("Invalid fields for Create operation")
			void testInvalidFieldsForCreate(BookDTO bookDTO, String expectedViolation) {
				Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDTO, Create.class);
				assertFalse(violations.isEmpty());
				assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedViolation)));
			}

			private static Stream<Arguments> invalidCreateFieldsProvider() {
				return Stream.of(
						Arguments.of(new BookDTO(1L, "Title", "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1)), "must be null"),
						Arguments.of(new BookDTO(null, null, "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(null, "Title", null, "978-3-16-148410-0", LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(null, "Title", "Author", null, LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(null, "Title", "Author", "978-3-16-148410-0", null), "must not be null")
				);
			}
		}

		@Nested
		@DisplayName("Update Operation Tests")
		class UpdateOperationTests {

			@Test
			@DisplayName("Valid BookDTO for Update operation")
			void testValidBookDTOForUpdate() {
				BookDTO bookDTO = new BookDTO(1L, "Title", "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1));
				Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDTO, Update.class);
				assertTrue(violations.isEmpty());
			}

			@ParameterizedTest
			@MethodSource("invalidUpdateFieldsProvider")
			@DisplayName("Invalid fields for Update operation")
			void testInvalidFieldsForUpdate(BookDTO bookDTO, String expectedViolation) {
				Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDTO, Update.class);
				assertFalse(violations.isEmpty());
				assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals(expectedViolation)));
			}

			private static Stream<Arguments> invalidUpdateFieldsProvider() {
				return Stream.of(
						Arguments.of(new BookDTO(null, "Title", "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(1L, null, "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(1L, "Title", null, "978-3-16-148410-0", LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(1L, "Title", "Author", null, LocalDate.now().minusDays(1)), "must not be null"),
						Arguments.of(new BookDTO(1L, "Title", "Author", "978-3-16-148410-0", null), "must not be null")
				);
			}
		}

		@Nested
		@DisplayName("General Field Validation Tests")
		class GeneralFieldValidationTests {

			@ParameterizedTest
			@MethodSource("invalidFieldsProvider")
			@DisplayName("Invalid fields validation")
			void testInvalidFields(BookDTO bookDTO, String expectedViolation) {
				Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDTO);
				assertFalse(violations.isEmpty());
				boolean b = violations.stream().anyMatch(v -> v.getMessage().equals(expectedViolation));
				assertTrue(b);
			}

			private static Stream<Arguments> invalidFieldsProvider() {
				return Stream.of(
						Arguments.of(new BookDTO(1L, "", "Author", "978-3-16-148410-0", LocalDate.now().minusDays(1)), "Title must be between 1 and 255 characters"),
						Arguments.of(new BookDTO(1L, "Title", "", "978-3-16-148410-0", LocalDate.now().minusDays(1)), "Author must be between 1 and 255 characters"),
						Arguments.of(new BookDTO(1L, "Title", "Author", "invalid-isbn", LocalDate.now().minusDays(1)), "Invalid ISBN format"),
						Arguments.of(new BookDTO(1L, "Title", "Author", "978-3-16-148410-0", LocalDate.now().plusDays(1)), "Publication date must be in the past")
				);
			}
		}

		@Nested
		@DisplayName("Combined Validation Tests")
		class CombinedValidationTests {

			@Test
			@DisplayName("Validation with both operation and field constraints")
			void testCombinedValidation() {
				BookDTO bookDTO = new BookDTO(1L, "", "Author", "invalid-isbn", LocalDate.now().plusDays(1));
				Set<ConstraintViolation<BookDTO>> violations = validator.validate(bookDTO, Update.class);
				assertFalse(violations.isEmpty());
				assertEquals(3, violations.size());
				assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Title must be between 1 and 255 characters")));
				assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid ISBN format")));
				assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Publication date must be in the past")));
			}
		}
	}
}