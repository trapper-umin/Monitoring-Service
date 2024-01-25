package monitoring.service.dev.utils.validations;

import monitoring.service.dev.utils.exceptions.NotValidException;

public class PersonPasswordValidation {

    private static PersonPasswordValidation instance;

    private PersonPasswordValidation(){

    }

    public static PersonPasswordValidation getInstance(){
        if(instance==null){
            instance=new PersonPasswordValidation();
        }
        return instance;
    }

    public void valid(String password){
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
