package monitoring.service.dev.services;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.dtos.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.Repository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.NotValidException;
import monitoring.service.dev.utils.validations.PersonPasswordValidation;
import monitoring.service.dev.utils.validations.PersonUsernameValidation;

import java.time.LocalDateTime;

public class AuthService {

    private static AuthService instance;

    private AuthService(){}

    public static AuthService getInstance(){
        if(instance==null){
            instance= new AuthService();
        }
        return instance;
    }

    PersonUsernameValidation puValidation = PersonUsernameValidation.getInstance();
    PersonPasswordValidation ppValidation = PersonPasswordValidation.getInstance();
    Repository repository = Repository.getInstance();

    public Person registration(CredentialsDTO credentials) {

        String username = credentials.getUsername();
        puValidation.valid(username);

        String password = credentials.getPassword();
        ppValidation.valid(password);

        Person person = Person.builder()
                .username(username)
                .password(password)
                .firstName(null)
                .lastName(null)
                .age(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
