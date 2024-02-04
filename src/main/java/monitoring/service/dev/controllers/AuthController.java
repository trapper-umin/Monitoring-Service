package monitoring.service.dev.controllers;

import monitoring.service.dev.controllers.impl.ImplAuthController;

public class AuthController extends ImplAuthController {

    private static AuthController instance;

    private AuthController(){}

    public static AuthController getInstance(){
        if(instance==null){
            instance = new AuthController();
        }
        return instance;
    }
}
