package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Audit;

import java.util.List;

public interface IAdminController {

    List<CredentialsDTO> getAllUsers();

    void setAuthorities(String username);

    void deleteAuthorities(String username);

    List<Audit> getAudit();

    void postAudit(Audit audit);
}
