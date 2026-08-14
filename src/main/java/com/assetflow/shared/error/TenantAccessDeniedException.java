package com.assetflow.shared.error;

import java.net.URI;
import java.util.Map;

public class TenantAccessDeniedException extends BusinessException {

    public TenantAccessDeniedException(String detail) {
        this("Tenant Access Denied", detail, null, null);
    }

    public TenantAccessDeniedException(String title, String detail, URI type, Map<String, Object> properties) {
        super(title, 403, detail, type, properties);
    }
}
