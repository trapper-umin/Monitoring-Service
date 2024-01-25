package monitoring.service.dev.controllers;

import monitoring.service.dev.controllers.impl.ImplDoController;

public class DoController extends ImplDoController {

    private static DoController instance;

    private DoController(){}

    public static DoController getInstance(){
        if(instance==null){
            instance = new DoController();
        }
        return instance;
    }
}
