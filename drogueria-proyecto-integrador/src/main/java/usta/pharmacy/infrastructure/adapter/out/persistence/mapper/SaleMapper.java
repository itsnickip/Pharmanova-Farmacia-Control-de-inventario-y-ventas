package usta.pharmacy.infrastructure.adapter.out.persistence.mapper;

import usta.pharmacy.domain.model.Sale;
import usta.pharmacy.infrastructure.adapter.out.persistence.entity.SaleEntity;

/** Convierte entre el modelo de dominio y la entidad JPA. */
public final class SaleMapper {

    private SaleMapper() {
    }

    public static Sale toDomain(SaleEntity entity) {
        return new Sale(entity.getId(), ProductMapper.toDomain(entity.getProduct()),
                entity.getQuantity(), entity.getUnitPrice(), entity.getTotal(), entity.getSaleDate());
    }

    public static SaleEntity toEntity(Sale sale) {
        SaleEntity entity = new SaleEntity();
        entity.setId(sale.getId());
        entity.setProduct(ProductMapper.toEntity(sale.getProduct()));
        entity.setQuantity(sale.getQuantity());
        entity.setUnitPrice(sale.getUnitPrice());
        entity.setTotal(sale.getTotal());
        entity.setSaleDate(sale.getSaleDate());
        return entity;
    }
}