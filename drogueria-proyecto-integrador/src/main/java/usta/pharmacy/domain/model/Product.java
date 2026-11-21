package usta.pharmacy.domain.model;

import usta.pharmacy.domain.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Entidad de dominio pura: sin anotaciones de JPA ni de ningún framework. */
public class Product {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private int stock;
    private int minStock;

    public Product(Long id, String name, String category, BigDecimal price, int stock, int minStock) {
        this.id = id;
        this.name = name == null ? null : name.trim();
        this.category = category == null ? null : category.trim();
        this.price = price == null ? null : price.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
        this.minStock = minStock;
    }

    /** Valida las reglas de negocio del producto. */
    public void validate() {
        if (name == null || name.length() < 2) {
            throw new BusinessException("El nombre del producto debe tener al menos 2 caracteres.");
        }
        if (category == null || category.isBlank()) {
            throw new BusinessException("La categoría es obligatoria.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor que cero.");
        }
        if (stock < 0) {
            throw new BusinessException("El stock no puede ser negativo.");
        }
        if (minStock < 0) {
            throw new BusinessException("El stock mínimo no puede ser negativo.");
        }
    }

    /** Un producto está "por agotarse" cuando su stock es menor o igual al mínimo. */
    public boolean isLowStock() {
        return stock <= minStock;
    }

    public boolean isOutOfStock() {
        return stock == 0;
    }

    /** Descuenta unidades por una venta. No permite dejar el stock en negativo. */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("La cantidad a vender debe ser mayor que cero.");
        }
        if (stock == 0) {
            throw new BusinessException("El producto " + name + " está agotado.");
        }
        if (quantity > stock) {
            throw new BusinessException("Stock insuficiente: solo quedan " + stock
                    + " unidades de " + name + ".");
        }
        this.stock -= quantity;
    }

    /** Suma unidades al reponer inventario. */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("La cantidad a reponer debe ser mayor que cero.");
        }
        this.stock += quantity;
    }

    public Long getId() {
        return id; }
    public String getName() {
        return name; }
    public String getCategory() {
        return category; }
    public BigDecimal getPrice() {
        return price; }
    public int getStock() {
        return stock; }
    public int getMinStock() {
        return minStock; }
}