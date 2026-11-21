package usta.pharmacy.application.port.out;

import usta.pharmacy.domain.model.Sale;

import java.time.LocalDate;
import java.util.List;

/** Puerto de salida: lo que el sistema necesita para guardar y consultar ventas. */
public interface SaleRepositoryPort {

    /**
     * Guarda la venta y el nuevo stock del producto en una sola operación atómica
     * (o se guardan las dos cosas, o ninguna).
     */
    Sale saveAndUpdateStock(Sale sale);

    List<Sale> findBetween(LocalDate from, LocalDate to);
}