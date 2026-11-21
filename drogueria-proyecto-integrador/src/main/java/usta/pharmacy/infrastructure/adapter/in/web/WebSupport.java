package usta.pharmacy.infrastructure.adapter.in.web;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

/** Utilidades compartidas por los recursos web. */
final class WebSupport {

    private static final Locale SPANISH = Locale.forLanguageTag("es-CO");

    private WebSupport() {
    }

    static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    /** Redirige a una ruta enviando un mensaje (success o error) por query param. */
    static Response redirect(String path, String key, String message) {
        return Response.seeOther(UriBuilder.fromPath(path).queryParam(key, message).build()).build();
    }

    /** Convierte "2026-11" en un YearMonth; si viene vacío o inválido, usa el mes actual. */
    static YearMonth parseMonthOrCurrent(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim());
        } catch (DateTimeParseException e) {
            return YearMonth.now();
        }
    }

    /** Ejemplo: "noviembre de 2026". */
    static String monthLabel(YearMonth month) {
        return month.getMonth().getDisplayName(TextStyle.FULL, SPANISH) + " de " + month.getYear();
    }
}