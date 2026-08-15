package com.assetflow.shared.config;

import com.assetflow.allocation.domain.AssetAllocation;
import com.assetflow.allocation.domain.AssetAllocationRepository;
import com.assetflow.asset.domain.*;
import com.assetflow.audit.domain.AuditCycle;
import com.assetflow.audit.domain.AuditCycleRepository;
import com.assetflow.audit.domain.AuditItem;
import com.assetflow.audit.domain.AuditItemRepository;
import com.assetflow.booking.domain.ResourceBooking;
import com.assetflow.booking.domain.ResourceBookingRepository;
import com.assetflow.identity.application.port.PasswordEncoderPort;
import com.assetflow.identity.domain.UserAccount;
import com.assetflow.identity.domain.UserAccountRepository;
import com.assetflow.identity.domain.UserRole;
import com.assetflow.identity.domain.UserStatus;
import com.assetflow.maintenance.domain.MaintenanceRequest;
import com.assetflow.maintenance.domain.MaintenanceRequestRepository;
import com.assetflow.notification.domain.Notification;
import com.assetflow.notification.domain.NotificationRepository;
import com.assetflow.organization.domain.OrganizationRepository;
import com.assetflow.organization.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    public static final UUID DEFAULT_ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final OrganizationRepository organizationRepo;
    private final DepartmentRepository departmentRepo;
    private final EmployeeRepository employeeRepo;
    private final UserAccountRepository userAccountRepo;
    private final AssetCategoryRepository categoryRepo;
    private final AssetRepository assetRepo;
    private final AssetAllocationRepository allocationRepo;
    private final ResourceBookingRepository bookingRepo;
    private final MaintenanceRequestRepository maintenanceRepo;
    private final AuditCycleRepository auditCycleRepo;
    private final AuditItemRepository auditItemRepo;
    private final NotificationRepository notificationRepo;
    private final PasswordEncoderPort passwordEncoder;

    @Value("${app.seed.admin-password:Password123!}")
    private String adminPassword;

    public DataSeeder(OrganizationRepository organizationRepo,
                      DepartmentRepository departmentRepo,
                      EmployeeRepository employeeRepo,
                      UserAccountRepository userAccountRepo,
                      AssetCategoryRepository categoryRepo,
                      AssetRepository assetRepo,
                      AssetAllocationRepository allocationRepo,
                      ResourceBookingRepository bookingRepo,
                      MaintenanceRequestRepository maintenanceRepo,
                      AuditCycleRepository auditCycleRepo,
                      AuditItemRepository auditItemRepo,
                      NotificationRepository notificationRepo,
                      PasswordEncoderPort passwordEncoder) {
        this.organizationRepo = organizationRepo;
        this.departmentRepo = departmentRepo;
        this.employeeRepo = employeeRepo;
        this.userAccountRepo = userAccountRepo;
        this.categoryRepo = categoryRepo;
        this.assetRepo = assetRepo;
        this.allocationRepo = allocationRepo;
        this.bookingRepo = bookingRepo;
        this.maintenanceRepo = maintenanceRepo;
        this.auditCycleRepo = auditCycleRepo;
        this.auditItemRepo = auditItemRepo;
        this.notificationRepo = notificationRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting Data Seeder execution...");

        if (userAccountRepo.existsByEmail("admin@assetflow.example")) {
            log.info("Data Seeder: Demo seed data already present. Skipping insertion.");
            return;
        }

        Organization org = seedOrganization();
        Map<String, Department> depts = seedDepartments(org.getId());
        Map<String, AssetCategory> categories = seedCategories(org.getId());
        List<Employee> employees = seedUsersAndEmployees(org.getId(), depts);
        List<Asset> assets = seedAssets(org.getId(), categories);
        seedAllocations(org.getId(), assets, employees, depts);
        seedBookings(org.getId(), assets, employees);
        seedMaintenance(org.getId(), assets, employees);
        seedAudits(org.getId(), assets, employees.get(0));
        seedNotifications(org.getId(), employees);

        log.info("Data Seeder completed successfully. Seeded 1 Organization, {} Departments, {} Categories, {} Employees/Users, {} Assets.",
                depts.size(), categories.size(), employees.size(), assets.size());
    }

    private Organization seedOrganization() {
        Optional<Organization> existing = organizationRepo.findById(DEFAULT_ORG_ID);
        if (existing.isPresent()) {
            return existing.get();
        }
        Organization org = new Organization("AssetFlow Enterprise", "DEFAULT_ORG");
        return organizationRepo.save(org);
    }

    private Map<String, Department> seedDepartments(UUID orgId) {
        Map<String, Department> result = new LinkedHashMap<>();
        List<Department> existing = departmentRepo.findByOrganizationId(orgId);
        String[][] deptData = {
                {"Executive Leadership", "EXEC"},
                {"Engineering & Software", "ENG"},
                {"IT Infrastructure", "IT"},
                {"Finance & Operations", "FIN"},
                {"Human Resources", "HR"}
        };

        for (String[] data : deptData) {
            Department dept = existing.stream()
                    .filter(d -> data[1].equals(d.getCode()))
                    .findFirst()
                    .orElseGet(() -> departmentRepo.save(new Department(orgId, data[0], data[1], null)));
            result.put(data[1], dept);
        }
        return result;
    }

    private Map<String, AssetCategory> seedCategories(UUID orgId) {
        Map<String, AssetCategory> result = new LinkedHashMap<>();
        List<AssetCategory> existing = categoryRepo.findByOrganizationId(orgId);
        String[][] catData = {
                {"Laptops & Workstations", "LAPTOP", "High performance laptops and desktop PCs"},
                {"Servers & Cloud Hardware", "SERVER", "Rack servers, SAN storage, and switches"},
                {"Monitors & Displays", "MONITOR", "4K and Ultrawide workstation displays"},
                {"Networking Gear", "NETWORK", "Routers, access points, and firewalls"},
                {"Mobile Devices", "MOBILE", "Testing smartphones and tablet devices"},
                {"Office Furniture", "FURNITURE", "Ergonomic standing desks and chairs"},
                {"Audio & Visual", "AV", "Conference cameras and microphones"},
                {"Peripherals & Accessories", "PERIPHERAL", "Keyboards, mice, and docking stations"}
        };

        for (String[] data : catData) {
            AssetCategory cat = existing.stream()
                    .filter(c -> data[1].equals(c.getCode()))
                    .findFirst()
                    .orElseGet(() -> categoryRepo.save(new AssetCategory(orgId, data[0], data[1], data[2])));
            result.put(data[1], cat);
        }
        return result;
    }

    private List<Employee> seedUsersAndEmployees(UUID orgId, Map<String, Department> depts) {
        List<Employee> employees = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode(adminPassword);

        // 1. Admin
        UserAccount adminUser = new UserAccount(UUID.randomUUID(), "admin@assetflow.example", encodedPassword, orgId, UserStatus.ACTIVE, UserRole.ADMIN, Instant.now(), Instant.now());
        UserAccount savedAdminUser = userAccountRepo.save(adminUser);
        Employee adminEmp = new Employee(orgId, savedAdminUser.getId(), depts.get("EXEC").getId(), "EMP-001", "Alice", "Administrator", "admin@assetflow.example");
        employees.add(employeeRepo.save(adminEmp));

        // 2. Dept Heads (2)
        UserAccount dh1 = new UserAccount(UUID.randomUUID(), "depthead.eng@assetflow.example", encodedPassword, orgId, UserStatus.ACTIVE, UserRole.DEPARTMENT_HEAD, Instant.now(), Instant.now());
        UserAccount savedDh1 = userAccountRepo.save(dh1);
        Employee dh1Emp = new Employee(orgId, savedDh1.getId(), depts.get("ENG").getId(), "EMP-002", "Bob", "EngineeringHead", "depthead.eng@assetflow.example");
        employees.add(employeeRepo.save(dh1Emp));

        UserAccount dh2 = new UserAccount(UUID.randomUUID(), "depthead.it@assetflow.example", encodedPassword, orgId, UserStatus.ACTIVE, UserRole.DEPARTMENT_HEAD, Instant.now(), Instant.now());
        UserAccount savedDh2 = userAccountRepo.save(dh2);
        Employee dh2Emp = new Employee(orgId, savedDh2.getId(), depts.get("IT").getId(), "EMP-003", "Charlie", "ITHead", "depthead.it@assetflow.example");
        employees.add(employeeRepo.save(dh2Emp));

        // 3. Asset Managers (2)
        UserAccount am1 = new UserAccount(UUID.randomUUID(), "assetmanager.primary@assetflow.example", encodedPassword, orgId, UserStatus.ACTIVE, UserRole.ASSET_MANAGER, Instant.now(), Instant.now());
        UserAccount savedAm1 = userAccountRepo.save(am1);
        Employee am1Emp = new Employee(orgId, savedAm1.getId(), depts.get("IT").getId(), "EMP-004", "David", "AssetManager", "assetmanager.primary@assetflow.example");
        employees.add(employeeRepo.save(am1Emp));

        UserAccount am2 = new UserAccount(UUID.randomUUID(), "assetmanager.secondary@assetflow.example", encodedPassword, orgId, UserStatus.ACTIVE, UserRole.ASSET_MANAGER, Instant.now(), Instant.now());
        UserAccount savedAm2 = userAccountRepo.save(am2);
        Employee am2Emp = new Employee(orgId, savedAm2.getId(), depts.get("FIN").getId(), "EMP-005", "Emma", "AssetManagerSec", "assetmanager.secondary@assetflow.example");
        employees.add(employeeRepo.save(am2Emp));

        // 4. Regular Employees (20)
        List<Department> deptList = new ArrayList<>(depts.values());
        for (int i = 1; i <= 20; i++) {
            String email = String.format("employee%02d@assetflow.example", i);
            UserAccount user = new UserAccount(UUID.randomUUID(), email, encodedPassword, orgId, UserStatus.ACTIVE, UserRole.EMPLOYEE, Instant.now(), Instant.now());
            UserAccount savedUser = userAccountRepo.save(user);

            Department d = deptList.get((i - 1) % deptList.size());
            Employee emp = new Employee(orgId, savedUser.getId(), d.getId(), String.format("EMP-%03d", i + 5), "Employee" + i, "User", email);
            employees.add(employeeRepo.save(emp));
        }

        return employees;
    }

    private List<Asset> seedAssets(UUID orgId, Map<String, AssetCategory> categories) {
        List<Asset> assets = new ArrayList<>();
        List<AssetCategory> catList = new ArrayList<>(categories.values());

        for (int i = 1; i <= 50; i++) {
            AssetCategory cat = catList.get((i - 1) % catList.size());
            String tag = String.format("TAG-%04d", i);
            String serial = String.format("SN-%08X", i * 1234567);
            String name = cat.getName().replace("s", "") + " #" + i;
            String desc = "Production ready " + cat.getName() + " unit";
            String location = "Building A - Floor " + ((i % 4) + 1);

            Asset asset = new Asset(orgId, cat.getId(), tag, name, desc, serial, location);
            if (i % 5 == 0) {
                asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
            } else if (i % 7 == 0) {
                asset.setStatus(AssetStatus.DISPOSED);
            } else if (i % 2 == 0) {
                asset.setStatus(AssetStatus.ALLOCATED);
            }
            assets.add(assetRepo.save(asset));
        }
        return assets;
    }

    private void seedAllocations(UUID orgId, List<Asset> assets, List<Employee> employees, Map<String, Department> depts) {
        Instant now = Instant.now();

        // 1. Currently Active Allocation
        Asset activeAsset = assets.stream().filter(a -> a.getStatus() == AssetStatus.ALLOCATED).findFirst().orElse(assets.get(0));
        Employee emp = employees.get(5);
        AssetAllocation alloc1 = new AssetAllocation(orgId, activeAsset.getId(), emp.getId(), emp.getDepartmentId(), now.plus(30, ChronoUnit.DAYS), "Standard employee laptop issue");
        allocationRepo.save(alloc1);

        // 2. Overdue Allocation
        Asset overdueAsset = assets.stream().filter(a -> a.getStatus() == AssetStatus.ALLOCATED && !a.getId().equals(activeAsset.getId())).findFirst().orElse(assets.get(1));
        Employee emp2 = employees.get(6);
        AssetAllocation alloc2 = new AssetAllocation(orgId, overdueAsset.getId(), emp2.getId(), emp2.getDepartmentId(), now.minus(10, ChronoUnit.DAYS), "Overdue temp checkout");
        allocationRepo.save(alloc2);

        // 3. Returned Allocation
        Asset returnedAsset = assets.get(2);
        AssetAllocation alloc3 = new AssetAllocation(orgId, returnedAsset.getId(), employees.get(7).getId(), depts.get("ENG").getId(), now.minus(60, ChronoUnit.DAYS), "Returned upon upgrade");
        alloc3.markReturned("GOOD");
        allocationRepo.save(alloc3);
    }

    private void seedBookings(UUID orgId, List<Asset> assets, List<Employee> employees) {
        Instant now = Instant.now();
        Asset resource = assets.get(3);
        Employee emp = employees.get(8);

        // Past booking
        ResourceBooking past = new ResourceBooking(orgId, resource.getId(), emp.getId(), "Q2 Project Demo", now.minus(7, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        bookingRepo.save(past);

        // Current/Future booking
        ResourceBooking future = new ResourceBooking(orgId, resource.getId(), emp.getId(), "Client Presentation", now.plus(2, ChronoUnit.DAYS), now.plus(2, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS));
        bookingRepo.save(future);
    }

    private void seedMaintenance(UUID orgId, List<Asset> assets, List<Employee> employees) {
        Asset maintAsset = assets.stream().filter(a -> a.getStatus() == AssetStatus.UNDER_MAINTENANCE).findFirst().orElse(assets.get(4));
        Employee requester = employees.get(9);
        Employee tech = employees.get(3); // Asset Manager

        // Pending request
        MaintenanceRequest req1 = new MaintenanceRequest(orgId, assets.get(5).getId(), requester.getId(), "Screen Flickering", "Display flickers during heavy render load", "HIGH");
        maintenanceRepo.save(req1);

        // Approved request
        MaintenanceRequest req2 = new MaintenanceRequest(orgId, maintAsset.getId(), requester.getId(), "Battery Replacement", "Battery health degraded below 70%", "MEDIUM");
        req2.approve(tech.getId());
        maintenanceRepo.save(req2);

        // Completed request
        MaintenanceRequest req3 = new MaintenanceRequest(orgId, assets.get(6).getId(), requester.getId(), "RAM Upgrade 32GB", "Upgraded system memory for Docker workload", "LOW");
        req3.approve(tech.getId());
        req3.resolve("Replaced 16GB kit with 32GB Corsair DDR5 RAM kit");
        maintenanceRepo.save(req3);
    }

    private void seedAudits(UUID orgId, List<Asset> assets, Employee auditor) {
        AuditCycle cycle = new AuditCycle(orgId, "Q3 2026 IT Infrastructure Audit", "Annual physical asset audit across HQ");
        AuditCycle savedCycle = auditCycleRepo.save(cycle);

        for (int i = 0; i < 5; i++) {
            AuditItem item = new AuditItem(savedCycle.getId(), assets.get(i).getId());
            if (i % 2 == 0) {
                item.verify("GOOD", "VERIFIED", "Physical tag matched");
            } else {
                item.verify("NEEDS_REPAIR", "DISCREPANCY", "Scratched screen surface");
            }
            auditItemRepo.save(item);
        }
    }

    private void seedNotifications(UUID orgId, List<Employee> employees) {
        Employee emp = employees.get(5);
        Notification n1 = new Notification(orgId, emp.getId(), "Asset Allocation Confirmation", "Asset TAG-0001 has been assigned to your profile.", "INFO");
        Notification n2 = new Notification(orgId, emp.getId(), "Maintenance Approved", "Your battery replacement request #102 has been approved.", "SUCCESS");
        notificationRepo.save(n1);
        notificationRepo.save(n2);
    }
}
