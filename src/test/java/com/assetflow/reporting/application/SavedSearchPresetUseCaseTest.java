package com.assetflow.reporting.application;

import com.assetflow.reporting.domain.SavedSearchPreset;
import com.assetflow.reporting.domain.SavedSearchPresetRepository;
import com.assetflow.shared.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavedSearchPresetUseCaseTest {

    @Mock
    private SavedSearchPresetRepository repository;

    private SavedSearchPresetUseCase useCase;
    private final UUID orgId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new SavedSearchPresetUseCase(repository);
        TenantContext.setOrganizationId(orgId);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should set target search preset as default and unset previous default")
    void shouldSetDefaultPresetAndUnsetPrevious() {
        UUID preset1Id = UUID.randomUUID();
        UUID preset2Id = UUID.randomUUID();

        SavedSearchPreset preset1 = new SavedSearchPreset(
                preset1Id, orgId, userId, "Old Default", "ASSET", "{\"status\":\"ACTIVE\"}", false, true, java.time.Instant.now(), java.time.Instant.now()
        );

        SavedSearchPreset preset2 = new SavedSearchPreset(
                preset2Id, orgId, userId, "New Default", "ASSET", "{\"status\":\"INACTIVE\"}", false, false, java.time.Instant.now(), java.time.Instant.now()
        );

        when(repository.findById(preset2Id)).thenReturn(Optional.of(preset2));
        when(repository.findAvailablePresets(orgId, userId, "ASSET")).thenReturn(List.of(preset1, preset2));
        when(repository.save(any(SavedSearchPreset.class))).thenAnswer(inv -> inv.getArgument(0));

        SavedSearchPreset updatedDefault = useCase.setDefaultPreset(orgId, userId, preset2Id);

        assertThat(preset1.isDefault()).isFalse();
        assertThat(updatedDefault.isDefault()).isTrue();
        verify(repository).save(preset1);
        verify(repository).save(preset2);
    }
}
