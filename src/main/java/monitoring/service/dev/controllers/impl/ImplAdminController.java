package monitoring.service.dev.controllers.impl;

import java.util.List;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.controllers.interfaces.IAdminController;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.repositories.jdbc.AdminRepository;
import monitoring.service.dev.repositories.jdbc.AuditRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.services.AdminService;
import monitoring.service.dev.services.AuditService;

public class ImplAdminController implements IAdminController {

    private final PeopleRepository peopleRepository = new PeopleRepository();
    private final AdminRepository adminRepository = new AdminRepository(
        AppConstants.JDBC_URL, AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
    private final AuditRepository auditRepository = new AuditRepository();
    private final AdminService adminService = new AdminService(peopleRepository, adminRepository);
    private final AuditService auditService = new AuditService(auditRepository);

    @Override
    public List<CredentialsDTOResp> getAllUsers() {
        return adminService.getAllUsers();
    }

    @Override
    public void setAuthorities(String username) {
        adminService.setAuthorities(username);
    }

    @Override
    public void deleteAuthorities(String username) {
        adminService.deleteAuthorities(username);
    }

    @Override
    public List<Audit> getAudit() {
        return auditService.getAudit();
    }

    @Override
    public void postAudit(Audit audit) {
        auditService.postAudit(audit);
    }
}