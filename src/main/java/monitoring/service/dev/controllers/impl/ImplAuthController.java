package monitoring.service.dev.controllers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.controllers.interfaces.IAuthController;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.responses.TokenDTOResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.services.AuthService;
import monitoring.service.dev.services.JWTService;

@WebServlet("/auth/*")
public class ImplAuthController extends HttpServlet implements IAuthController {

    private final IPeopleRepository repository = new PeopleRepository();
    private final AuthService service = new AuthService(repository);
    private final JWTService jwtService = new JWTService(repository);
    private final ObjectMapper jackson = new ObjectMapper();

    @Override
    public Person registration(CredentialsDTOReqst credentials) {
        return service.registration(credentials);
    }

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
            Person person = null;

            if ("/reg".equals(path)) {
                person = registration(credentials);
            } else if ("/login".equals(path)) {
                person = authentication(credentials);
            } else {
                throw new IllegalArgumentException("Unknown path");
            }

            resp.setContentType("application/json");
            jackson.writeValue(resp.getOutputStream(),
                TokenDTOResp.builder().token(jwtService.generate(person.getUsername())).build());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                jackson.writeValue(resp.getOutputStream(),
                    Collections.singletonMap("error", e.getMessage()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}