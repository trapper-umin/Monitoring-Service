package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;

import java.util.List;
import java.util.Map;

public interface IDoController {

    List<SensorDTO> getCurrentReadings(CredentialsDTO credentials);

    List<SensorDTO> getMonthlyReadings(CredentialsDTO credentials, String month, String year);

    List<History> getHistory(CredentialsDTO credentials);

    void pushHistory(History history);

    void submitReading(CredentialsDTO credentials);

}
