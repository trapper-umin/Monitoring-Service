package monitoring.service.dev.controllers.impl;

import monitoring.service.dev.controllers.interfaces.IDoController;
import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.services.DoService;

import java.util.Map;

public abstract class ImplDoController implements IDoController {

    DoService service = DoService.getInstance();

    @Override
    public Map<SensorDTO, MeterReadingDTO> getCurrentReadings(Person credentials) {
        return service.getCurrentReadings(credentials);
    }

    @Override
    public Map<SensorDTO, MeterReadingDTO> getMonthlyReadings(Person credentials, String month) {
        return service.getMonthlyReadings(credentials, month);
    }

    @Override
    public void submitReading(SensorDTO sensor) {
        service.submitReading(sensor);
    }
}
