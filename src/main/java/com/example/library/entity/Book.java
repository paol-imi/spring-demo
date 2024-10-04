package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a book in the library system.
 * This entity is mapped to the 'books' table in the database.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "books", indexes = {
        // Index to allow searching for books by title.
        @Index(name = "idx_book_title", columnList = "title"),
        // Index to allow searching for books by author, or by author and title. The composite index is built with
        // the author column first, as it is more selective than the title column.
        @Index(name = "idx_book_author_title", columnList = "author, title")
})
public class Book extends Auditable {
    /**
     * The locations where the book is available.
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<BookCopy> bookCopies = new HashSet<>();
    /**
     * The unique identifier of the book.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The title of the book.
     */
    @Column(nullable = false)
    private String title;
    /**
     * The author of the book.
     */
    @Column(nullable = false)
    private String author;
    /**
     * The ISBN of the book.
     */
    @Column(nullable = false, unique = true)
    private String isbn;
    /**
     * The publication date of the book (based on the UTC timezone).
     */
    @Column(nullable = false)
    private LocalDate publicationDate;
}