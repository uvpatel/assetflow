package com.assetflow.asset.api;

import com.assetflow.asset.api.dto.CreateCategoryFieldDefinitionRequest;
import com.assetflow.asset.api.dto.SetAssetCustomFieldValueRequest;
import com.assetflow.asset.application.CustomFieldUseCase;
import com.assetflow.asset.domain.AssetCustomFieldValue;
import com.assetflow.asset.domain.CategoryFieldDefinition;
import com.assetflow.shared.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomFieldController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomFieldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomFieldUseCase customFieldUseCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("Should create field definition via POST")
    void shouldCreateFieldDefinition() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CategoryFieldDefinition definition = new CategoryFieldDefinition(
                orgId,
                categoryId,
                "imei_number",
                "IMEI",
                "TEXT",
                true,
                null
        );

        when(customFieldUseCase.createFieldDefinition(any(CreateCategoryFieldDefinitionRequest.class)))
                .thenReturn(definition);

        CreateCategoryFieldDefinitionRequest request = new CreateCategoryFieldDefinitionRequest(
                categoryId,
                "imei_number",
                "IMEI",
                "TEXT",
                true,
                null,
                "^[0-9]{15}$",
                null,
                null,
                null
        );

        mockMvc.perform(post("/custom-fields/definitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fieldName").value("imei_number"))
                .andExpect(jsonPath("$.fieldLabel").value("IMEI"))
                .andExpect(jsonPath("$.required").value(true));
    }

    @Test
    @DisplayName("Should get field definitions by category via GET")
    void shouldGetFieldDefinitionsByCategory() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        CategoryFieldDefinition definition = new CategoryFieldDefinition(
                orgId,
                categoryId,
                "serial_no",
                "Serial Number",
                "TEXT",
                false,
                null
        );

        when(customFieldUseCase.getFieldDefinitions(categoryId)).thenReturn(List.of(definition));

        mockMvc.perform(get("/custom-fields/definitions/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fieldName").value("serial_no"))
                .andExpect(jsonPath("$[0].fieldLabel").value("Serial Number"));
    }

    @Test
    @DisplayName("Should set and get custom field values for asset")
    void shouldSetAndGetCustomFieldValues() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID assetId = UUID.randomUUID();
        UUID fieldDefId = UUID.randomUUID();

        AssetCustomFieldValue value = new AssetCustomFieldValue(
                orgId,
                assetId,
                fieldDefId,
                "352099001761482"
        );

        when(customFieldUseCase.setCustomFieldValues(eq(assetId), any())).thenReturn(List.of(value));
        when(customFieldUseCase.getCustomFieldValues(assetId)).thenReturn(List.of(value));

        SetAssetCustomFieldValueRequest setRequest = new SetAssetCustomFieldValueRequest(
                fieldDefId,
                "352099001761482"
        );

        mockMvc.perform(post("/custom-fields/values/asset/{assetId}", assetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(setRequest))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fieldValue").value("352099001761482"));

        mockMvc.perform(get("/custom-fields/values/asset/{assetId}", assetId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fieldValue").value("352099001761482"));
    }
}
