package org.agoravaiapp.transaction;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepository implements PanacheRepositoryBase<Transaction, UUID> {

    public List<Transaction> findByUser(String userId, LocalDate from, LocalDate to, int page, int size) {
        return find(query(from, to), sort(), params(userId, from, to))
                .page(page, size)
                .list();
    }

    public long countByUser(String userId, LocalDate from, LocalDate to) {
        return find(query(from, to), params(userId, from, to)).count();
    }

    private static String query(LocalDate from, LocalDate to) {
        StringBuilder sb = new StringBuilder("userId = :userId");
        if (from != null) {
            sb.append(" and date >= :from");
        }
        if (to != null) {
            sb.append(" and date <= :to");
        }
        return sb.toString();
    }

    private static Sort sort() {
        return Sort.descending("date").and("createdAt");
    }

    private static Map<String, Object> params(String userId, LocalDate from, LocalDate to) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (from != null) {
            params.put("from", from);
        }
        if (to != null) {
            params.put("to", to);
        }
        return params;
    }
}
