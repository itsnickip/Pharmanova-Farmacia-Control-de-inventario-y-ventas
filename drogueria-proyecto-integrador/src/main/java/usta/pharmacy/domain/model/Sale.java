package usta.pharmacy.domain.model;

import usta.pharmacy.domain.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Entidad de dominio pura: venta de un producto. */
public class Sale {

    private Long id;
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
    private LocalDate saleDate;

    public Sale(Long id, Product product, int quantity, BigDecimal unitPrice,
                BigDecimal total, LocalDate saleDate) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice == null ? null : unitPrice.setScale(2, RoundingMode.HALF_UP);
        this.total = total == null ? null : total.setScale(2, RoundingMode.HALF_UP);
        this.saleDate = saleDate;
    }

    /** Crea una venta nueva calculando el precio unitario y el total a partir del producto. */
    public static Sale create(Product product, int quantity, LocalDate saleDate) {
        BigDecimal unitPrice = product == null ? null : product.getPrice();
        BigDecimal total = unitPrice == null ? null : unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new Sale(null, product, quantity, unitPrice, total, saleDate);
    }

    /** Valida las reglas de negocio de la venta. */
    public void validate() {
        if (product == null) {
            throw new BusinessException("Debe seleccionar un producto.");
        }
        if (quantity <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que cero.");
        }
        if (saleDate == null || saleDate.isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de la venta es obligatoria y no puede ser futura.");
        }
    }

    public Long getId() {
        return id; }
    public Product getProduct() {
        return product; }
    public int getQuantity() {
        return quantity; }
    public BigDecimal getUnitPrice() {
        return unitPrice; }
    public BigDecimal getTotal() {
        return total; }
    public LocalDate getSaleDate() {
        return saleDate; }
}