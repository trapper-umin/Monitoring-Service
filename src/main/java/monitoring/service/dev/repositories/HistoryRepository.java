package monitoring.service.dev.repositories;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryRepository {

    private static HistoryRepository instance;

    private static final List<History> historyDB = new ArrayList<>();

    private HistoryRepository(){}

    public static HistoryRepository getInstance(){
        if(instance==null){
            instance = new HistoryRepository();
        }
        return instance;
    }

    public void push(History history){
        historyDB.add(history);
    }

    public List<History> get(CredentialsDTO credentials){
        return historyDB.stream()
                .filter(h -> h.getCredentials().getUsername().equals(credentials.getUsername()) &&
                        h.getCredentials().getPassword().equals(credentials.getPassword()))
                .collect(Collectors.toList());
    }
}
