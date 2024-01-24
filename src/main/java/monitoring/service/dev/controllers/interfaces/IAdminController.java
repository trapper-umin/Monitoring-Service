package monitoring.service.dev.controllers.interfaces;

public interface IAdminController {

    void getAllMeterReadings();

    void getHistoryOfMeterReadings();

    void setAuthorities(String username);

    void deleteAuthorities(String username);
}
