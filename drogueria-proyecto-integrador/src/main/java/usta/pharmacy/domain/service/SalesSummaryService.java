package usta.pharmacy.domain.service;

import usta.pharmacy.domain.model.MonthlySummary;
import usta.pharmacy.domain.model.Sale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Servicio de dominio: calcula cuánto se vendió en un mes a partir de sus ventas. */
public class SalesSummaryService {

    public MonthlySummary summarize(YearMonth month, List<Sale> sales) {
        BigDecimal total = sales.stream()
                .map(Sale::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        int units = sales.stream().mapToInt(Sale::getQuantity).sum();

        Map<String, Integer> unitsByProduct = new HashMap<>();
        for (Sale sale : sales) {
            unitsByProduct.merge(sale.getProduct().getName(), sale.getQuantity(), Integer::sum);
        }
        String top = unitsByProduct.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin ventas");

        return new MonthlySummary(month, total, units, sales.size(), top);
    }
}