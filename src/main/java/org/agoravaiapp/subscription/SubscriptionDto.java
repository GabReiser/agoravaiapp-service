package org.agoravaiapp.subscription;

import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionDto(
        UUID id,
        String userId,
        String name,
        String category,
        BigDecimal amount,
        int billingDay,
        boolean active) {

    public static SubscriptionDto from(Subscription s) {
        return new SubscriptionDto(s.id, s.userId, s.name, s.category, s.amount, s.billingDay, s.active);
    }
}
