package monitoring.service.dev.controllers.v2.impl;

import monitoring.service.dev.controllers.v2.interfaces.IAdminController;
import monitoring.service.dev.dtos.requests.AuthoritiesDTOReqst;
import monitoring.service.dev.dtos.responses.AuditDTOResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.services.logic.AdminLogicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public abstract class ImplAdminController implements IAdminController {

    private final AdminLogicService service;

    protected ImplAdminController(AdminLogicService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<WrapperResp<UserDTOResp>> rights(String token, AuthoritiesDTOReqst authorities,
        BindingResult bindingResult) {
        return service.rights(token, authorities, bindingResult);
    }

    @Override
    public ResponseEntity<WrapperResp<AuditDTOResp>> getAudit(String token) {
        return service.getAudit(token);
    }
}
