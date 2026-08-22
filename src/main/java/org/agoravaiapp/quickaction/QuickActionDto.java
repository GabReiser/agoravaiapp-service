package org.agoravaiapp.quickaction;

import java.math.BigDecimal;
import java.util.UUID;

public record QuickActionDto(UUID id, String userId, String label, String icon, BigDecimal amount, String category) {

    public static QuickActionDto from(QuickAction q) {
        return new QuickActionDto(q.id, q.userId, q.label, q.icon, q.amount, q.category);
    }
}
