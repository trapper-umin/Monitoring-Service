package monitoring.service.dev.repositories;

import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;

import java.util.Map;
import java.util.Optional;

public interface IPeopleRepository {

    void registration(Person person);

    Optional<Person> findByUsername(String username);

    Map<Sensor, MeterReading> getCurrentReadings(Person credentials);

    void submitReading(Person credentials, Sensor sensor, MeterReading meterReading);
}
