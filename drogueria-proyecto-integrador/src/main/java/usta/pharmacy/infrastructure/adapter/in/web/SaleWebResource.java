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
import org.jboss.resteasy.reactive.RestQuery;
import usta.pharmacy.application.port.in.ProductUseCase;
import usta.pharmacy.application.port.in.SaleUseCase;
import usta.pharmacy.domain.exception.BusinessException;
import usta.pharmacy.domain.model.Sale;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import static usta.pharmacy.infrastructure.adapter.in.web.WebSupport.monthLabel;
import static usta.pharmacy.infrastructure.adapter.in.web.WebSupport.orEmpty;
import static usta.pharmacy.infrastructure.adapter.in.web.WebSupport.parseMonthOrCurrent;
import static usta.pharmacy.infrastructure.adapter.in.web.WebSupport.redirect;

/** Adaptador de entrada: vistas HTML de ventas y reporte mensual. */
@Path("/sales")
@Tag(name = "Ventas", description = "Vistas de registro de ventas, resumen mensual y reporte PDF")
public class SaleWebResource {

    @Inject
    SaleUseCase saleUseCase;

    @Inject
    ProductUseCase productUseCase;

    @Inject
    SalesPdfGenerator pdfGenerator;

    @Inject
    Template sales;

    @Inject
    Template form;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Ventas del mes",
            description = "Lista las ventas del mes indicado (yyyy-MM) con su resumen: total vendido, unidades y producto más vendido")
    public TemplateInstance list(@RestQuery String month, @RestQuery String success, @RestQuery String error) {
        YearMonth yearMonth = parseMonthOrCurrent(month);
        return sales.data("sales", saleUseCase.listByMonth(yearMonth))
                .data("summary", saleUseCase.summarize(yearMonth))
                .data("month", yearMonth.toString())
                .data("monthLabel", monthLabel(yearMonth))
                .data("success", orEmpty(success))
                .data("error", orEmpty(error));
    }

    @GET
    @Path("/new")
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Formulario de nueva venta")
    public Response newForm(@RestQuery String productId) {
        return renderForm(orEmpty(productId), "", LocalDate.now().toString(), "");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Registrar venta", description = "Valida el stock disponible, descuenta unidades y guarda en MySQL")
    public Response create(@RestForm String productId, @RestForm String quantity, @RestForm String saleDate) {
        String idText = orEmpty(productId);
        String quantityText = orEmpty(quantity);
        String dateText = orEmpty(saleDate);
        try {
            Sale sale = saleUseCase.register(Long.valueOf(idText), Integer.parseInt(quantityText.trim()),
                    LocalDate.parse(dateText));
            return redirect("/sales", "success", "Venta registrada: " + sale.getQuantity() + " x "
                    + sale.getProduct().getName() + " por $" + sale.getTotal() + ".");
        } catch (BusinessException e) {
            return renderForm(idText, quantityText, dateText, e.getMessage());
        } catch (NumberFormatException | DateTimeParseException e) {
            return renderForm(idText, quantityText, dateText,
                    "Revise los datos: el producto, la cantidad y la fecha deben tener un formato válido.");
        }
    }

    @GET
    @Path("/report/pdf")
    @Produces("application/pdf")
    @Operation(summary = "Reporte PDF mensual",
            description = "Descarga el resumen de ventas del mes y los productos por agotarse")
    public Response pdf(@RestQuery String month) {
        YearMonth yearMonth = parseMonthOrCurrent(month);
        byte[] content = pdfGenerator.generate(yearMonth, saleUseCase.listByMonth(yearMonth),
                saleUseCase.summarize(yearMonth), productUseCase.listLowStock());
        return Response.ok(content)
                .header("Content-Disposition", "attachment; filename=\"reporte-" + yearMonth + ".pdf\"")
                .build();
    }

    private Response renderForm(String productId, String quantity, String saleDate, String error) {
        String html = form.data("products", productUseCase.listAll())
                .data("productId", productId)
                .data("quantity", quantity)
                .data("saleDate", saleDate)
                .data("error", error)
                .data("success", "")
                .render();
        return Response.ok(html, MediaType.TEXT_HTML).build();
    }
}