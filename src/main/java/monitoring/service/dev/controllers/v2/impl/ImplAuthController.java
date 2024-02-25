package monitoring.service.dev.controllers.v2.impl;

import monitoring.service.dev.controllers.v2.interfaces.IAuthController;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.responses.TokenDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.services.logic.AuthLogicService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public abstract class ImplAuthController implements IAuthController {

    private final AuthLogicService service;

    public ImplAuthController(AuthLogicService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<WrapperResp<TokenDTOResp>> registration(CredentialsDTOReqst credentials,
        BindingResult bindingResult) {
        return service.registration(credentials, bindingResult);
    }

    @Override
    public ResponseEntity<WrapperResp<TokenDTOResp>> authentication(CredentialsDTOReqst credentials,
        BindingResult bindingResult) {
        return service.authentication(credentials, bindingResult);
    }
}
