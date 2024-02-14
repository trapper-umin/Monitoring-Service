package monitoring.service.dev.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.controllers.interfaces.IAdminController;
import monitoring.service.dev.dtos.requests.AuthoritiesDTOReqst;
import monitoring.service.dev.dtos.responses.AuditDTOResp;
import monitoring.service.dev.dtos.responses.CommonResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.out.Sandler;
import monitoring.service.dev.repositories.jdbc.AdminRepository;
import monitoring.service.dev.repositories.jdbc.AuditRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.services.AdminService;
import monitoring.service.dev.services.AuditService;
import monitoring.service.dev.services.JWTService;
import monitoring.service.dev.utils.annotations.DoAudit;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.JWTException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import monitoring.service.dev.utils.mappers.AuditMapper;
import org.mapstruct.factory.Mappers;

@WebServlet("/admin/*")
public class ImplAdminController extends HttpServlet implements IAdminController {

    private final PeopleRepository peopleRepository = new PeopleRepository();
    private final AdminRepository adminRepository = new AdminRepository(AppConstants.JDBC_URL,
        AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
    private final AuditRepository auditRepository = new AuditRepository();
    private final AdminService adminService = new AdminService(peopleRepository, adminRepository);
    private final AuditService auditService = new AuditService(auditRepository);
    private final JWTService jwtService = new JWTService(peopleRepository);
    private final AuditMapper auditMapper = Mappers.getMapper(AuditMapper.class);
    private final ObjectMapper jackson = new ObjectMapper();
    private final Sandler sandler = new Sandler(jackson);

    @DoAudit
    @Override
    public List<UserDTOResp> getAllUsers() {
        return adminService.getAllUsers();
    }

    @DoAudit
    @Override
    public void setAuthorities(String username) {
        adminService.setAuthorities(username);
    }

    @DoAudit
    @Override
    public void deleteAuthorities(String username) {
        adminService.deleteAuthorities(username);
    }

    @DoAudit
    @Override
    public List<Audit> getAudit() {
        return auditService.getAudit();
    }

    @Override
    public void postAudit(Audit audit) {
        auditService.postAudit(audit);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        process(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                "Authorization token is required");
            return;
        }

        token = token.substring(7);
        Person person = validateToken(resp, token);
        if (person == null) {
            return;
        }

        if (person.getRole().equals(Role.USER)) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        String path = req.getPathInfo();
        switch (path) {
            case "/rights" -> {
                AuthoritiesDTOReqst authorities;
                try {
                    authorities = jackson.readValue(req.getInputStream(),
                        AuthoritiesDTOReqst.class);
                } catch (IOException e) {
                    sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request body");
                    return;
                }
                processRights(resp, authorities);
            }
            case "/audit" -> processAudit(resp);
            default -> sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                "Unknown request path");
        }
    }

    private void processRights(HttpServletResponse resp, AuthoritiesDTOReqst authorities) {
        try {
            String action = authorities.getAction();
            switch (action) {
                case AppConstants.UPGRADE -> setAuthorities(authorities.getUsername());
                case AppConstants.DOWNGRADE -> deleteAuthorities(authorities.getUsername());
                default -> throw new NotValidException("Incorrect action (upgrade or downgrade)");
            }
            List<UserDTOResp> users = getAllUsers();
            sandler.sendSuccessResponse(resp,
                new CommonResp<>(HttpServletResponse.SC_OK, "all users", LocalDateTime.now(),
                    users));
        } catch (NotValidException | CanNotDoException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        }
    }

    private void processAudit(HttpServletResponse resp) {
        try {
            List<Audit> audits = getAudit();
            List<AuditDTOResp> auditDTO = auditMapper.convertToAuditDTOList(audits);
            sandler.sendSuccessResponse(resp,
                new CommonResp<>(HttpServletResponse.SC_OK, "get system audit", LocalDateTime.now(),
                    auditDTO));
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        }
    }

    private Person validateToken(HttpServletResponse resp, String token) {
        try {
            return jwtService.validate(token);
        } catch (NotFoundException | JWTException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        }
        return null;
    }
}