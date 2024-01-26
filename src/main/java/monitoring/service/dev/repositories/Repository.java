package monitoring.service.dev.repositories;

import monitoring.service.dev.common.Role;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.MeterReading;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.utils.exceptions.MeterReadingExistsException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class Repository implements IPeopleRepository{
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

        Sensor sensor = Sensor.builder()
                .id(1)
                .type(SensorType.COLD_WATER_METERS)
                .build();

        Sensor sensor2 = Sensor.builder()
                .id(1)
                .type(SensorType.HOT_WATER_METERS)
                .build();

        MeterReading meterReading = MeterReading.builder()
                .id(1)
                .indication(100)
                .date(LocalDateTime.now())
                .build();

        MeterReading meterReading2 = MeterReading.builder()
                .id(1)
                .indication(200)
                .date(LocalDateTime.now())
                .build();

        personToSensors.put(person,List.of(sensor, sensor2));
        sensorToReadings.put(sensor,List.of(meterReading));
        sensorToReadings.put(sensor2,List.of(meterReading2));
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

    public Map<Sensor, MeterReading> getCurrentReadings(Person credentials) {
        Map<Sensor, MeterReading> currentReadings = new HashMap<>();

        List<Sensor> sensors = personToSensors.get(credentials);
        if (sensors != null) {
            for (Sensor sensor : sensors) {
                List<MeterReading> readings = sensorToReadings.get(sensor);
                if (readings != null) {
                    MeterReading latestReading = readings.stream()
                            .filter(reading -> reading.getDate().getYear() == LocalDateTime.now().getYear() &&
                                    reading.getDate().getMonth() == LocalDateTime.now().getMonth())
                            .max(Comparator.comparing(MeterReading::getDate))
                            .orElse(null);
                    if (latestReading != null) {
                        currentReadings.put(sensor, latestReading);
                    }
                }
            }
        }
        return currentReadings;
    }

    public void submitReading(Person person, Sensor sensor, MeterReading meterReading) throws MeterReadingExistsException {
        // Проверяем, существует ли список сенсоров для данного пользователя
        List<Sensor> sensors = personToSensors.get(person);
        if (sensors == null) {
            sensors = new ArrayList<>();
            personToSensors.put(person, sensors);
        }

        // Добавляем сенсор, если он еще не был добавлен
        if (!sensors.contains(sensor)) {
            sensors.add(sensor);
        }

        // Проверяем, существует ли список показаний для данного сенсора
        List<MeterReading> readings = sensorToReadings.get(sensor);
        if (readings == null) {
            readings = new ArrayList<>();
            sensorToReadings.put(sensor, readings);
        } else {
            // Проверяем, существуют ли уже показания для данного месяца
            YearMonth submittedMonth = YearMonth.from(meterReading.getDate());
            for (MeterReading existingReading : readings) {
                YearMonth existingMonth = YearMonth.from(existingReading.getDate());
                if (submittedMonth.equals(existingMonth)) {
                    throw new MeterReadingExistsException("Meter reading for this month already exists.");
                }
            }
        }

        // Добавляем показания к сенсору
        readings.add(meterReading);
    }
}

