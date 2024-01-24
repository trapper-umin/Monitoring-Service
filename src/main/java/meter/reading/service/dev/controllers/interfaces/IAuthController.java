package meter.reading.service.dev.controllers.interfaces;

public interface IAuthController {

    void registration(String username, String password);

    void authentication(String username, String password);
}
