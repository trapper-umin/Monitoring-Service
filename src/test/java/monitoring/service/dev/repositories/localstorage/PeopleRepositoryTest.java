package monitoring.service.dev.repositories.localstorage;

import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.models.Reading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.localstorage.PeopleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PeopleRepositoryTest {

    private PeopleRepository repository;
    private Person testPerson;

    @BeforeEach
    void setUp() {
        repository = PeopleRepository.getInstance();
        PeopleRepository.clearDb();

        Sensor sensor1 = new Sensor(1,SensorType.HOT_WATER_METERS, List.of(new Reading(25.0, LocalDateTime.now())));
        Sensor sensor2 = new Sensor(2,SensorType.COLD_WATER_METERS, List.of(new Reading(60.0, LocalDateTime.now().minusMonths(1))));

        testPerson = Person.builder()
                .username("root")
                .password("root")
                .sensors(Arrays.asList(sensor1,sensor2))
                .build();
        repository.registration(testPerson);
    }

    @Test
    void testRegistration() {
        Person newPerson = Person.builder()
                .username("username")
                .password("password")
                .sensors(new ArrayList<>())
                .build();

        repository.registration(newPerson);

        assertTrue(PeopleRepository.getDb().contains(newPerson));
    }

    @Test
    void findByUsernameReturnsExistingUser() {

        Optional<Person> found = repository.findByUsername("root");
        assertTrue(found.isPresent(), "Пользователь должен быть найден");
    }

    @Test
    void findByUsernameReturnsEmptyForUnknownUser() {

        Optional<Person> found = repository.findByUsername("unknown");
        assertFalse(found.isPresent(), "Пользователь не должен быть найден");
    }

    @Test
    void getCurrentReadingsShouldReturnSensorsWithCurrentMonthReadings() {
        List<Sensor> sensors = repository.getCurrentReadings(testPerson);
        assertEquals(1, sensors.size(), "Должен вернуть один сенсор с показаниями за текущий месяц");
        assertEquals(SensorType.HOT_WATER_METERS, sensors.get(0).getType(), "Тип сенсора должен соответствовать ожидаемому");
    }

    @Test
    void getCurrentReadingsShouldReturnEmptyListForUserWithoutCurrentMonthReadings() {
        testPerson.getSensors().forEach(sensor -> sensor.getReadings().forEach(reading -> reading.setDate(LocalDateTime.now().minusMonths(1))));

        List<Sensor> sensors = repository.getCurrentReadings(testPerson);
        assertTrue(sensors.isEmpty(), "Должен вернуть пустой список, если нет показаний за текущий месяц");
    }

    @Test
    void getMonthlyReadingsReturnsSensorsForGivenMonthAndYear() {
        String targetMonth = "JANUARY";
        String targetYear = "2024";
        List<Sensor> sensors = repository.getMonthlyReadings(testPerson, targetMonth, targetYear);

        assertFalse(sensors.isEmpty(), "Список сенсоров не должен быть пустым");
    }

    @Test
    void getMonthlyReadingsReturnsEmptyListWhenNoReadings() {
        String targetMonth = "FEBRUARY";
        String targetYear = "2021";
        List<Sensor> sensors = repository.getMonthlyReadings(testPerson, targetMonth, targetYear);

        assertTrue(sensors.isEmpty(), "Список сенсоров должен быть пустым, если показаний за указанный месяц нет");
    }

    @Test
    void getAllUsersReturnsListOfAllUsers() {
        Person user1 = Person.builder().build();
        Person user2 = Person.builder().build();
        repository.registration(user1);
        repository.registration(user2);

        List<Person> users = repository.getAllUsers();
        assertEquals(3, users.size(), "Должен возвращаться список всех пользователей");
        assertTrue(users.contains(user1), "Список должен содержать первого пользователя");
        assertTrue(users.contains(user2), "Список должен содержать второго пользователя");
    }


}
