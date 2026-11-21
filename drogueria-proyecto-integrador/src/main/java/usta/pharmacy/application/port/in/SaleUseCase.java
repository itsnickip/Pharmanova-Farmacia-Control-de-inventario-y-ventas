package usta.pharmacy.application.port.in;

import usta.pharmacy.domain.model.MonthlySummary;
import usta.pharmacy.domain.model.Sale;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Puerto de entrada: casos de uso de ventas. */
public interface SaleUseCase {

    /** Registra la venta y descuenta el stock del producto. */
    Sale register(Long productId, int quantity, LocalDate saleDate);

    List<Sale> listByMonth(YearMonth month);

    MonthlySummary summarize(YearMonth month);
}