package org.agoravaiapp.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        Period period,
        Kpis kpis,
        List<CashflowEntry> cashflow,
        List<CategoryValue> expensesByCategory) {

    public record Period(String from, String to) {
    }

    public record Kpis(BigDecimal income, BigDecimal expense, BigDecimal balance, double savingsRate) {
    }

    public record CashflowEntry(String month, String label, BigDecimal income, BigDecimal expense) {
    }

    public record CategoryValue(String category, BigDecimal value) {
    }
}
