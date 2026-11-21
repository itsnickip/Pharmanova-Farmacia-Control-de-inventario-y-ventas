package usta.pharmacy.application.usecase;

import usta.pharmacy.application.exception.ResourceNotFoundException;
import usta.pharmacy.application.port.in.SaleUseCase;
import usta.pharmacy.application.port.out.ProductRepositoryPort;
import usta.pharmacy.application.port.out.SaleRepositoryPort;
import usta.pharmacy.domain.model.MonthlySummary;
import usta.pharmacy.domain.model.Product;
import usta.pharmacy.domain.model.Sale;
import usta.pharmacy.domain.service.SaleDomainService;
import usta.pharmacy.domain.service.SalesSummaryService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Caso de uso de ventas: usa los servicios de dominio y los puertos de salida. */
public class SaleUseCaseImpl implements SaleUseCase {

    private final SaleRepositoryPort saleRepository;
    private final ProductRepositoryPort productRepository;
    private final SaleDomainService saleDomainService;
    private final SalesSummaryService salesSummaryService;

    public SaleUseCaseImpl(SaleRepositoryPort saleRepository, ProductRepositoryPort productRepository,
                           SaleDomainService saleDomainService, SalesSummaryService salesSummaryService) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.saleDomainService = saleDomainService;
        this.salesSummaryService = salesSummaryService;
    }

    @Override
    public Sale register(Long productId, int quantity, LocalDate saleDate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto seleccionado no existe."));

        // Las reglas de la venta (validación y descuento de stock) viven en el dominio
        Sale sale = saleDomainService.makeSale(product, quantity, saleDate);

        return saleRepository.saveAndUpdateStock(sale);
    }

    @Override
    public List<Sale> listByMonth(YearMonth month) {
        return saleRepository.findBetween(month.atDay(1), month.atEndOfMonth());
    }

    @Override
    public MonthlySummary summarize(YearMonth month) {
        return salesSummaryService.summarize(month, listByMonth(month));
    }
}