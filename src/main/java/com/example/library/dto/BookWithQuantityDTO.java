package com.example.library.dto;

import com.example.library.lib.NullabilityValidationGroups.NotNullField;
import com.example.library.validator.ISBN;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * A DTO representing a book with a quantity.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BookWithQuantityDTO {
    /**
     * The title of the book.
     */
    @NotNull(groups = NotNullField.class)
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    /**
     * The author of the book.
     */
    @NotNull(groups = NotNullField.class)
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    private String author;

    /**
     * The ISBN of the book.
     */
    @NotNull(groups = NotNullField.class)
    @ISBN(message = "Invalid ISBN format")
    private String isbn;

    /**
     * The quantity of the book in the library.
     */
    @Min(0)
    private int quantity;
}
