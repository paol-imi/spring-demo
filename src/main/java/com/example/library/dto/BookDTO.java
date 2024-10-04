package com.example.library.dto;

import com.example.library.validator.ISBN;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * A DTO representing a book.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BookDTO {
    /**
     * The unique identifier of the book.
     */
    public Long id;

    /**
     * The title of the book.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    public String title;

    /**
     * The author of the book.
     */
    @NotNull
    @Size(min = 1, max = 255, message = "Author must be between 1 and 255 characters")
    public String author;

    /**
     * The ISBN of the book.
     */
    @NotNull
    @ISBN(message = "Invalid ISBN format")
    public String isbn;

    /**
     * The publication date of the book.
     */
    @NotNull
    @Past(message = "Publication date must be in the past")
    public LocalDate publicationDate;
}
