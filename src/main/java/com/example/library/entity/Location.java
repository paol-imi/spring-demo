package com.example.library.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A location where books are stored.
 */
@Entity
@Table(name = "locations", indexes = {
		// Index to allow searching for books by title.
		@Index(name = "idx_location_name", columnList = "name"),
})
public class Location {
	/**
	 * The book copies stored at the location.
	 */
	@OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<BookCopy> bookCopies = new HashSet<>();
	/**
	 * The unique identifier of the location.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * The name of the location.
	 */
	@Column(nullable = false, unique = true)
	private String name;
	/**
	 * The address of the location.
	 */
	@Column(nullable = false)
	private String address;

	/**
	 * Creates a new location.
	 */
	public Location() {
	}

	/**
	 * Creates a new location with the given name and address.
	 *
	 * @param id      the unique identifier of the location
	 * @param name    the name of the location
	 * @param address the address of the location
	 */
	public Location(Long id, @NonNull String name, @NonNull String address) {
		this.id = id;
		this.name = name;
		this.address = address;
	}

	/**
	 * Returns the unique identifier of the location.
	 *
	 * @return the unique identifier of the location
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the unique identifier of the location.
	 *
	 * @param id the unique identifier of the location
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the location.
	 *
	 * @return the name of the location
	 */
	@NonNull
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the location.
	 *
	 * @param name the name of the location
	 */
	public void setName(@NonNull String name) {
		this.name = name;
	}

	/**
	 * Returns the address of the location.
	 *
	 * @return the address of the location
	 */
	@NonNull
	public String getAddress() {
		return this.address;
	}

	/**
	 * Sets the address of the location.
	 *
	 * @param address the address of the location
	 */
	public void setAddress(@NonNull String address) {
		this.address = address;
	}

	/**
	 * Returns the book copies stored at the location.
	 *
	 * @return the book copies stored at the location
	 */
	@NonNull
	public Set<BookCopy> getBookCopies() {
		return Collections.unmodifiableSet(this.bookCopies);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param o the reference object with which to compare
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		Location location = (Location) o;
		return Objects.equals(this.id, location.id) &&
				Objects.equals(this.name, location.name) &&
				Objects.equals(this.address, location.address);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.address);
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object
	 */
	@Override
	public String toString() {
		return "Location{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", address='" + this.address + '\'' +
				'}';
	}
}