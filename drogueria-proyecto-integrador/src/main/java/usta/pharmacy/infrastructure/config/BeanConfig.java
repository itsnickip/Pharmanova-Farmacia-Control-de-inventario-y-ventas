package usta.pharmacy.infrastructure.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import usta.pharmacy.application.port.in.ProductUseCase;
import usta.pharmacy.application.port.in.SaleUseCase;
import usta.pharmacy.application.port.out.ProductRepositoryPort;
import usta.pharmacy.application.port.out.SaleRepositoryPort;
import usta.pharmacy.application.usecase.ProductUseCaseImpl;
import usta.pharmacy.application.usecase.SaleUseCaseImpl;
import usta.pharmacy.domain.service.SaleDomainService;
import usta.pharmacy.domain.service.SalesSummaryService;

/**
 * El dominio y la aplicación son clases puras (sin anotaciones de CDI).
 * Aquí se crean y se conectan con los adaptadores: así no dependen del framework.
 */
@ApplicationScoped
public class BeanConfig {

    @Produces
    @Singleton
    public SaleDomainService saleDomainService() {
        return new SaleDomainService();
    }

    @Produces
    @Singleton
    public SalesSummaryService salesSummaryService() {
        return new SalesSummaryService();
    }

    @Produces
    @Singleton
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository) {
        return new ProductUseCaseImpl(productRepository);
    }

    @Produces
    @Singleton
    public SaleUseCase saleUseCase(SaleRepositoryPort saleRepository,
                                   ProductRepositoryPort productRepository,
                                   SaleDomainService saleDomainService,
                                   SalesSummaryService salesSummaryService) {
        return new SaleUseCaseImpl(saleRepository, productRepository, saleDomainService, salesSummaryService);
    }
}