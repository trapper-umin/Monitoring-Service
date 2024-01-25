package monitoring.service.dev.repositories;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Repository {
    private static Repository instance;
    Map<Person, List<Sensor>> personToSensors;
    Map<Sensor, List<MeterReading>> sensorToReadings;

    private Repository() {
        personToSensors = new HashMap<>();
        sensorToReadings= new HashMap<>();

        Person person = Person.builder()
                .username("root")
                .password("root")
                .firstName("root")
                .lastName("root")
                .age(20)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.ADMIN)
                .build();

        personToSensors.put(person,null);
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public void registration(Person person){
        personToSensors.put(person, null);
    }

    public Optional<Person> findByUsername(String username){
        return personToSensors.keySet().stream()
                .filter(person -> person.getUsername().equals(username))
                .findFirst();
    }
}

