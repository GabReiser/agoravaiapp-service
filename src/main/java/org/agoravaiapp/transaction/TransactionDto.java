package org.agoravaiapp.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionDto(
        UUID id,
        String userId,
        UUID categoryId,
        String categoryName,
        String categoryIcon,
        String categoryColor,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDate date,
        String source,
        Instant createdAt) {

    public static TransactionDto from(Transaction t) {
        String categoryName = t.category != null ? t.category.name : null;
        String categoryIcon = t.category != null ? t.category.icon : null;
        String categoryColor = t.category != null ? t.category.color : null;
        UUID categoryId = t.category != null ? t.category.id : null;
        return new TransactionDto(
                t.id,
                t.userId,
                categoryId,
                categoryName,
                categoryIcon,
                categoryColor,
                t.description,
                t.amount,
                t.type,
                t.date,
                t.source,
                t.createdAt);
    }
}
