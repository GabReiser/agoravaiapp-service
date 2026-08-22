package org.agoravaiapp.dashboard;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.agoravaiapp.dashboard.DashboardResponse.CashflowEntry;
import org.agoravaiapp.dashboard.DashboardResponse.CategoryValue;
import org.agoravaiapp.dashboard.DashboardResponse.Kpis;
import org.agoravaiapp.dashboard.DashboardResponse.Period;

@ApplicationScoped
public class DashboardService {

    private static final String[] PT_MONTHS = {
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez"
    };

    @Inject
    EntityManager em;

    @Transactional
    public DashboardResponse build(String userId, LocalDate from, LocalDate to) {
        return new DashboardResponse(
                new Period(from.toString(), to.toString()),
                buildKpis(userId, from, to),
                buildCashflow(userId, from, to),
                buildExpensesByCategory(userId, from, to));
    }

    private Kpis buildKpis(String userId, LocalDate from, LocalDate to) {
        Object[] row = (Object[]) em.createNativeQuery("""
                        SELECT
                            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0),
                            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0)
                        FROM core.transactions
                        WHERE user_id = :userId AND date BETWEEN :from AND :to
                        """)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();

        BigDecimal income = (BigDecimal) row[0];
        BigDecimal expense = (BigDecimal) row[1];
        BigDecimal balance = income.subtract(expense);
        double savingsRate = income.signum() > 0 ? balance.doubleValue() / income.doubleValue() : 0.0;
        return new Kpis(income, expense, balance, savingsRate);
    }

    @SuppressWarnings("unchecked")
    private List<CashflowEntry> buildCashflow(String userId, LocalDate from, LocalDate to) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT
                            to_char(date, 'YYYY-MM') AS month,
                            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) AS income,
                            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) AS expense
                        FROM core.transactions
                        WHERE user_id = :userId AND date BETWEEN :from AND :to
                        GROUP BY to_char(date, 'YYYY-MM')
                        ORDER BY month
                        """)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        return rows.stream()
                .map(r -> new CashflowEntry((String) r[0], monthLabel((String) r[0]), (BigDecimal) r[1], (BigDecimal) r[2]))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<CategoryValue> buildExpensesByCategory(String userId, LocalDate from, LocalDate to) {
        List<Object[]> rows = em.createNativeQuery("""
                        SELECT c.name, COALESCE(SUM(t.amount), 0) AS value
                        FROM core.transactions t
                        JOIN core.categories c ON c.id = t.category_id
                        WHERE t.user_id = :userId AND t.type = 'EXPENSE' AND t.date BETWEEN :from AND :to
                        GROUP BY c.name
                        ORDER BY value DESC
                        """)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();

        return rows.stream()
                .map(r -> new CategoryValue((String) r[0], (BigDecimal) r[1]))
                .toList();
    }

    private static String monthLabel(String yearMonth) {
        // yearMonth no formato "yyyy-MM"
        int month = Integer.parseInt(yearMonth.substring(5, 7));
        return PT_MONTHS[month - 1];
    }
}
