package usta.pharmacy.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/** Resumen de ventas de un mes: responde "¿cuánto vendió la droguería en el mes?". */
public class MonthlySummary {

    private final YearMonth month;
    private final BigDecimal totalAmount;
    private final int unitsSold;
    private final int salesCount;
    private final String topProduct;

    public MonthlySummary(YearMonth month, BigDecimal totalAmount, int unitsSold,
                          int salesCount, String topProduct) {
        this.month = month;
        this.totalAmount = totalAmount;
        this.unitsSold = unitsSold;
        this.salesCount = salesCount;
        this.topProduct = topProduct;
    }

    public YearMonth getMonth() { return month; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public int getUnitsSold() { return unitsSold; }
    public int getSalesCount() { return salesCount; }
    public String getTopProduct() { return topProduct; }
}