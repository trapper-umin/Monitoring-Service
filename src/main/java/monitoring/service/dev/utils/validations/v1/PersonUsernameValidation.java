package monitoring.service.dev.utils.validations.v1;

import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.common.IValidator;

import java.util.Objects;

public class PersonUsernameValidation implements IValidator<CredentialsDTOReqst> {

    private static PersonUsernameValidation instance;

    private PersonUsernameValidation() {
    }

    public static PersonUsernameValidation getInstance() {
        if (instance == null) {
            instance = new PersonUsernameValidation();
        }
        return instance;
    }

    IPeopleRepository peopleRepository = new PeopleRepository();

    public void valid(CredentialsDTOReqst credentials) {
        String username = credentials.getUsername();

        Objects.requireNonNull(username, "username should not be null");

        String trimmedUsername = username.trim();
        if (trimmedUsername.isEmpty()) {
            throw new NotValidException("username should not be empty");
        }
        if (trimmedUsername.length() > 16) {
            throw new NotValidException("username must be less than or equal to 16 characters");
        }
        if (!username.matches("[a-zA-Z0-9]+")) {
            throw new NotValidException("username should only contain Latin letters and digits");
        }
        boolean isUsernameTaken = peopleRepository.findByUsername(username).isPresent();
        if (isUsernameTaken) {
            throw new NotValidException("username should be unique");
        }
    }
}