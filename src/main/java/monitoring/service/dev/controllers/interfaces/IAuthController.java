package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.models.Person;

public interface IAuthController {

    Person registration(CredentialsDTOReqst credentials);

    Person authentication(CredentialsDTOReqst credentials);
}