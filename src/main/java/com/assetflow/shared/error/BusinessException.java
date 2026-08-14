package com.assetflow.shared.error;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public abstract class BusinessException extends RuntimeException {

    private final String title;
    private final int status;
    private final URI type;
    private final Map<String, Object> properties;

    protected BusinessException(String title, int status, String detail, URI type, Map<String, Object> properties) {
        super(detail);
        this.title = title;
        this.status = status;
        this.type = type != null ? type : URI.create("about:blank");
        this.properties = properties != null ? properties : Collections.emptyMap();
    }

    public String getTitle() {
        return title;
    }

    public int getStatus() {
        return status;
    }

    public URI getType() {
        return type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
