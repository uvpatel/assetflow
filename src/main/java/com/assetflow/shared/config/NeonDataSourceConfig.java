package com.assetflow.shared.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

@Configuration
public class NeonDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new CustomDataSourceProperties();
    }

    public static class CustomDataSourceProperties extends DataSourceProperties {

        @Override
        public void setUrl(String url) {
            if (StringUtils.hasText(url)) {
                url = url.trim();

                // Convert protocol postgresql:// to jdbc:postgresql:// if needed
                if (url.startsWith("postgresql://")) {
                    url = "jdbc:" + url;
                }

                // If credentials exist within the URL (e.g., jdbc:postgresql://user:pass@host/db), extract them
                if (url.startsWith("jdbc:postgresql://") && url.contains("@")) {
                    int prefixLength = "jdbc:postgresql://".length();
                    int atIndex = url.indexOf('@');
                    String credentialsPart = url.substring(prefixLength, atIndex);
                    String restOfUrl = url.substring(atIndex + 1);

                    if (credentialsPart.contains(":")) {
                        String[] parts = credentialsPart.split(":", 2);
                        if (!StringUtils.hasText(getUsername())) {
                            setUsername(parts[0]);
                        }
                        if (!StringUtils.hasText(getPassword())) {
                            setPassword(parts[1]);
                        }
                    } else if (!StringUtils.hasText(getUsername())) {
                        setUsername(credentialsPart);
                    }
                    url = "jdbc:postgresql://" + restOfUrl;
                }
            }
            super.setUrl(url);
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            super.afterPropertiesSet();
            if (!StringUtils.hasText(getUrl())) {
                throw new IllegalStateException("Neon database configuration error: DATABASE_URL is missing or empty.");
            }
            if (!StringUtils.hasText(getUsername())) {
                throw new IllegalStateException("Neon database configuration error: DATABASE_USERNAME is missing or empty.");
            }
            if (!StringUtils.hasText(getPassword())) {
                throw new IllegalStateException("Neon database configuration error: DATABASE_PASSWORD is missing or empty.");
            }
        }
    }
}

