package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;

import java.util.List;

public interface IDoController {

    List<SensorDTO> getCurrentReadings(CredentialsDTO credentials);

    List<SensorDTO> getMonthlyReadings(CredentialsDTO credentials, String month, String year);

    List<History> getHistory(CredentialsDTO credentials);

    void pushHistory(History history);

    void submitReading(CredentialsDTO credentials);

}
