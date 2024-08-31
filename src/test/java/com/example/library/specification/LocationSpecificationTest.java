package com.example.library.specification;

import com.example.library.entity.Location;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("LocationSpecification Tests")
class LocationSpecificationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("nameLike should filter locations by name")
	void nameLikeShouldFilterLocationsByName() {
		Location location1 = new Location(null, "Central Library", "123 Main St");
		Location location2 = new Location(null, "East Branch", "456 Oak Ave");
		entityManager.persist(location1);
		entityManager.persist(location2);
		entityManager.flush();

		CriteriaBuilder cb = entityManager.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Location> query = cb.createQuery(Location.class);
		Root<Location> root = query.from(Location.class);

		query.where(LocationSpecification.nameLike("Central").toPredicate(root, query, cb));

		List<Location> result = entityManager.getEntityManager().createQuery(query).getResultList();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("Central Library");
	}
}