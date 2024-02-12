package monitoring.service.dev.repositories;

import java.util.List;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.History;

public interface IHistoryRepository {

    List<History> get(CredentialsDTOReqst credentials);

    void push(History history);
}
