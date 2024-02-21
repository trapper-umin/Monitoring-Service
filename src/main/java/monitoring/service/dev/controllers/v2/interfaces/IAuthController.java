package monitoring.service.dev.controllers.v2.interfaces;

import jakarta.validation.Valid;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.responses.TokenDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuthController {

    @PostMapping(value = AppConstants.COMMAND_REGISTER, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<WrapperResp<TokenDTOResp>> registration(@Valid @RequestBody CredentialsDTOReqst credentials,
        BindingResult bindingResult);

    @PostMapping(value = AppConstants.COMMAND_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<WrapperResp<TokenDTOResp>> authentication(@Valid @RequestBody CredentialsDTOReqst credentials,
        BindingResult bindingResult);
}