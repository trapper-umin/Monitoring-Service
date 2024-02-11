package monitoring.service.dev.repositories;

import java.util.Optional;
import monitoring.service.dev.models.Person;

public interface IPeopleRepository {

    Person registration(Person person);

    Optional<Person> findByUsername(String username);
}