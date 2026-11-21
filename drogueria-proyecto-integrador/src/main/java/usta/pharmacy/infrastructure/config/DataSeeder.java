package usta.pharmacy.infrastructure.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import usta.pharmacy.application.port.in.ProductUseCase;
import usta.pharmacy.application.port.in.SaleUseCase;
import usta.pharmacy.domain.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Carga datos de prueba coherentes la primera vez que se inicia la aplicación. */
@ApplicationScoped
public class DataSeeder {

    @Inject
    ProductUseCase productUseCase;

    @Inject
    SaleUseCase saleUseCase;

    void onStart(@Observes StartupEvent event) {
        if (!productUseCase.listAll().isEmpty()) {
            return; // ya hay datos
        }

        // Stock inicial (las ventas de abajo lo van descontando)
        Product acetaminophen = product("Acetaminofén 500 mg", "Analgésicos", "8500", 150, 30);
        Product ibuprofen = product("Ibuprofeno 400 mg", "Analgésicos", "12000", 20, 15);
        Product amoxicillin = product("Amoxicilina 500 mg", "Antibióticos", "18500", 60, 15);
        Product loratadine = product("Loratadina 10 mg", "Antialérgicos", "7500", 15, 10);
        Product alcohol = product("Alcohol antiséptico 350 ml", "Higiene", "6800", 70, 20);
        Product oralSerum = product("Suero oral sobre", "Hidratación", "3500", 30, 10);
        Product vitaminC = product("Vitamina C 1 g efervescente", "Vitaminas", "15000", 12, 8);
        Product mask = product("Mascarilla quirúrgica caja x50", "Protección", "22000", 30, 10);

        // Ventas: algunas del mes actual y otras de meses anteriores
        sell(acetaminophen, 12, 1);
        sell(acetaminophen, 10, 5);
        sell(acetaminophen, 8, 33);
        sell(ibuprofen, 7, 2);
        sell(ibuprofen, 5, 12);
        sell(amoxicillin, 15, 3);
        sell(loratadine, 6, 4);
        sell(loratadine, 4, 10);
        sell(alcohol, 10, 6);
        sell(oralSerum, 20, 7);
        sell(oralSerum, 10, 15);
        sell(vitaminC, 9, 8);
        sell(mask, 5, 20);
        // Resultado: Ibuprofeno (8), Loratadina (5), Vitamina C (3) quedan por agotarse
        // y el Suero oral queda agotado (0).
    }

    private Product product(String name, String category, String price, int stock, int minStock) {
        return productUseCase.register(new Product(null, name, category, new BigDecimal(price), stock, minStock));
    }

    private void sell(Product product, int quantity, int daysAgo) {
        saleUseCase.register(product.getId(), quantity, LocalDate.now().minusDays(daysAgo));
    }
}