package monitoring.service.dev.services;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.IHistoryRepository;

import java.util.List;
import monitoring.service.dev.repositories.jdbc.HistoryRepository;

public class HistoryService {

    private static HistoryService instance;
    private static final IHistoryRepository repository = new HistoryRepository();

    private HistoryService(){}

    public static HistoryService getInstance(){
        if(instance==null){
            instance = new HistoryService();
        }
        return instance;
    }

    public void push(History history){
        repository.push(history);
    }

    public List<History> get(CredentialsDTO credentials){
        return repository.get(credentials);
    }
}
