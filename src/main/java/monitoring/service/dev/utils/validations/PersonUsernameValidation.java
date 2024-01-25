package monitoring.service.dev.utils.validations;

import monitoring.service.dev.repositories.Repository;
import monitoring.service.dev.utils.exceptions.NotValidException;

import java.util.Objects;

public class PersonUsernameValidation {

    private static PersonUsernameValidation instance;

    private PersonUsernameValidation(){}

    public static PersonUsernameValidation getInstance(){
        if(instance==null){
            instance=new PersonUsernameValidation();
        }
        return instance;
    }

    Repository repository = Repository.getInstance();

    public void valid(String username){
        Objects.requireNonNull(username, "username should not be null");

        String trimmedUsername = username.trim();
        if(trimmedUsername.isEmpty()) {
            throw new NotValidException("username should not be empty");
        }
        if(trimmedUsername.length() > 16) {
            throw new NotValidException("username must be less than or equal to 16 characters");
        }
        if(!username.matches("[a-zA-Z0-9]+")){
            throw new NotValidException("username should only contain Latin letters and digits");
        }
        boolean isUsernameTaken = repository.findByUsername(username).isPresent();
        if(isUsernameTaken) {
            throw new NotValidException("username should be unique");
        }
    }
}
