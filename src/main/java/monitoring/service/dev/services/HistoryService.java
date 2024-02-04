package monitoring.service.dev.services;

import java.util.List;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.IHistoryRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class HistoryService {

    private final IHistoryRepository repository;

    public HistoryService(IHistoryRepository repository) {
        this.repository = repository;
    }

    public void push(History history) {
        try {
            repository.push(history);
        } catch (ProblemWithSQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<History> get(CredentialsDTO credentials) {
        return repository.get(credentials);
    }
}
