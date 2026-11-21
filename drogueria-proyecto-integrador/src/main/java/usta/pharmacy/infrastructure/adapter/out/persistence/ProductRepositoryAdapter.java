package usta.pharmacy.infrastructure.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import usta.pharmacy.application.port.out.ProductRepositoryPort;
import usta.pharmacy.domain.model.Product;
import usta.pharmacy.infrastructure.adapter.out.persistence.entity.ProductEntity;
import usta.pharmacy.infrastructure.adapter.out.persistence.mapper.ProductMapper;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida: implementa el puerto usando JPA/Hibernate. */
@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity saved = em.merge(ProductMapper.toEntity(product));
        em.flush();
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(ProductEntity.class, id))
                .map(ProductMapper::toDomain);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return em.createQuery("SELECT p FROM ProductEntity p WHERE LOWER(p.name) = LOWER(:name)",
                        ProductEntity.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM ProductEntity p ORDER BY p.name", ProductEntity.class)
                .getResultList()
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }
}