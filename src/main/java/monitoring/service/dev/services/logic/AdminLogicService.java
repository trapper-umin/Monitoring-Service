package monitoring.service.dev.services.logic;

import static monitoring.service.dev.utils.Handler.handleErrors;

import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.requests.AuthoritiesDTOReqst;
import monitoring.service.dev.dtos.responses.AuditDTOResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.services.db.AdminService;
import monitoring.service.dev.services.db.AuditService;
import monitoring.service.dev.services.db.JWTService;
import monitoring.service.dev.utils.exceptions.ForbiddenException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.mappers.AuditMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class AdminLogicService {

    private final JWTService jwtService;
    private final AdminService adminService;
    private final AuditService auditService;
    private final AuditMapper auditMapper;

    public AdminLogicService(JWTService jwtService, AdminService adminService,
        AuditService auditService, AuditMapper auditMapper) {
        this.jwtService = jwtService;
        this.adminService = adminService;
        this.auditService = auditService;
        this.auditMapper = auditMapper;
    }

    public ResponseEntity<WrapperResp<UserDTOResp>> rights(String token,
        AuthoritiesDTOReqst authorities, BindingResult bindingResult) {
        handleErrors(bindingResult);
        token = jwtService.extractToken(token);
        Person person = jwtService.validate(token);
        isAvailable(person);

        String username = authorities.getUsername();
        if (person.getUsername().equals(username)) {
            throw new IllegalArgumentException("can't change your authorities");
        }
        String action = authorities.getAction();
        switch (action) {
            case AppConstants.UPGRADE -> adminService.setAuthorities(username);
            case AppConstants.DOWNGRADE -> adminService.deleteAuthorities(username);
            default -> throw new NotValidException("incorrect action");
        }
        List<UserDTOResp> people = adminService.getAllUsers();
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_OK, "OK", LocalDateTime.now(), people),
            HttpStatus.OK);
    }

    public ResponseEntity<WrapperResp<AuditDTOResp>> getAudit(String token) {
        token = jwtService.extractToken(token);
        Person person = jwtService.validate(token);
        isAvailable(person);

        List<AuditDTOResp> audits = auditMapper.convertToAuditDTOList(auditService.getAudit());
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_OK, "OK", LocalDateTime.now(), audits),
            HttpStatus.OK);
    }

    private void isAvailable(Person person) {
        if (person.getRole().equals(Role.USER)) {
            throw new ForbiddenException("forbidden");
        }
    }
}