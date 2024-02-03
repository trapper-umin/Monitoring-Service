package monitoring.service.dev.services;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.RepositoryFactory;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.PersonPasswordValidation;
import monitoring.service.dev.utils.validations.PersonUsernameValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AuthService {

    private static final PersonUsernameValidation puValidation = PersonUsernameValidation.getInstance();
    private static final PersonPasswordValidation ppValidation = PersonPasswordValidation.getInstance();
    private static final IPeopleRepository repository = RepositoryFactory.getRepository();

    private static AuthService instance;

    private AuthService(){}

    public static AuthService getInstance(){
        if(instance==null){
            instance= new AuthService();
        }
        return instance;
    }

    /**
     * Registers a new person in the system using the provided credentials.
     * The method performs validation of the username and password using
     * defined validation rules. It then creates a new Person object with
     * default values for first name, last name, age, and an empty list of sensors.
     * The new person is assigned the role of USER.
     *
     * @param credentials The credentials object containing the username and password.
     * @return The newly created Person object with registered details.
     * @throws NotValidException if the provided credentials do not meet the validation criteria.
     */
    public Person registration(CredentialsDTO credentials) {

        String username = credentials.getUsername();
        puValidation.valid(credentials);

        String password = credentials.getPassword();
        ppValidation.valid(credentials);

        Person person = Person.builder()
                .username(username)
                .password(password)
                .sensors(new ArrayList<>())
                .role(Role.USER)
                .build();

        repository.registration(person);

        return person;
    }

    public Person authentication(CredentialsDTO credentials) {

        String username = credentials.getUsername();
        Person person = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with username '"+username+"' was not found"));

        String password = credentials.getPassword();
        if(!password.equals(person.getPassword())){
            throw new NotValidException("incorrect password");
        }

        return person;
    }
}
