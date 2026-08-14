package com.assetflow.shared.error;

import java.net.URI;
import java.util.Map;

public class DomainViolationException extends BusinessException {

    public DomainViolationException(String detail) {
        this("Domain Rule Violation", detail, null, null);
    }

    public DomainViolationException(String title, String detail) {
        this(title, detail, null, null);
    }

    public DomainViolationException(String title, String detail, URI type, Map<String, Object> properties) {
        super(title, 400, detail, type, properties);
    }
}
