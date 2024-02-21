package monitoring.service.dev.controllers.v2.interfaces;

import jakarta.validation.Valid;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.requests.AuthoritiesDTOReqst;
import monitoring.service.dev.dtos.responses.AuditDTOResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IAdminController {

    @PostMapping(value = AppConstants.COMMAND_RIGHTS, produces = MediaType.APPLICATION_JSON_VALUE, headers = {
        "Authorization"})
    ResponseEntity<WrapperResp<UserDTOResp>> rights(@RequestHeader("Authorization") String token,
        @Valid @RequestBody AuthoritiesDTOReqst authorities, BindingResult bindingResult);

    @GetMapping(value = AppConstants.COMMAND_AUDIT, produces = MediaType.APPLICATION_JSON_VALUE, headers = {
        "Authorization"})
    ResponseEntity<WrapperResp<AuditDTOResp>> getAudit(@RequestHeader("Authorization") String token);
}