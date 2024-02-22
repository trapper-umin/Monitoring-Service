package monitoring.service.dev.services.logic;

import static monitoring.service.dev.utils.Handler.handleErrors;

import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.responses.TokenDTOResp;
import monitoring.service.dev.dtos.responses.WrapperResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.services.db.AuthService;
import monitoring.service.dev.services.db.JWTService;
import monitoring.service.dev.utils.validations.v2.CredentialsValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class AuthLogicService {

    private final CredentialsValidator validator;
    private final AuthService authService;
    private final JWTService jwtService;

    public AuthLogicService(CredentialsValidator validator, AuthService authService,
        JWTService jwtService) {

        this.validator = validator;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public ResponseEntity<WrapperResp<TokenDTOResp>> registration(CredentialsDTOReqst credentials,
        BindingResult bindingResult) {
        validator.validate(credentials, bindingResult);
        handleErrors(bindingResult);

        Person person = authService.registration(credentials);
        String token = jwtService.generate(person.getUsername());

        return createResponse(token);
    }

    public ResponseEntity<WrapperResp<TokenDTOResp>> authentication(CredentialsDTOReqst credentials,
        BindingResult bindingResult) {
        handleErrors(bindingResult);

        Person person = authService.authentication(credentials);
        String token = jwtService.generate(person.getUsername());

        return createResponse(token);
    }

    private ResponseEntity<WrapperResp<TokenDTOResp>> createResponse(String token) {
        return new ResponseEntity<>(
            new WrapperResp<>(HttpServletResponse.SC_OK, "OK", LocalDateTime.now(),
                List.of(TokenDTOResp.builder().token(token).build())), HttpStatus.OK);
    }
}