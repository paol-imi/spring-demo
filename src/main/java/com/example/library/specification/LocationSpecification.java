package com.example.library.specification;

import com.example.library.entity.Location;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

/**
 * Defines specifications for querying locations.
 */
public interface LocationSpecification extends Specification<Location> {
	/**
	 * Create a specification that matches locations with a name containing the given string.
	 *
	 * @param name the name to search for
	 * @return a specification that matches locations with a name containing the given string
	 */
	static @Nullable LocationSpecification nameLike(@Nullable String name) {
		if (name == null) {
			return null;
		} else {
			return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
		}
	}
}