package monitoring.service.dev.services.db;

import java.util.List;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.IHistoryRepository;
import monitoring.service.dev.repositories.jdbc.HistoryRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    private final IHistoryRepository repository;

    public HistoryService(HistoryRepository repository) {
        this.repository = repository;
    }

    public void push(History history) {
        try {
            repository.push(history);
        } catch (ProblemWithSQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<History> get(CredentialsDTOReqst credentials) {
        return repository.get(credentials);
    }

    public List<History> get(String username) {
        return repository.get(CredentialsDTOReqst.builder().username(username).build());
    }
}
