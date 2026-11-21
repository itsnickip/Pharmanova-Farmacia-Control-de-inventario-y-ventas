package usta.pharmacy.application.port.out;

import usta.pharmacy.domain.model.Product;

import java.util.List;
import java.util.Optional;

/** Puerto de salida: lo que el sistema necesita para guardar y consultar productos. */
public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findByName(String name);

    List<Product> findAll();
}