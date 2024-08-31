package com.example.library.repository;

import com.example.library.dto.BookWithQuantityDTO;
import com.example.library.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for the BookCopy entity.
 */
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, BookCopy.BookCopyId>, JpaSpecificationExecutor<BookCopy> {
	/**
	 * Find the quantity of a book at a location.
	 *
	 * @param locationId the id of the location
	 * @param bookId     the id of the book
	 * @return the quantity of the book at the location
	 */
	@Query("SELECT bc.quantity FROM BookCopy bc WHERE bc.id.locationId = :locationId AND bc.id.bookId = :bookId")
	Integer findBookQuantityByLocationIdAndBookId(@Param("locationId") Long locationId, @Param("bookId") Long bookId);

	/**
	 * Find the books with quantities at a location.
	 *
	 * @param locationId the id of the location
	 * @param pageable   the pageable object
	 * @return the books with quantities at the location
	 */
	@Query("SELECT new com.example.library.dto.BookWithQuantityDTO(b.title, b.author, b.isbn, bc.quantity) " +
			"FROM BookCopy bc JOIN bc.book b WHERE bc.id.locationId = :locationId")
	Page<BookWithQuantityDTO> findBooksWithQuantitiesByLocationId(@Param("locationId") Long locationId, Pageable pageable);
}
