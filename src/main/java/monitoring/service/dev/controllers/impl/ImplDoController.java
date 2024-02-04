package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IDoController;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.db.PeopleRepository;
import monitoring.service.dev.repositories.db.ReadingsRepository;
import monitoring.service.dev.services.DoService;
import monitoring.service.dev.services.HistoryService;

import java.util.List;

public abstract class ImplDoController implements IDoController {

    private final PeopleRepository peopleRepository = new PeopleRepository();
    private final ReadingsRepository readingsRepository = new ReadingsRepository();
    private final DoService service = new DoService(peopleRepository, readingsRepository);
    private static final HistoryService phService = HistoryService.getInstance();

    @Override
    public List<SensorDTO> getCurrentReadings(CredentialsDTO credentials) {
        return service.getCurrentReadings(credentials);
    }

    @Override
    public List<SensorDTO> getMonthlyReadings(CredentialsDTO credentials, String month,
        String year) {
        return service.getMonthlyReadings(credentials, month, year);
    }

    @Override
    public List<History> getHistory(CredentialsDTO credentials) {
        return phService.get(credentials);
    }

    @Override
    public void pushHistory(History history) {
        phService.push(history);
    }

    @Override
    public void submitReading(CredentialsDTO credentials) {
        service.submitReading(credentials);
    }
}
