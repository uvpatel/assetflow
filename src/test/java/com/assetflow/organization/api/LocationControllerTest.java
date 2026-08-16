package com.assetflow.organization.api;

import com.assetflow.organization.api.dto.CreateLocationRequest;
import com.assetflow.organization.api.dto.LocationResponse;
import com.assetflow.organization.api.dto.LocationTreeNodeDto;
import com.assetflow.organization.api.dto.UpdateLocationRequest;
import com.assetflow.organization.application.LocationUseCase;
import com.assetflow.organization.domain.LocationType;
import com.assetflow.shared.error.GlobalExceptionHandler;
import com.assetflow.shared.security.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocationUseCase locationUseCase;

    @MockitoBean
    private com.assetflow.identity.application.port.TokenProviderPort tokenProviderPort;

    private final UUID orgId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should create location node via POST")
    void shouldCreateLocation() throws Exception {
        UUID locId = UUID.randomUUID();
        CreateLocationRequest request = new CreateLocationRequest(
                "Headquarters Building A",
                "HQ-BLD-A",
                LocationType.BUILDING,
                null,
                "Main campus corporate tower",
                null
        );

        LocationResponse response = new LocationResponse(
                locId,
                orgId,
                "Headquarters Building A",
                "HQ-BLD-A",
                LocationType.BUILDING,
                null,
                "Main campus corporate tower",
                null,
                true,
                Instant.now(),
                Instant.now()
        );

        when(locationUseCase.createLocation(eq(orgId), any(CreateLocationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(locId.toString()))
                .andExpect(jsonPath("$.name").value("Headquarters Building A"))
                .andExpect(jsonPath("$.code").value("HQ-BLD-A"))
                .andExpect(jsonPath("$.type").value("BUILDING"));
    }

    @Test
    @DisplayName("Should list locations via GET")
    void shouldListLocations() throws Exception {
        UUID locId = UUID.randomUUID();
        LocationResponse response = new LocationResponse(
                locId,
                orgId,
                "Server Room 101",
                "SR-101",
                LocationType.ROOM,
                null,
                "Primary datacenter server room",
                null,
                true,
                Instant.now(),
                Instant.now()
        );

        when(locationUseCase.listLocations(eq(orgId), eq(Optional.of(LocationType.ROOM))))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/locations")
                        .param("type", "ROOM")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Server Room 101"))
                .andExpect(jsonPath("$[0].type").value("ROOM"));
    }

    @Test
    @DisplayName("Should fetch hierarchical location tree via GET")
    void shouldGetLocationTree() throws Exception {
        UUID campusId = UUID.randomUUID();
        LocationTreeNodeDto node = new LocationTreeNodeDto(
                campusId,
                "Austin HQ Campus",
                "ATX-CAMPUS",
                LocationType.CAMPUS,
                null,
                "100 Tech Blvd",
                true,
                List.of()
        );

        when(locationUseCase.buildLocationTree(eq(orgId))).thenReturn(List.of(node));

        mockMvc.perform(get("/api/v1/locations/tree")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(campusId.toString()))
                .andExpect(jsonPath("$[0].name").value("Austin HQ Campus"))
                .andExpect(jsonPath("$[0].type").value("CAMPUS"));
    }

    @Test
    @DisplayName("Should update location via PUT")
    void shouldUpdateLocation() throws Exception {
        UUID locId = UUID.randomUUID();
        UpdateLocationRequest request = new UpdateLocationRequest(
                "Updated Server Room 101",
                null,
                "100 Tech Blvd",
                "Updated server room description"
        );

        LocationResponse response = new LocationResponse(
                locId,
                orgId,
                "Updated Server Room 101",
                "SR-101",
                LocationType.ROOM,
                null,
                "100 Tech Blvd",
                "Updated server room description",
                true,
                Instant.now(),
                Instant.now()
        );

        when(locationUseCase.updateLocation(eq(orgId), eq(locId), any(UpdateLocationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/locations/{id}", locId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Server Room 101"));
    }

    @Test
    @DisplayName("Should deactivate location via POST")
    void shouldDeactivateLocation() throws Exception {
        UUID locId = UUID.randomUUID();
        LocationResponse response = new LocationResponse(
                locId,
                orgId,
                "Old Storage Locker",
                "LCK-01",
                LocationType.STORAGE_AREA,
                null,
                null,
                null,
                false,
                Instant.now(),
                Instant.now()
        );

        when(locationUseCase.deactivateLocation(eq(orgId), eq(locId))).thenReturn(response);

        mockMvc.perform(post("/api/v1/locations/{id}/deactivate", locId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
