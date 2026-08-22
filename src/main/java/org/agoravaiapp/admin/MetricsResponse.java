package org.agoravaiapp.admin;

import java.math.BigDecimal;

public record MetricsResponse(
        long transactions,
        long subscriptions,
        long activeSubscriptions,
        long users,
        BigDecimal incomeTotal,
        BigDecimal expenseTotal) {
}
