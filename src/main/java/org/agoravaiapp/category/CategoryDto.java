package org.agoravaiapp.category;

import java.util.UUID;

public record CategoryDto(UUID id, String name, String icon, String color, boolean system) {

    public static CategoryDto from(Category category) {
        return new CategoryDto(category.id, category.name, category.icon, category.color, category.system);
    }
}
