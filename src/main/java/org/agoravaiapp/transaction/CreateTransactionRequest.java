package org.agoravaiapp.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull(message = "categoryId e obrigatorio") UUID categoryId,
        String description,
        @NotNull(message = "amount e obrigatorio")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero") BigDecimal amount,
        @NotNull(message = "type e obrigatorio") TransactionType type,
        @NotNull(message = "date e obrigatoria") LocalDate date,
        String source) {
}
