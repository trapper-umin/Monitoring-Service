package monitoring.service.dev.controllers.interfaces;

public interface IDoController {

    void getMeterReadings(String username);

    void sendMeterReadings();
}
