package com.example.library.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * A book copy in the library.
 */
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
	 * Creates a new book copy.
	 */
	public BookCopy() {
	}

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
	 * Returns the unique identifier of the book copy.
	 *
	 * @return the unique identifier of the book copy
	 */
	@NonNull
	public BookCopyId getId() {
		return this.id;
	}

	/**
	 * Returns the book that the copy belongs to.
	 *
	 * @return the book that the copy belongs to
	 */
	@NonNull
	public Book getBook() {
		return this.book;
	}

	/**
	 * Sets the book that the copy belongs to.
	 *
	 * @param book the book that the copy belongs to
	 */
	public void setBook(@NonNull Book book) {
		this.book = book;
		if (this.id == null) {
			this.id = new BookCopyId();
		}

		this.id.setBookId(book.getId());
	}

	/**
	 * Returns the location where the copy is stored.
	 *
	 * @return the location where the copy is stored
	 */
	@NonNull
	public Location getLocation() {
		return this.location;
	}

	/**
	 * Sets the location where the copy is stored.
	 *
	 * @param location the location where the copy is stored
	 */
	public void setLocation(@NonNull Location location) {
		this.location = location;
		if (this.id == null) {
			this.id = new BookCopyId();
		}
		this.id.setLocationId(location.getId());
	}

	/**
	 * Returns the quantity of the book copy.
	 *
	 * @return the quantity of the book copy
	 */
	@NonNull
	public Integer getQuantity() {
		return this.quantity;
	}

	/**
	 * Sets the quantity of the book copy.
	 *
	 * @param quantity the quantity of the book copy
	 */
	public void setQuantity(@NonNull Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * The primary key of the book copy.
	 */
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

		/**
		 * Creates a new book copy ID.
		 */
		public BookCopyId() {
		}

		/**
		 * Creates a new book copy ID with the given book and location IDs.
		 *
		 * @param bookId     the unique identifier of the book
		 * @param locationId the unique identifier of the location
		 */
		public BookCopyId(@NonNull Long bookId, @NonNull Long locationId) {
			this.bookId = bookId;
			this.locationId = locationId;
		}

		/**
		 * Returns the unique identifier of the book.
		 *
		 * @return the unique identifier of the book
		 */
		@NonNull
		public Long getBookId() {
			return this.bookId;
		}

		/**
		 * Sets the unique identifier of the book.
		 *
		 * @param bookId the unique identifier of the book
		 */
		public void setBookId(@NonNull Long bookId) {
			this.bookId = bookId;
		}

		/**
		 * Returns the unique identifier of the location.
		 *
		 * @return the unique identifier of the location
		 */
		@NonNull
		public Long getLocationId() {
			return this.locationId;
		}

		/**
		 * Sets the unique identifier of the location.
		 *
		 * @param locationId the unique identifier of the location
		 */
		public void setLocationId(@NonNull Long locationId) {
			this.locationId = locationId;
		}

		/**
		 * Compares this book copy ID to another object.
		 *
		 * @param o the object to compare to
		 * @return true if the objects are equal, false otherwise
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || this.getClass() != o.getClass()) return false;
			BookCopyId that = (BookCopyId) o;
			return this.bookId.equals(that.bookId) && this.locationId.equals(that.locationId);
		}

		/**
		 * Returns a hash code value for the book copy ID.
		 *
		 * @return a hash code value for the book copy ID
		 */
		@Override
		public int hashCode() {
			return this.bookId.hashCode() + this.locationId.hashCode();
		}

		/**
		 * Returns a string representation of the book copy ID.
		 *
		 * @return a string representation of the book copy ID
		 */
		@Override
		public String toString() {
			return "BookCopyId{" +
					"bookId=" + this.bookId +
					", locationId=" + this.locationId +
					'}';
		}
	}
}