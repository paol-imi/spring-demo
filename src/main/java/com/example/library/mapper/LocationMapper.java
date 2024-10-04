package com.example.library.mapper;

import com.example.library.dto.LocationDTO;
import com.example.library.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Location toEntity(LocationDTO dto);

    LocationDTO toDto(Location entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    @IgnoreAuditFields
    Location updateLocation(LocationDTO dto, @MappingTarget Location entity);
}