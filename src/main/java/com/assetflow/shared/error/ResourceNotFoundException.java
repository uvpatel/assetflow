package com.assetflow.shared.error;

import java.net.URI;
import java.util.Map;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String detail) {
        this("Resource Not Found", detail, null, null);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        this("Resource Not Found", String.format("%s with identifier '%s' was not found.", resourceName, resourceId), null, Map.of("resourceName", resourceName, "resourceId", String.valueOf(resourceId)));
    }

    public ResourceNotFoundException(String title, String detail, URI type, Map<String, Object> properties) {
        super(title, 404, detail, type, properties);
    }
}
