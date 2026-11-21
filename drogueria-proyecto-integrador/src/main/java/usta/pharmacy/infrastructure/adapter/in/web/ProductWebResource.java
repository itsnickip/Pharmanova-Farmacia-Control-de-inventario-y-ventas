package usta.pharmacy.infrastructure.adapter.in.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import usta.pharmacy.application.port.in.ProductUseCase;
import usta.pharmacy.domain.exception.BusinessException;
import usta.pharmacy.domain.model.Product;

import java.math.BigDecimal;

import static usta.pharmacy.infrastructure.adapter.in.web.WebSupport.orEmpty;
import static usta.pharmacy.infrastructure.adapter.in.web.WebSupport.redirect;

/** Adaptador de entrada: vistas HTML del inventario de productos. */
@Path("/")
@Tag(name = "Productos", description = "Vistas de inventario, productos por agotarse y reposición")
public class ProductWebResource {

    @Inject
    ProductUseCase productUseCase;

    @Inject
    Template index;

    @Inject
    Template productForm;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Página principal",
            description = "Inventario de productos; con filter=low muestra solo los que están por agotarse")
    public TemplateInstance home(@RestQuery String filter, @RestQuery String success, @RestQuery String error) {
        boolean onlyLow = "low".equals(filter);
        return index.data("products", onlyLow ? productUseCase.listLowStock() : productUseCase.listAll())
                .data("filter", onlyLow ? "low" : "all")
                .data("lowCount", productUseCase.listLowStock().size())
                .data("success", orEmpty(success))
                .data("error", orEmpty(error));
    }

    @GET
    @Path("/products/new")
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Formulario de registro de producto")
    public Response newForm() {
        return renderForm("", "", "", "", "", "");
    }

    @POST
    @Path("/products")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Registrar producto", description = "Valida las reglas de negocio y guarda en MySQL")
    public Response create(@RestForm String name, @RestForm String category, @RestForm String price,
                           @RestForm String stock, @RestForm String minStock) {
        String nameText = orEmpty(name);
        String categoryText = orEmpty(category);
        String priceText = orEmpty(price);
        String stockText = orEmpty(stock);
        String minStockText = orEmpty(minStock);
        try {
            Product product = new Product(null, nameText, categoryText, new BigDecimal(priceText.trim()),
                    Integer.parseInt(stockText.trim()), Integer.parseInt(minStockText.trim()));
            productUseCase.register(product);
            return redirect("/", "success", "Producto registrado correctamente.");
        } catch (BusinessException e) {
            return renderForm(nameText, categoryText, priceText, stockText, minStockText, e.getMessage());
        } catch (NumberFormatException e) {
            return renderForm(nameText, categoryText, priceText, stockText, minStockText,
                    "Revise los datos: el precio, el stock y el stock mínimo deben ser números válidos.");
        }
    }

    @POST
    @Path("/products/{id}/restock")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(summary = "Reponer stock", description = "Suma unidades al inventario de un producto")
    public Response restock(@RestPath Long id, @RestForm String quantity) {
        try {
            Product product = productUseCase.restock(id, Integer.parseInt(orEmpty(quantity).trim()));
            return redirect("/", "success", "Stock actualizado: " + product.getName()
                    + " ahora tiene " + product.getStock() + " unidades.");
        } catch (BusinessException e) {
            return redirect("/", "error", e.getMessage());
        } catch (NumberFormatException e) {
            return redirect("/", "error", "La cantidad a reponer debe ser un número entero válido.");
        }
    }

    private Response renderForm(String name, String category, String price,
                                String stock, String minStock, String error) {
        String html = productForm.data("name", name)
                .data("category", category)
                .data("price", price)
                .data("stock", stock)
                .data("minStock", minStock)
                .data("error", error)
                .data("success", "")
                .render();
        return Response.ok(html, MediaType.TEXT_HTML).build();
    }
}