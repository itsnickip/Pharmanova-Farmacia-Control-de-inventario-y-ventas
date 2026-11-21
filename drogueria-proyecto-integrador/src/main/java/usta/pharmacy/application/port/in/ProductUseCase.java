package usta.pharmacy.application.port.in;

import usta.pharmacy.domain.model.Product;

import java.util.List;

/** Puerto de entrada: casos de uso de productos. */
public interface ProductUseCase {

    Product register(Product product);

    /** Suma unidades al inventario de un producto. */
    Product restock(Long productId, int quantity);

    List<Product> listAll();

    /** Productos con stock menor o igual al mínimo. */
    List<Product> listLowStock();

    Product findById(Long id);
}