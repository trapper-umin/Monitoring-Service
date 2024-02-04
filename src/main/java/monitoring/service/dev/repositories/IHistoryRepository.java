package monitoring.service.dev.repositories;

import java.util.List;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;

public interface IHistoryRepository {

    List<History> get(CredentialsDTO credentials);

    void push(History history);
}
