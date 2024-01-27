package monitoring.service.dev.repositories;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IPeopleRepository {

    void registration(Person person);

    Optional<Person> findByUsername(String username);

    List<Sensor> getCurrentReadings(Person credentials);

    List<Sensor> getMonthlyReadings(Person credentials, String month, String year);

    void submitReading(Person credentials);

    List<Person> getAllUsers();

    void setAuthorities(Person person);

    void deleteAuthorities(Person person);
}
