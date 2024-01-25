package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IAuthController;
import monitoring.service.dev.dtos.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.services.AuthService;

public abstract class ImplAuthController implements IAuthController {

    AuthService service = AuthService.getInstance();

    @Override
    public void registration(CredentialsDTO credentials) {
        service.registration(credentials);
    }

    @Override
    public Person authentication(CredentialsDTO credentials) {
        return service.authentication(credentials);
    }
}
