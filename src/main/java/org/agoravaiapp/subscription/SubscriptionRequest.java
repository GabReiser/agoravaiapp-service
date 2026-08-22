package org.agoravaiapp.subscription;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SubscriptionRequest(
        @NotBlank(message = "name e obrigatorio") String name,
        String category,
        @NotNull(message = "amount e obrigatorio")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero") BigDecimal amount,
        @Min(value = 1, message = "billingDay deve estar entre 1 e 31")
        @Max(value = 31, message = "billingDay deve estar entre 1 e 31") Integer billingDay,
        Boolean active) {
}
