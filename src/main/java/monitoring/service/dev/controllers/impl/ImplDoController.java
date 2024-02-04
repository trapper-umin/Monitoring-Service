package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IDoController;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.jdbc.HistoryRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.repositories.jdbc.ReadingsRepository;
import monitoring.service.dev.services.DoService;
import monitoring.service.dev.services.HistoryService;

import java.util.List;

public abstract class ImplDoController implements IDoController {

    private final PeopleRepository peopleRepository = new PeopleRepository();
    private final ReadingsRepository readingsRepository = new ReadingsRepository();
    private final HistoryRepository historyRepository = new HistoryRepository();
    private final DoService doService = new DoService(peopleRepository, readingsRepository);
    private final HistoryService historyService = new HistoryService(historyRepository);

    @Override
    public List<SensorDTO> getCurrentReadings(CredentialsDTO credentials) {
        return doService.getCurrentReadings(credentials);
    }

    @Override
    public List<SensorDTO> getMonthlyReadings(CredentialsDTO credentials, String month,
        String year) {
        return doService.getMonthlyReadings(credentials, month, year);
    }

    @Override
    public List<History> getHistory(CredentialsDTO credentials) {
        return historyService.get(credentials);
    }

    @Override
    public void pushHistory(History history) {
        historyService.push(history);
    }

    @Override
    public void submitReading(CredentialsDTO credentials) {
        doService.submitReading(credentials);
    }
}
