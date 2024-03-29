package monitoring.service.dev.utils.validations.v1;

import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.common.IValidator;

public class PersonPasswordValidation implements IValidator<CredentialsDTOReqst> {

    private static PersonPasswordValidation instance;

    private PersonPasswordValidation() {

    }

    public static PersonPasswordValidation getInstance() {
        if (instance == null) {
            instance = new PersonPasswordValidation();
        }
        return instance;
    }

    public void valid(CredentialsDTOReqst credentials) {
        String password = credentials.getPassword();

        if (password == null || password.trim().isEmpty()) {
            throw new NotValidException("password should not be empty");
        }
        if (password.length() > 16) {
            throw new NotValidException("password must be less than or equal to 16 characters");
        }
        if (!password.matches("[a-zA-Z0-9]+")) {
            throw new NotValidException("password should only contain Latin letters and digits");
        }
    }
}