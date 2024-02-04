package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IAuthController;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.db.PeopleRepository;
import monitoring.service.dev.services.AuthService;

public abstract class ImplAuthController implements IAuthController {

    private final IPeopleRepository repository = new PeopleRepository();
    private final AuthService service = new AuthService(repository);

    @Override
    public Person registration(CredentialsDTO credentials) {
        return service.registration(credentials);
    }

    @Override
    public Person authentication(CredentialsDTO credentials) {
        return service.authentication(credentials);
    }
}