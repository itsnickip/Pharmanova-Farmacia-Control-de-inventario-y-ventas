package usta.pharmacy.infrastructure.adapter.out.persistence.mapper;

import usta.pharmacy.domain.model.Product;
import usta.pharmacy.infrastructure.adapter.out.persistence.entity.ProductEntity;

/** Convierte entre el modelo de dominio y la entidad JPA. */
public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toDomain(ProductEntity entity) {
        return new Product(entity.getId(), entity.getName(), entity.getCategory(),
                entity.getPrice(), entity.getStock(), entity.getMinStock());
    }

    public static ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setCategory(product.getCategory());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setMinStock(product.getMinStock());
        return entity;
    }
}