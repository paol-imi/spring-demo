package com.example.library.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for the ISBN annotation.
 */
public class ISBNValidator implements ConstraintValidator<ISBN, String> {
	/**
	 * Pattern to match any character that is not a digit or 'X'.
	 */
	private static final Pattern NOT_DIGITS_OR_NOT_X = Pattern.compile("[^\\dX]");
	/**
	 * The ISBN validation algorithm to use.
	 */
	private ISBN.Type type = ISBN.Type.ANY;

	/**
	 * Create a new ISBNValidator.
	 */
	public ISBNValidator() {
	}

	private static boolean checkChecksumISBN10(String isbn) {
		int sum = 0;

		for (int i = 0; i < isbn.length() - 1; ++i) {
			sum += (isbn.charAt(i) - 48) * (10 - i);
		}

		sum += isbn.charAt(9) == 'X' ? 10 : isbn.charAt(9) - 48;
		return sum % 11 == 0;
	}

	private static boolean checkChecksumISBN13(String isbn) {
		int sum = 0;

		for (int i = 0; i < isbn.length(); ++i) {
			sum += (isbn.charAt(i) - 48) * (i % 2 == 0 ? 1 : 3);
		}

		return sum % 10 == 0;
	}

	/**
	 * Initialize the validator with the ISBN annotation.
	 *
	 * @param constraintAnnotation the ISBN annotation
	 */
	public void initialize(ISBN constraintAnnotation) {
		this.type = constraintAnnotation.type();
	}

	/**
	 * Validate the ISBN.
	 *
	 * @param isbn    the ISBN to validate
	 * @param context the validation context
	 * @return true if the ISBN is valid, false otherwise
	 */
	public boolean isValid(String isbn, ConstraintValidatorContext context) {
		if (isbn == null) {
			return true;
		} else {
			String digits = NOT_DIGITS_OR_NOT_X.matcher(isbn).replaceAll("");

			return switch (this.type) {
				case ISBN_10 -> digits.length() == 10;
				case ISBN_13 -> digits.length() == 13;
				default -> digits.length() == 10 || digits.length() == 13;
			};
		}
	}
}
