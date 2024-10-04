package com.example.library.mapper;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Book toEntity(BookDTO dto);

    BookDTO toDto(Book entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Book updateBook(BookDTO dto, @MappingTarget Book entity);
}
