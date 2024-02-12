package monitoring.service.dev.repositories.localstorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.History;

@Deprecated
public class HistoryRepository {

    private static final List<History> historyDB = new ArrayList<>();
    private static HistoryRepository instance;

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

    public List<History> get(CredentialsDTOResp credentials) {
        String username = credentials.getUsername();
        return historyDB.stream().filter(h -> h.getUsername().equals(username))
            .collect(Collectors.toList());
    }
}
