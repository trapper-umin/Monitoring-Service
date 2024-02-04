package monitoring.service.dev.services;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.HistoryRepository;

import java.util.List;

public class PeopleHistoryService {

    private static PeopleHistoryService instance;
    private static final HistoryRepository repository = HistoryRepository.getInstance();

    private PeopleHistoryService(){}

    public static PeopleHistoryService getInstance(){
        if(instance==null){
            instance = new PeopleHistoryService();
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
