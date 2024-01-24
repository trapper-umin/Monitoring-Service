package monitoring.service.dev.repositories;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PeopleRepository {
    private static PeopleRepository instance;
    private Map<String, Person> db;

    private PeopleRepository() {
        db = new HashMap<>();
    }

    public static PeopleRepository getInstance() {
        if (instance == null) {
            instance = new PeopleRepository();
        }
        return instance;
    }

    public void registration(Person person){
        db.put(null, null);
    }

    public Optional<Person> findByUsername(String username){
        return Optional.ofNullable(db.get(username));
    }
}

