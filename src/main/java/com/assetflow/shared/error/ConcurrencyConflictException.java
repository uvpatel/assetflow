package com.assetflow.shared.error;

import java.net.URI;
import java.util.Map;

public class ConcurrencyConflictException extends BusinessException {

    public ConcurrencyConflictException(String detail) {
        this("Concurrency Conflict", detail, null, null);
    }

    public ConcurrencyConflictException(String title, String detail, URI type, Map<String, Object> properties) {
        super(title, 409, detail, type, properties);
    }
}
