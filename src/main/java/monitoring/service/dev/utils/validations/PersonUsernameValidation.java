package monitoring.service.dev.utils.validations;

import monitoring.service.dev.repositories.PeopleRepository;
import monitoring.service.dev.utils.exceptions.NotValidException;

public class PersonUsernameValidation {

    PeopleRepository repository = PeopleRepository.getInstance();

    public void valid(String username){
        if(username.isEmpty()){
            throw new NotValidException("username should be not empty");
        }
        if(username.length()>16){
            throw new NotValidException("username must be less than or equal to 16");
        }
        if(repository.findByUsername(username).isPresent()){
            throw new NotValidException("username should be unique");
        }
    }
}
