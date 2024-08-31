package com.example.library.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation for validating ISBNs.
 */
@Documented
@Constraint(validatedBy = ISBNValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ISBN {
	String message() default "ISBN is not valid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	/**
	 * The type of ISBN to validate, default is ANY.
	 */
	Type type() default Type.ANY;

	/**
	 * The type of ISBN to validate.
	 */
	enum Type {
		ISBN_10,
		ISBN_13,
		ANY
	}
}
