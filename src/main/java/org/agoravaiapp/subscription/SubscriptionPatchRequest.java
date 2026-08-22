package org.agoravaiapp.subscription;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record SubscriptionPatchRequest(
        String name,
        String category,
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero") BigDecimal amount,
        @Min(value = 1, message = "billingDay deve estar entre 1 e 31")
        @Max(value = 31, message = "billingDay deve estar entre 1 e 31") Integer billingDay,
        Boolean active) {
}
