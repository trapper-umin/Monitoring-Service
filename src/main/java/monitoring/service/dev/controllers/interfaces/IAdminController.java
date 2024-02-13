package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.models.Audit;

import java.util.List;

public interface IAdminController {

    List<UserDTOResp> getAllUsers();

    void setAuthorities(String username);

    void deleteAuthorities(String username);

    List<Audit> getAudit();

    void postAudit(Audit audit);
}
