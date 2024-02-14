package monitoring.service.dev.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.controllers.interfaces.IAuthController;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.responses.TokenDTOResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.out.Sandler;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.services.AuthService;
import monitoring.service.dev.services.JWTService;
import monitoring.service.dev.utils.annotations.DoAudit;
import monitoring.service.dev.utils.annotations.SpeedTest;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

@WebServlet("/auth/*")
public class ImplAuthController extends HttpServlet implements IAuthController {

    private final AuthService service;
    private final JWTService jwtService;
    private final ObjectMapper jackson;
    private final Sandler sandler;

    public ImplAuthController() {
        IPeopleRepository repository = new PeopleRepository();
        this.jackson = new ObjectMapper();
        this.sandler = new Sandler(jackson);
        this.service = new AuthService(repository);
        this.jwtService = new JWTService(repository);
    }

    @DoAudit
    @SpeedTest
    @Override
    public Person registration(CredentialsDTOReqst credentials) {
        return service.registration(credentials);
    }

    @DoAudit
    @SpeedTest
    @Override
    public Person authentication(CredentialsDTOReqst credentials) {
        return service.authentication(credentials);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String path = req.getPathInfo();
            CredentialsDTOReqst credentials = jackson.readValue(req.getInputStream(),
                CredentialsDTOReqst.class);
            processRequest(path, credentials, resp);
        } catch (IOException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid request data");
        } catch (IllegalArgumentException | NotFoundException | NotValidException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ProblemWithSQLException e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage());
        } catch (Exception e) {
            sandler.sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        }
    }

    private void processRequest(String path, CredentialsDTOReqst credentials,
        HttpServletResponse resp) {
        Person person;
        if (AppConstants.COMMAND_REGISTER.equals(path)) {
            person = registration(credentials);
        } else if (AppConstants.COMMAND_LOGIN.equals(path)) {
            person = authentication(credentials);
        } else {
            throw new IllegalArgumentException("Unknown path");
        }
        sandler.sendSuccessResponse(resp,
            TokenDTOResp.builder().token(jwtService.generate(person.getUsername())).build());
    }
}