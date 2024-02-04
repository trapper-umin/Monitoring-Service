package monitoring.service.dev.repositories;

import java.util.List;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;

public interface IReadingsRepository {

    List<Sensor> getCurrentReadings(Person credentials);

    List<Sensor> getMonthlyReadings(Person credentials, String month, String year);

    void submitReading(Person credentials);
}
