package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IDoController;
import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.services.DoService;
import monitoring.service.dev.services.PeopleHistoryService;

import java.util.List;
import java.util.Map;

public abstract class ImplDoController implements IDoController {

    private static final DoService service = DoService.getInstance();
    private static final PeopleHistoryService phService =PeopleHistoryService.getInstance();

    @Override
    public List<SensorDTO> getCurrentReadings(CredentialsDTO credentials) {
        return service.getCurrentReadings(credentials);
    }

    @Override
    public List<SensorDTO> getMonthlyReadings(CredentialsDTO credentials, String month, String year) {
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
