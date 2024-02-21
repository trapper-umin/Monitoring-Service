package monitoring.service.dev.services.db;

import java.util.ArrayList;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import monitoring.service.dev.utils.validations.v1.PersonPasswordValidation;
import monitoring.service.dev.utils.validations.v1.PersonUsernameValidation;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final PersonUsernameValidation usernameValidation = PersonUsernameValidation.getInstance();
    private static final PersonPasswordValidation passwordValidation = PersonPasswordValidation.getInstance();
    private final IPeopleRepository repository;

    public AuthService(IPeopleRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers a new person in the system using the provided credentials. The method performs
     * validation of the username and password using defined validation rules. It then creates a new
     * Person object with default values for first name, last name, age, and an empty list of
     * sensors. The new person is assigned the role of USER.
     *
     * @param credentials The credentials object containing the username and password.
     * @return The newly created Person object with registered details.
     * @throws NotValidException if the provided credentials do not meet the validation criteria.
     */
    public Person registration(CredentialsDTOReqst credentials) throws ProblemWithSQLException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        Person person = Person.builder().username(username).password(password)
            .sensors(new ArrayList<>()).role(Role.USER).build();

        return repository.registration(person);
    }

    public Person authentication(CredentialsDTOReqst credentials) throws ProblemWithSQLException,
            NotFoundException, NotValidException{
        String username = credentials.getUsername();
        Person person = repository.findByUsername(username).orElseThrow(
            () -> new NotFoundException(username + "not found"));

        String password = credentials.getPassword();
        if (!password.equals(person.getPassword())) {
            throw new NotValidException("incorrect password");
        }

        return person;
    }
}
