package usta.pharmacy.application.exception;

import usta.pharmacy.domain.exception.BusinessException;

/** Se lanza cuando un caso de uso no encuentra el recurso solicitado. */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}