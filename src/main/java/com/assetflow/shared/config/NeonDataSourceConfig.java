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
            if (StringUtils.hasText(url) && url.startsWith("postgresql://")) {
                url = "jdbc:" + url;
            }
            super.setUrl(url);
        }
    }
}
