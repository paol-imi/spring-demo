package com.example.library.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * A location where books are stored.
 */
public class LocationDTO {
	/**
	 * The unique identifier of the location.
	 */
	private Long id;

	/**
	 * The name of the location.
	 */
	@NotNull
	@Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
	private String name;

	/**
	 * The address of the location.
	 */
	@NotNull
	@Size(min = 1, max = 255, message = "Address must be between 1 and 255 characters")
	private String address;

	/**
	 * Creates a new location DTO.
	 */
	public LocationDTO() {
	}

	/**
	 * Creates a new location with the given name and address.
	 *
	 * @param id      the unique identifier of the location
	 * @param name    the name of the location
	 * @param address the address of the location
	 */
	public LocationDTO(Long id, String name, String address) {
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
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the location.
	 *
	 * @param name the name of the location
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the address of the location.
	 *
	 * @return the address of the location
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Sets the address of the location.
	 *
	 * @param address the address of the location
	 */
	public void setAddress(String address) {
		this.address = address;
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

		LocationDTO locationDTO = (LocationDTO) o;
		return Objects.equals(this.id, locationDTO.id) &&
				Objects.equals(this.name, locationDTO.name) &&
				Objects.equals(this.address, locationDTO.address);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object
	 */
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.address);
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object
	 */
	public String toString() {
		return "Location{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", address='" + this.address + '\'' +
				'}';
	}
}