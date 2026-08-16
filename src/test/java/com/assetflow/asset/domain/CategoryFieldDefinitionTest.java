package com.assetflow.asset.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryFieldDefinitionTest {

    @Test
    @DisplayName("Should validate required field presence correctly")
    void shouldValidateRequiredField() {
        CategoryFieldDefinition def = new CategoryFieldDefinition(
                UUID.randomUUID(), UUID.randomUUID(), "serial_no", "Serial Number", "STRING", true, null
        );

        assertThatThrownBy(() -> def.validateFieldValue(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("required");

        assertThatCode(() -> def.validateFieldValue("SN-12345")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should validate regex pattern matching correctly")
    void shouldValidateRegexPattern() {
        CategoryFieldDefinition def = new CategoryFieldDefinition(
                UUID.randomUUID(), UUID.randomUUID(), "ip_addr", "IP Address", "STRING", false, null,
                "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", null, null, null
        );

        assertThatThrownBy(() -> def.validateFieldValue("invalid-ip"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not match required pattern");

        assertThatCode(() -> def.validateFieldValue("192.168.1.1")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should validate numeric min/max range boundaries correctly")
    void shouldValidateNumericRange() {
        CategoryFieldDefinition def = new CategoryFieldDefinition(
                UUID.randomUUID(), UUID.randomUUID(), "voltage", "Voltage Rating", "NUMBER", false, null,
                null, null, new BigDecimal("100.00"), new BigDecimal("240.00")
        );

        assertThatThrownBy(() -> def.validateFieldValue("90.00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be less than");

        assertThatThrownBy(() -> def.validateFieldValue("300.00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be greater than");

        assertThatCode(() -> def.validateFieldValue("220.00")).doesNotThrowAnyException();
    }
}
