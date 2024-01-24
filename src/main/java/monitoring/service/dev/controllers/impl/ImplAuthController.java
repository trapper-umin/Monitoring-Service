package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IAuthController;
import monitoring.service.dev.services.PeopleService;

public abstract class ImplAuthController implements IAuthController {

    PeopleService service = PeopleService.getInstance();

    @Override
    public void registration(String username, String password) {
        service.registration(username,password);
    }

    @Override
    public void authentication(String username, String password) {

    }
}
