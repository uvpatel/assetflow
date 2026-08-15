package com.assetflow.shared.config;

import com.assetflow.allocation.domain.AssetAllocationRepository;
import com.assetflow.asset.domain.AssetCategoryRepository;
import com.assetflow.asset.domain.AssetRepository;
import com.assetflow.audit.domain.AuditCycleRepository;
import com.assetflow.audit.domain.AuditItemRepository;
import com.assetflow.booking.domain.ResourceBookingRepository;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.notification.domain.NotificationRepository;
import com.assetflow.organization.domain.DepartmentRepository;
import com.assetflow.organization.domain.EmployeeRepository;
import com.assetflow.organization.domain.Organization;
import com.assetflow.organization.domain.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private OrganizationRepository organizationRepo;
    @Mock
    private DepartmentRepository departmentRepo;
    @Mock
    private EmployeeRepository employeeRepo;
    @Mock
    private UserAccountRepository userAccountRepo;
    @Mock
    private AssetCategoryRepository categoryRepo;
    @Mock
    private AssetRepository assetRepo;
    @Mock
    private AssetAllocationRepository allocationRepo;
    @Mock
    private ResourceBookingRepository bookingRepo;
    @Mock
    private MaintenanceRequestRepository maintenanceRepo;
    @Mock
    private AuditCycleRepository auditCycleRepo;
    @Mock
    private AuditItemRepository auditItemRepo;
    @Mock
    private NotificationRepository notificationRepo;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private DataSeeder dataSeeder;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(dataSeeder, "adminPassword", "Password123!");
        lenient().when(passwordEncoder.encode(any())).thenReturn("hashed-password");
        lenient().when(organizationRepo.findById(any())).thenReturn(Optional.of(new Organization("AssetFlow Enterprise", "DEFAULT_ORG")));
    }

    @Test
    @DisplayName("Should skip seeding if admin account already exists (Idempotency)")
    void shouldSkipSeedingWhenDataExists() {
        when(userAccountRepo.existsByEmail("admin@assetflow.example")).thenReturn(true);

        dataSeeder.run();

        verify(userAccountRepo, never()).save(any(UserAccount.class));
        verify(organizationRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should execute seed logic when admin account is absent")
    void shouldExecuteSeedingWhenDataAbsent() {
        when(organizationRepo.findById(any())).thenReturn(Optional.empty());
        when(organizationRepo.save(any())).thenAnswer(i -> {
            Organization o = i.getArgument(0);
            o.setId(DataSeeder.DEFAULT_ORG_ID);
            return o;
        });
        when(departmentRepo.save(any())).thenAnswer(i -> {
            com.assetflow.organization.domain.Department d = i.getArgument(0);
            d.setId(UUID.randomUUID());
            return d;
        });
        when(categoryRepo.save(any())).thenAnswer(i -> {
            com.assetflow.asset.domain.AssetCategory c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });
        when(employeeRepo.save(any())).thenAnswer(i -> {
            com.assetflow.organization.domain.Employee e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });
        when(userAccountRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(assetRepo.save(any())).thenAnswer(i -> {
            com.assetflow.asset.domain.Asset a = i.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });
        when(auditCycleRepo.save(any())).thenAnswer(i -> {
            com.assetflow.audit.domain.AuditCycle ac = i.getArgument(0);
            ac.setId(UUID.randomUUID());
            return ac;
        });

        dataSeeder.run();

        verify(userAccountRepo, atLeast(25)).save(any(UserAccount.class));
        verify(departmentRepo, times(5)).save(any());
        verify(categoryRepo, times(8)).save(any());
        verify(assetRepo, times(50)).save(any());
    }

    @Test
    @DisplayName("Should maintain entity state and skip creation on subsequent seeder runs")
    void testIdempotentRepeatedRun() {
        when(userAccountRepo.existsByEmail("admin@assetflow.example")).thenReturn(true);
        dataSeeder.run();

        verify(userAccountRepo, never()).save(any(UserAccount.class));
        verify(organizationRepo, never()).save(any());
    }
}
