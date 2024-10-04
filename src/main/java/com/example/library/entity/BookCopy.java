package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * A book copy in the library.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "book_copies")
public class BookCopy {
    /**
     * The unique identifier of the book copy.
     */
    @EmbeddedId
    private BookCopyId id;

    /**
     * The book that the copy belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    /**
     * The location where the copy is stored.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("locationId")
    @JoinColumn(name = "location_id")
    private Location location;

    /**
     * The quantity of the book copy.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Creates a new book copy with the given book, location, and quantity.
     *
     * @param book     the book that the copy belongs to
     * @param location the location where the copy is stored
     * @param quantity the quantity of the book copy
     */
    public BookCopy(@NonNull Book book, @NonNull Location location, @NonNull Integer quantity) {
        this.id = new BookCopyId(book.getId(), location.getId());
        this.book = book;
        this.location = location;
        this.quantity = quantity;
    }

    /**
     * The primary key of the book copy.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    @Embeddable
    public static class BookCopyId implements Serializable {
        /**
         * The unique identifier of the book.
         */
        @Column(name = "book_id")
        private Long bookId;

        /**
         * The unique identifier of the location.
         */
        @Column(name = "location_id")
        private Long locationId;
    }
}