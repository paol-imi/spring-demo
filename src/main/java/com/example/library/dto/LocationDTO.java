package com.example.library.dto;

import com.example.library.lib.NullabilityValidationGroups.NotNullField;
import com.example.library.lib.NullabilityValidationGroups.NotNullId;
import com.example.library.lib.NullabilityValidationGroups.NullId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * A location where books are stored.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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
}