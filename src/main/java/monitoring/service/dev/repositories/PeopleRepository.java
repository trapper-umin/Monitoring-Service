package monitoring.service.dev.repositories;

import monitoring.service.dev.common.Role;
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

        Person person = Person.builder()
                .username("root")
                .password("root")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.ADMIN)
                .sensors(new ArrayList<>())
                .build();

        db.add(person);
    }

    public static PeopleRepository getInstance() {
        if (instance == null) {
            instance = new PeopleRepository();
        }
        return instance;
    }

    /**
     * Очищает базу данных пользователей.
     * Этот метод удаляет все записи из списка {@code db}, эффективно очищая базу данных.
     * Внимание: использовать с осторожностью, предназначен в основном для тестирования.
     */
    public static void clearDb() {
        db.clear();
    }
    /**
     * Возвращает список всех пользователей в базе данных.
     * Этот метод предоставляет доступ к внутреннему списку {@code db}, содержащему всех зарегистрированных пользователей.
     *
     * @return Список {@link Person}, представляющий всех пользователей в базе данных.
     */
    public static List<Person> getDb() {
        return db;
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Добавляет объект {@link Person} в список {@code db}, эффективно регистрируя пользователя в базе данных.
     *
     * @param person Объект {@link Person}, представляющий регистрируемого пользователя.
     */
    @Override
    public void registration(Person person){
        db.add(person);
    }

    /**
     * Ищет пользователя по имени пользователя (username).
     * Этот метод выполняет поиск в списке всех зарегистрированных пользователей {@code db}
     * и возвращает первого пользователя, чьё имя пользователя совпадает с указанным.
     * Поиск осуществляется с учетом регистра.
     *
     * @param username Имя пользователя, по которому осуществляется поиск.
     * @return {@link Optional} объект {@link Person}, если пользователь с таким именем найден,
     *         или пустой {@link Optional}, если пользователь не найден.
     */
    @Override
    public Optional<Person> findByUsername(String username){
        return db.stream()
                .filter(person -> person.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Возвращает список сенсоров пользователя с показаниями за текущий месяц.
     * Этот метод ищет пользователя в базе данных по предоставленным учетным данным (имя пользователя и пароль),
     * и возвращает все сенсоры этого пользователя, у которых есть показания, относящиеся к текущему месяцу.
     *
     * @param credentials Объект {@link Person}, содержащий учетные данные пользователя.
     * @return Список {@link Sensor}, каждый из которых имеет одно показание за текущий месяц.
     *         Если пользователь не найден или у пользователя нет сенсоров с показаниями за текущий месяц,
     *         возвращается пустой список.
     */
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

    /**
     * Возвращает список сенсоров с показаниями за указанный месяц и год для заданного пользователя.
     * Данный метод производит поиск сенсоров, принадлежащих пользователю, идентифицированному по учетным данным,
     * и фильтрует показания этих сенсоров, оставляя только те, что соответствуют указанному месяцу и году.
     *
     * @param credentials Учетные данные пользователя, для которого производится поиск сенсоров.
     * @param month Месяц (в формате полного названия на английском, например, "January"), за который требуется получить показания.
     * @param year Год, за который требуется получить показания.
     * @return Список {@link Sensor}, содержащий сенсоры и их показания, соответствующие указанному периоду.
     *         Если пользователь не найден или у пользователя нет сенсоров с показаниями за указанный период,
     *         возвращается пустой список.
     */
    @Override
    public List<Sensor> getMonthlyReadings(Person credentials, String month, String year) {
        List<Sensor> resultSensors = new ArrayList<>();

        Optional<Person> foundPerson = findByUsername(credentials.getUsername());

        if (foundPerson.isEmpty()) {
            return resultSensors;
        }

        YearMonth targetYearMonth = YearMonth.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase()).getValue());

        for (Sensor sensor : foundPerson.get().getSensors()) {
            Sensor tempSensor = new Sensor();
            tempSensor.setType(sensor.getType());
            List<MeterReading> tempReadings = new ArrayList<>();

            for (MeterReading reading : sensor.getReadings()) {
                LocalDate readingDate = reading.getDate().toLocalDate();
                YearMonth readingYearMonth = YearMonth.from(readingDate);

                if (readingYearMonth.equals(targetYearMonth)) {
                    tempReadings.add(reading);
                }
            }

            if (!tempReadings.isEmpty()) {
                tempSensor.setReadings(tempReadings);
                resultSensors.add(tempSensor);
            }
        }

        return resultSensors;
    }

    /**
     * Сохраняет показания сенсора для указанного пользователя в базе данных.
     * Если у пользователя нет сенсора с заданным типом, метод добавляет новый сенсор вместе с показаниями.
     * Если сенсор существует, но за указанный месяц показания отсутствуют, метод добавляет новые показания к существующему сенсору.
     * В случае, если показания за данный месяц уже существуют для сенсора, генерируется исключение {@link MeterReadingExistsException}.
     * Если пользователь не найден в базе данных, генерируется исключение {@link NotFoundException}.
     *
     * @param person Объект {@link Person}, содержащий учетные данные пользователя и список сенсоров с показаниями.
     * @throws MeterReadingExistsException если показания за данный месяц уже зарегистрированы для сенсора.
     * @throws NotFoundException если пользователь не найден в базе данных.
     */
    @Override
    public void submitReading(Person person) throws MeterReadingExistsException, NotFoundException {
        Optional<Person> personInDb = findByUsername(person.getUsername());

        if (personInDb.isEmpty()) {
            throw new NotFoundException("User with username '" + person.getUsername() + "' was not found");
        }

        Person foundPerson = personInDb.get();

        if (foundPerson.getSensors() == null) {
            foundPerson.setSensors(new ArrayList<>());
        }

        for (Sensor newSensor : person.getSensors()) {
            Sensor existingSensor = null;
            for (Sensor s : foundPerson.getSensors()) {
                if (s.getType().equals(newSensor.getType())) {
                    existingSensor = s;
                    break;
                }
            }

            if (existingSensor == null) {
                foundPerson.getSensors().add(newSensor);
            } else {
                for (MeterReading newReading : newSensor.getReadings()) {
                    YearMonth newReadingMonth = YearMonth.from(newReading.getDate());
                    boolean hasSameMonthReading = existingSensor.getReadings().stream()
                            .anyMatch(existingReading -> YearMonth.from(existingReading.getDate()).equals(newReadingMonth));

                    if (hasSameMonthReading) {
                        throw new MeterReadingExistsException("Meter reading for " + newReadingMonth + " already exists for sensor type " + newSensor.getType());
                    }
                }
                existingSensor.addReadings(new ArrayList<>(newSensor.getReadings()));
            }
        }
    }

    /**
     * Возвращает список всех пользователей из базы данных.
     * Этот метод предоставляет доступ ко всем зарегистрированным пользователям в системе,
     * позволяя получить обзор или произвести операции над всеми учетными записями.
     *
     * @return Список {@link Person}, представляющий всех пользователей в базе данных.
     *         Если в базе данных нет пользователей, возвращается пустой список.
     */
    @Override
    public List<Person> getAllUsers() {
        return db;
    }

    /**
     * Устанавливает роль пользователя в системе на "ADMIN".
     * Этот метод изменяет роль заданного пользователя на "ADMIN", позволяя ему получить административные права.
     * Если пользователь уже обладает ролью "ADMIN", метод генерирует исключение {@link CanNotDoException},
     * указывая, что изменение роли не может быть выполнено.
     *
     * @param person Объект {@link Person}, чья роль должна быть изменена. Должен быть не {@code null}.
     * @throws CanNotDoException если у пользователя уже есть роль "ADMIN".
     *                           Исключение содержит сообщение с указанием имени пользователя и его текущей роли.
     */

    @Override
    public void setAuthorities(Person person) {
        if(person.getRole().equals(Role.ADMIN)){
            throw new CanNotDoException("so " + person.getUsername() + " has a role " + person.getRole());
        }

        person.setRole(Role.ADMIN);
    }

    /**
     * Снимает права "ADMIN" с пользователя, устанавливая его роль на "USER".
     * Этот метод предназначен для изменения роли заданного пользователя на "USER", тем самым лишая его прав администратора.
     * Если у пользователя уже установлена роль "USER", метод генерирует исключение {@link CanNotDoException},
     * сигнализируя о том, что дополнительное изменение роли не требуется и не может быть выполнено.
     *
     * @param person Объект {@link Person}, чья роль должна быть изменена на "USER". Должен быть не {@code null}.
     * @throws CanNotDoException если у пользователя уже установлена роль "USER".
     *                           Исключение содержит сообщение, указывающее имя пользователя и его текущую роль.
     */
    @Override
    public void deleteAuthorities(Person person) {
        if(person.getRole().equals(Role.USER)){
            throw new CanNotDoException("so " + person.getUsername() + " has a role " + person.getRole());
        }

        person.setRole(Role.USER);
    }

}

