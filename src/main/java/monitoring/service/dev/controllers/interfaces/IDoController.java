package monitoring.service.dev.controllers.interfaces;

import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.Person;

import java.util.List;
import java.util.Map;

public interface IDoController {

    Map<SensorDTO, MeterReadingDTO> getCurrentReadings(Person credentials);

    Map<SensorDTO, MeterReadingDTO> getMonthlyReadings(Person credentials, String month);

    void submitReading(SensorDTO sensor);

}
