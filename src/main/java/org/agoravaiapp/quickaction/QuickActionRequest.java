package org.agoravaiapp.quickaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record QuickActionRequest(
        @NotBlank(message = "label e obrigatorio") String label,
        String icon,
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero") BigDecimal amount,
        String category) {
}
