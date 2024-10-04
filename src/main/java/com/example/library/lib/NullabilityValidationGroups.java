package com.example.library.lib;

import jakarta.validation.groups.Default;

public class NullabilityValidationGroups {
    public interface Create extends Default, NullId, NotNullField {
    }

    public interface Update extends Default, NotNullId, NotNullField {
    }

    public interface PartialUpdate extends Default, NotNullId {
    }

    public interface NullId {
    }

    public interface NotNullId {
    }

    public interface NotNullField {
    }
}

/*
 * @Getter
 * @Setter
 * @AllArgsConstructor
 * @ToString
 * @EqualsAndHashCode
 * public class LocationDTO {
 *   @NotNull(groups = NotNullId.class)
 *   @Null(groups = NullId.class)
 *   private Long id;
 *
 *   @NotNull(groups = NotNullField.class)
 *   @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
 *   private String name;
 *
 *   @NotNull(groups = NotNullField.class)
 *   @Size(min = 1, max = 255, message = "Address must be between 1 and 255 characters")
 *   private String address;
 * }
 */