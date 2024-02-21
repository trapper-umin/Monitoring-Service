package monitoring.service.dev.controllers.v1.interfaces;

import java.util.List;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.models.Audit;

@Deprecated
public interface IAdminController {

    List<UserDTOResp> getAllUsers();

    void setAuthorities(String username);

    void deleteAuthorities(String username);

    List<Audit> getAudit();

    void postAudit(Audit audit);
}
