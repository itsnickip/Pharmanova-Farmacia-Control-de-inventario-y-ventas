package usta.pharmacy.domain.exception;

/** Error de regla de negocio. El mensaje se muestra al usuario en la vista. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}