package usta.pharmacy.domain.service;

import usta.pharmacy.domain.model.Product;
import usta.pharmacy.domain.model.Sale;

import java.time.LocalDate;

/** Servicio de dominio: reglas de una venta que involucran a Product y Sale (sin puertos). */
public class SaleDomainService {

    /**
     * Crea la venta, la valida y descuenta el stock del producto.
     * Regla: no se puede vender más de lo que hay en inventario.
     */
    public Sale makeSale(Product product, int quantity, LocalDate saleDate) {
        Sale sale = Sale.create(product, quantity, saleDate);
        sale.validate();
        product.decreaseStock(quantity);
        return sale;
    }
}