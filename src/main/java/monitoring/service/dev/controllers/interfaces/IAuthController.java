package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.CredentialsDTO;
import monitoring.service.dev.models.Person;

public interface IAuthController {

    void registration(CredentialsDTO credentials);

    Person authentication(CredentialsDTO credentials);
}
