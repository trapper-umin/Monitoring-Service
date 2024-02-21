package monitoring.service.dev.controllers.v1.interfaces;

import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.models.Person;

@Deprecated
public interface IAuthController {

    Person registration(CredentialsDTOReqst credentials);

    Person authentication(CredentialsDTOReqst credentials);
}