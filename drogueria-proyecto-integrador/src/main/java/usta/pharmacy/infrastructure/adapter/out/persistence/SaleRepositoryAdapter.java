package usta.pharmacy.infrastructure.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import usta.pharmacy.application.port.out.SaleRepositoryPort;
import usta.pharmacy.domain.model.Sale;
import usta.pharmacy.infrastructure.adapter.out.persistence.entity.ProductEntity;
import usta.pharmacy.infrastructure.adapter.out.persistence.entity.SaleEntity;
import usta.pharmacy.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import usta.pharmacy.infrastructure.adapter.out.persistence.mapper.SaleMapper;

import java.time.LocalDate;
import java.util.List;

/** Adaptador de salida: implementa el puerto usando JPA/Hibernate. */
@ApplicationScoped
public class SaleRepositoryAdapter implements SaleRepositoryPort {

    @Inject
    EntityManager em;

    /** Una sola transacción: actualiza el stock del producto y guarda la venta. */
    @Override
    @Transactional
    public Sale saveAndUpdateStock(Sale sale) {
        ProductEntity product = em.merge(ProductMapper.toEntity(sale.getProduct()));

        SaleEntity entity = SaleMapper.toEntity(sale);
        entity.setProduct(product);

        SaleEntity saved = em.merge(entity);
        em.flush();
        return SaleMapper.toDomain(saved);
    }

    @Override
    public List<Sale> findBetween(LocalDate from, LocalDate to) {
        return em.createQuery("SELECT s FROM SaleEntity s WHERE s.saleDate BETWEEN :from AND :to "
                        + "ORDER BY s.saleDate DESC, s.id DESC", SaleEntity.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList()
                .stream()
                .map(SaleMapper::toDomain)
                .toList();
    }
}