package monitoring.service.dev.repositories.localstorage;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryRepository {

    private static HistoryRepository instance;

    private static final List<History> historyDB = new ArrayList<>();

    private HistoryRepository() {
    }

    public static HistoryRepository getInstance() {
        if (instance == null) {
            instance = new HistoryRepository();
        }
        return instance;
    }

    public void push(History history) {
        historyDB.add(history);
    }

    public List<History> get(CredentialsDTO credentials) {
        String username = credentials.getUsername();
        return historyDB.stream().filter(
                h -> h.getUsername().equals(username))
            .collect(Collectors.toList());
    }
}
