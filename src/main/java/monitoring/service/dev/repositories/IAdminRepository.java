package monitoring.service.dev.repositories;

import java.util.List;
import monitoring.service.dev.models.Person;

public interface IAdminRepository {

    List<Person> getAllUsers();

    void setAuthorities(Person person);

    void deleteAuthorities(Person person);
}
