package monitoring.service.dev.controllers.v1.impl;

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
import monitoring.service.dev.controllers.v1.interfaces.IAdminController;
import monitoring.service.dev.dtos.requests.AuthoritiesDTOReqst;
import monitoring.service.dev.dtos.responses.AuditDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.out.Sandler;
import monitoring.service.dev.repositories.jdbc.AdminRepository;
import monitoring.service.dev.repositories.jdbc.AuditRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.services.db.AdminService;
import monitoring.service.dev.services.db.AuditService;
import monitoring.service.dev.services.db.JWTService;
import monitoring.service.dev.utils.annotations.DoAudit;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.ForbiddenException;
import monitoring.service.dev.utils.exceptions.JWTException;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import monitoring.service.dev.utils.mappers.AuditMapper;
import org.mapstruct.factory.Mappers;

@Deprecated
@WebServlet("/api/v1/admins/*")
public class ImplAdminController extends HttpServlet implements IAdminController {

    private final AdminService adminService;
    private final AuditService auditService;
    private final JWTService jwtService;
    private final AuditMapper auditMapper;
    private final ObjectMapper jackson;
    private final Sandler sandler;

    public ImplAdminController() {
        PeopleRepository peopleRepository = new PeopleRepository();
        AdminRepository adminRepository = new AdminRepository();
        AuditRepository auditRepository = new AuditRepository();
        this.adminService = new AdminService(peopleRepository, adminRepository);
        this.auditService = new AuditService(auditRepository);
        this.jwtService = new JWTService(peopleRepository);
        this.auditMapper = Mappers.getMapper(AuditMapper.class);
        this.jackson = new ObjectMapper();
        this.sandler = new Sandler(jackson);
    }

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
        try {
            String token = jwtService.extractToken(req);
            Person person = jwtService.validate(token);
            if (person.getRole().equals(Role.USER)) {
                throw new ForbiddenException("Forbidden");
            }
            String path = req.getPathInfo();
            switch (path) {
                case AppConstants.COMMAND_RIGHTS -> {
                    try {
                        AuthoritiesDTOReqst authorities = jackson.readValue(req.getInputStream(),
                            AuthoritiesDTOReqst.class);
                        if (authorities.getUsername().equals(person.getUsername())) {
                            throw new IllegalArgumentException("You can't change your authorities");
                        }
                        processRights(resp, authorities);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Invalid request body");
                    }
                }
                case AppConstants.COMMAND_AUDIT -> processAudit(resp);
                default -> throw new IllegalArgumentException("Unknown request path");
            }
        } catch (IllegalArgumentException | NotFoundException | NotValidException |
                 CanNotDoException | JWTException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ForbiddenException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        } catch (Exception e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        }
    }

    private void processRights(HttpServletResponse resp, AuthoritiesDTOReqst authorities)
        throws NotValidException, CanNotDoException, ProblemWithSQLException {
        String action = authorities.getAction();
        switch (action) {
            case AppConstants.UPGRADE -> setAuthorities(authorities.getUsername());
            case AppConstants.DOWNGRADE -> deleteAuthorities(authorities.getUsername());
            default -> throw new NotValidException("Incorrect action (upgrade or downgrade)");
        }
        List<UserDTOResp> users = getAllUsers();
        sandler.sendSuccessResponse(resp,
            new WrapperResp<>(HttpServletResponse.SC_OK, "all users", LocalDateTime.now(), users));
    }

    private void processAudit(HttpServletResponse resp) throws ProblemWithSQLException {
        List<Audit> audits = getAudit();
        List<AuditDTOResp> auditDTO = auditMapper.convertToAuditDTOList(audits);
        sandler.sendSuccessResponse(resp,
            new WrapperResp<>(HttpServletResponse.SC_OK, "get system audit", LocalDateTime.now(),
                auditDTO));
    }
}