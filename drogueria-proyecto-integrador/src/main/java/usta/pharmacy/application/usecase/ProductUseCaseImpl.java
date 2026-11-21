package usta.pharmacy.application.usecase;

import usta.pharmacy.application.exception.ResourceNotFoundException;
import usta.pharmacy.application.port.in.ProductUseCase;
import usta.pharmacy.application.port.out.ProductRepositoryPort;
import usta.pharmacy.domain.exception.BusinessException;
import usta.pharmacy.domain.model.Product;

import java.util.List;

/** Caso de uso de productos: orquesta el dominio y el puerto de salida (sin anotaciones de framework). */
public class ProductUseCaseImpl implements ProductUseCase {

    private final ProductRepositoryPort productRepository;

    public ProductUseCaseImpl(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product register(Product product) {
        product.validate();
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new BusinessException("Ya existe un producto con el nombre " + product.getName() + ".");
        }
        return productRepository.save(product);
    }

    @Override
    public Product restock(Long productId, int quantity) {
        Product product = findById(productId);
        product.increaseStock(quantity);
        return productRepository.save(product);
    }

    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> listLowStock() {
        return productRepository.findAll().stream()
                .filter(Product::isLowStock)
                .toList();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El producto solicitado no existe."));
    }
}