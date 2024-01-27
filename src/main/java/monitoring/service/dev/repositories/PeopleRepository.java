package monitoring.service.dev.repositories;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.MeterReadingExistsException;
import monitoring.service.dev.utils.exceptions.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

public class PeopleRepository implements IPeopleRepository{
    private static PeopleRepository instance;
    private static final List<Person> db = new ArrayList<>();

    private PeopleRepository() {

        MeterReading meterReading = MeterReading.builder()
                .indication(100)
                .date(LocalDateTime.now())
                .build();

        MeterReading meterReading2 = MeterReading.builder()
                .indication(200)
                .date(LocalDateTime.now())
                .build();

        Sensor sensor = Sensor.builder()
                .type(SensorType.COLD_WATER_METERS)
                .readings(List.of(meterReading))
                .build();

        Sensor sensor2 = Sensor.builder()
                .type(SensorType.HOT_WATER_METERS)
                .readings(List.of(meterReading2))
                .build();

        Person person = Person.builder()
                .username("root")
                .password("root")
                .firstName("root")
                .lastName("root")
                .age(20)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.ADMIN)
                .sensors(List.of(sensor,sensor2))
                .build();

        db.add(person);
    }

    public static PeopleRepository getInstance() {
        if (instance == null) {
            instance = new PeopleRepository();
        }
        return instance;
    }

    @Override
    public void registration(Person person){
        db.add(person);
    }

    @Override
    public Optional<Person> findByUsername(String username){
        return db.stream()
                .filter(person -> person.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<Sensor> getCurrentReadings(Person credentials) {
        List<Sensor> currentMonthSensors = new ArrayList<>();
        Person foundPerson = db.stream()
                .filter(p -> p.getUsername().equals(credentials.getUsername()) && p.getPassword().equals(credentials.getPassword()))
                .findFirst()
                .orElse(null);

        if (foundPerson != null && foundPerson.getSensors() != null) {
            YearMonth currentMonth = YearMonth.now();
            for (Sensor sensor : foundPerson.getSensors()) {
                if (sensor.getReadings() != null) {
                    boolean hasCurrentMonthReading = sensor.getReadings().stream()
                            .anyMatch(reading -> YearMonth.from(reading.getDate()).equals(currentMonth));

                    if (hasCurrentMonthReading) {
                        currentMonthSensors.add(sensor);
                    }
                }
            }
        }
        return currentMonthSensors;
    }

    @Override
    public List<Sensor> getMonthlyReadings(Person credentials, String month, String year) {
        List<Sensor> resultSensors = new ArrayList<>();

        // Находим пользователя в базе данных
        Optional<Person> foundPerson = findByUsername(credentials.getUsername());

        if (foundPerson.isEmpty()) {
            return resultSensors; // Возвращаем пустой список, если пользователь не найден
        }

        // Форматируем месяц и год в YearMonth для сравнения
        YearMonth targetYearMonth = YearMonth.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase()).getValue());

        // Проходим по всем сенсорам пользователя
        for (Sensor sensor : foundPerson.get().getSensors()) {
            Sensor tempSensor = new Sensor(); // Создаем временный сенсор для добавления показаний
            tempSensor.setType(sensor.getType()); // Устанавливаем тип сенсора
            List<MeterReading> tempReadings = new ArrayList<>();

            // Проходим по всем показаниям сенсора
            for (MeterReading reading : sensor.getReadings()) {
                LocalDate readingDate = reading.getDate().toLocalDate();
                YearMonth readingYearMonth = YearMonth.from(readingDate);
                // Проверяем, соответствует ли дата показания целевому периоду
                if (readingYearMonth.equals(targetYearMonth)) {
                    tempReadings.add(reading); // Добавляем показание, если оно соответствует периоду
                }
            }

            // Если для сенсора были найдены показания за целевой период, добавляем его в результат
            if (!tempReadings.isEmpty()) {
                tempSensor.setReadings(tempReadings);
                resultSensors.add(tempSensor);
            }
        }

        return resultSensors;
    }

    @Override
    public void submitReading(Person person) throws MeterReadingExistsException, NotFoundException {
        Optional<Person> personInDb = findByUsername(person.getUsername());

        if (personInDb.isEmpty()) {
            throw new NotFoundException("User with username '" + person.getUsername() + "' was not found");
        }

        Person foundPerson = personInDb.get();

        // Убедимся, что список сенсоров инициализирован
        if (foundPerson.getSensors() == null) {
            foundPerson.setSensors(new ArrayList<>());
        }

        // Обходим каждый новый сенсор из предоставленных данных
        for (Sensor newSensor : person.getSensors()) {
            // Пытаемся найти существующий сенсор такого же типа
            Sensor existingSensor = null;
            for (Sensor s : foundPerson.getSensors()) {
                if (s.getType().equals(newSensor.getType())) {
                    existingSensor = s;
                    break;
                }
            }

            if (existingSensor == null) {
                // Если сенсора такого типа нет, добавляем его вместе с показаниями
                foundPerson.getSensors().add(newSensor);
            } else {
                // Проверяем, существуют ли показания за тот же месяц для этого типа сенсора
                for (MeterReading newReading : newSensor.getReadings()) {
                    YearMonth newReadingMonth = YearMonth.from(newReading.getDate());
                    boolean hasSameMonthReading = existingSensor.getReadings().stream()
                            .anyMatch(existingReading -> YearMonth.from(existingReading.getDate()).equals(newReadingMonth));

                    if (hasSameMonthReading) {
                        throw new MeterReadingExistsException("Meter reading for " + newReadingMonth + " already exists for sensor type " + newSensor.getType());
                    }
                }
                // Если показаний за этот месяц нет, добавляем новые показания к существующему сенсору
                existingSensor.getReadings().addAll(newSensor.getReadings());
            }
        }
    }

    @Override
    public List<Person> getAllUsers() {
        return db;
    }

    @Override
    public void setAuthorities(Person person) {
        if(person.getRole().equals(Role.ADMIN)){
            throw new CanNotDoException("so " + person.getUsername() + " has a role " + person.getRole());
        }

        person.setRole(Role.ADMIN);
    }

    @Override
    public void deleteAuthorities(Person person) {
        if(person.getRole().equals(Role.USER)){
            throw new CanNotDoException("so " + person.getUsername() + " has a role " + person.getRole());
        }

        person.setRole(Role.USER);
    }

}

