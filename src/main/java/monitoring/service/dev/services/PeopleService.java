package monitoring.service.dev.services;

import monitoring.service.dev.utils.validations.PersonPasswordValidation;
import monitoring.service.dev.utils.validations.PersonUsernameValidation;

public class PeopleService {

    private static PeopleService instance;

    private PeopleService(){}

    public static PeopleService getInstance(){
        if(instance==null){
            instance= new PeopleService();
        }
        return instance;
    }

    PersonUsernameValidation personUsernameValidation = new PersonUsernameValidation();
    PersonPasswordValidation personPasswordValidation = new PersonPasswordValidation();

    public void registration(String username, String password) {
        personUsernameValidation.valid(username);

    }

    //validation and etc
}
