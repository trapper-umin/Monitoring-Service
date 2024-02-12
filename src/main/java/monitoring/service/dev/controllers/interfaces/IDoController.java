package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.History;

import java.util.List;

public interface IDoController {

    List<SensorDTO> getCurrentReadings(CredentialsDTOReqst credentials);

    List<SensorDTO> getMonthlyReadings(CredentialsDTOReqst credentials, String month, String year);

    List<History> getHistory(CredentialsDTOReqst credentials);

    void pushHistory(History history);

    void submitReading(CredentialsDTOWithSensorReqst credentials);
}
