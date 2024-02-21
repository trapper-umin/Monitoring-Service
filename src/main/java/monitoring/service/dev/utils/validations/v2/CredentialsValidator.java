package monitoring.service.dev.utils.validations.v2;

import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CredentialsValidator implements Validator {

    private final IPeopleRepository repository;

    public CredentialsValidator(PeopleRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CredentialsDTOReqst.class);
    }

    @Override
    public void validate(Object target, Errors errors) throws ProblemWithSQLException {
        CredentialsDTOReqst credentials = (CredentialsDTOReqst) target;
        if (repository.findByUsername(credentials.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "should be unique");
        }
    }
}