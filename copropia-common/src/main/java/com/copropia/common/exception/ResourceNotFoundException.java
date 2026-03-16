package com.copropia.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " no encontrado con id: " + id, HttpStatus.NOT_FOUND);
    }
}
