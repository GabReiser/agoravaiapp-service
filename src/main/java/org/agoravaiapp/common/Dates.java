package org.agoravaiapp.common;

import jakarta.ws.rs.BadRequestException;
import java.time.LocalDate;

public final class Dates {

    private Dates() {
    }

    /** Converte um query param de data ISO ({@code yyyy-MM-dd}). {@code null} para vazio. */
    public static LocalDate parse(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (RuntimeException e) {
            throw new BadRequestException("Valor invalido para " + field + ": " + value);
        }
    }
}
