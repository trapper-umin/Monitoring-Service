package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IAdminController;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.services.AdminService;
import monitoring.service.dev.services.AuditService;

import java.util.List;

public abstract class ImplAdminController implements IAdminController {

    private static final AdminService service = AdminService.getInstance();
    private static final AuditService aService = AuditService.getInstance();

    @Override
    public List<CredentialsDTO> getAllUsers() {
        return service.getAllUsers();
    }

    @Override
    public void setAuthorities(String username) {
        service.setAuthorities(username);
    }

    @Override
    public void deleteAuthorities(String username) {
        service.deleteAuthorities(username);
    }

    @Override
    public List<Audit> getAudit() {
        return aService.getAudit();
    }

    @Override
    public void postAudit(Audit audit) {
        aService.postAudit(audit);
    }
}
