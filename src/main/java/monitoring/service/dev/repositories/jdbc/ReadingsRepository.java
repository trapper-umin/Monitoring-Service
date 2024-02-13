package monitoring.service.dev.repositories.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Reading;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.IReadingsRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class ReadingsRepository implements IReadingsRepository {

    private static final PeopleRepository peopleRepository = new PeopleRepository();
    private static final String GET_CURRENT_READINGS_QUERY = """
        SELECT
            s.type AS sensor_type,
            r.indication,
            r.date
        FROM
            person p
        JOIN
            sensor s ON p.person_id = s.person_id
        JOIN
            reading r ON s.sensor_id = r.sensor_id
        WHERE
            p.username = ?
            AND
            date_trunc('month', r.date) = date_trunc('month', current_date)
        ORDER BY
            r.date;
        """;

    private static final String GET_MONTHLY_READINGS_QUERY = """
        SELECT
            s.type AS sensor_type,
            r.indication,
            r.date
        FROM
            person p
        JOIN
            sensor s ON p.person_id = s.person_id
        JOIN
            reading r ON s.sensor_id = r.sensor_id
        WHERE
            p.username = ?
            AND
            EXTRACT(YEAR FROM r.date) = ?
            AND
            EXTRACT(MONTH FROM r.date) = ?
        ORDER BY
            r.date;
        """;

    private static final String USE_FUNC_SAVE_SENSOR_READING_QUERY = """
        SELECT liquibase.save_sensor_reading(?, ?, ?, ?);
        """;

    /**
     * Получает текущие показания датчиков для указанного пользователя.
     * <p>
     * Этот метод выполняет запрос к базе данных для извлечения списка текущих показаний всех датчиков,
     * связанных с пользователем. Возвращает список объектов {@link Sensor}, каждый из которых содержит
     * информацию о конкретном датчике и его текущих показаниях. Данные о датчиках извлекаются на основе
     * имени пользователя, указанного в объекте {@link Person} credentials.
     * <p>
     * Использует предопределённый SQL-запрос {@code GET_CURRENT_READINGS_QUERY} для получения данных.
     * В случае успешного выполнения запроса, результаты парсятся и преобразуются в список объектов {@link Sensor}
     * с помощью вспомогательного метода {@code parsResultSet}.
     * <p>
     * В случае возникновения исключения SQL, метод выбрасывает исключение {@link ProblemWithSQLException},
     * сигнализирующее о возникшей проблеме при попытке получить текущие показания датчиков.
     * Текст исключения включает дополнительное сообщение об ошибке для упрощения диагностики проблемы.
     *
     * @param credentials Объект {@link Person}, содержащий учетные данные пользователя, для которого требуется получить показания.
     * @return Список объектов {@link Sensor}, представляющих текущие показания датчиков пользователя.
     * @throws ProblemWithSQLException если произошла ошибка при выполнении запроса к базе данных.
     */
    @Override
    public List<Sensor> getCurrentReadings(Person credentials) {

        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(
            GET_CURRENT_READINGS_QUERY)) {

            preparedStatement.setString(1, credentials.getUsername());

            return parsResultSet(preparedStatement);
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "There is a problem with getting current readings. Please try again later.\n"
                    + e.getMessage());
        }
    }

    /**
     * Получает показания датчиков за указанный месяц и год для заданного пользователя.
     * <p>
     * Этот метод выполняет запрос к базе данных для извлечения списка показаний всех датчиков, связанных
     * с пользователем, за определённый месяц и год. Возвращает список объектов {@link Sensor}, каждый из которых
     * содержит информацию о датчике и его показаниях за указанный период. Данные фильтруются на основе
     * имени пользователя, указанного в объекте {@link Person} credentials, а также месяца и года.
     * <p>
     * Использует предопределённый SQL-запрос {@code GET_MONTHLY_READINGS_QUERY}, где параметры запроса включают
     * имя пользователя, год и месяц. Год и месяц передаются в запрос как числовые значения, где месяц
     * преобразуется из строки в соответствующее числовое значение с помощью {@link java.time.Month#valueOf(String).getValue()}.
     * <p>
     * В случае успешного выполнения запроса, результаты парсятся и преобразуются в список объектов {@link Sensor}
     * с помощью вспомогательного метода {@code parsResultSet}.
     * <p>
     * В случае возникновения исключения SQL, метод выбрасывает исключение {@link ProblemWithSQLException},
     * сигнализирующее о возникшей проблеме при попытке получить показания датчиков за месяц.
     * Текст исключения содержит дополнительное сообщение об ошибке для облегчения диагностики проблемы.
     *
     * @param credentials Объект {@link Person}, содержащий учетные данные пользователя.
     * @param month Строка, представляющая месяц, за который требуется получить показания.
     * @param year Строка, представляющая год, за который требуется получить показания.
     * @return Список объектов {@link Sensor}, представляющих показания датчиков за указанный месяц и год.
     * @throws ProblemWithSQLException если произошла ошибка при выполнении запроса к базе данных.
     */
    @Override
    public List<Sensor> getMonthlyReadings(Person credentials, String month, String year) {

        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(
            GET_MONTHLY_READINGS_QUERY)) {

            preparedStatement.setString(1, credentials.getUsername());
            preparedStatement.setInt(2, Integer.parseInt(year));
            preparedStatement.setInt(3, Month.valueOf(month.toUpperCase()).getValue());

            return parsResultSet(preparedStatement);
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "There is a problem with getting monthly readings. Please try again later.\n"
                    + e.getMessage());
        }
    }

    /**
     * Парсит {@link ResultSet} из выполненного {@link PreparedStatement} для извлечения данных о датчиках.
     * <p>
     * Этот метод проходит через каждую строку в {@link ResultSet}, создавая на их основе объекты {@link Sensor}.
     * Каждый объект {@link Sensor} содержит информацию о типе датчика и его показаниях. Тип датчика определяется
     * по значению поля "sensor_type" в результате запроса: если значение равно "HOT", датчик считается датчиком
     * горячей воды ({@link SensorType#HOT_WATER_METERS}), в противном случае - датчиком холодной воды
     * ({@link SensorType#COLD_WATER_METERS}). Показания датчика включают в себя значение показаний и дату,
     * когда они были сделаны.
     * <p>
     * Возвращает список объектов {@link Sensor}, собранных из результатов запроса. В случае отсутствия данных
     * в {@link ResultSet}, возвращается пустой список.
     *
     * @param preparedStatement Подготовленный SQL-запрос.
     * @return Список объектов {@link Sensor}, представляющих датчики и их показания.
     * @throws SQLException Если происходит ошибка SQL при обработке {@link ResultSet}.
     */
    private List<Sensor> parsResultSet(PreparedStatement preparedStatement) throws SQLException {
        List<Sensor> sensors = new ArrayList<>();
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                sensors.add(Sensor.builder().type(
                    resultSet.getString("sensor_type").equals("HOT") ? SensorType.HOT_WATER_METERS
                        : SensorType.COLD_WATER_METERS).readings(List.of(
                    Reading.builder().indication(resultSet.getDouble("indication"))
                        .date(resultSet.getTimestamp("date").toLocalDateTime()).build())).build());
            }
        }
        return sensors;
    }

    /**
     * Отправляет показания датчика для пользователя.
     * <p>
     * Этот метод использует хранимую процедуру в базе данных для сохранения показаний датчика, связанных с определённым пользователем.
     * Показания, включая тип датчика и значение показаний, извлекаются из объекта {@link Person}.
     * <p>
     * Для идентификации пользователя используется его имя пользователя, а тип датчика, значение показаний и дата показаний
     * передаются как параметры в хранимую процедуру. Процедура определена SQL-запросом {@code USE_FUNC_SAVE_SENSOR_READING_QUERY}.
     * <p>
     * При успешном выполнении запроса изменения фиксируются в базе данных. В случае возникновения исключения
     * SQL транзакция откатывается, и выбрасывается исключение {@link ProblemWithSQLException}, сигнализирующее
     * о проблеме при отправке показаний.
     *
     * @param credentials Объект {@link Person}, содержащий учетные данные пользователя и информацию о его показание.
     * @throws ProblemWithSQLException если происходит ошибка при выполнении запроса к базе данных.
     */
    @Override
    public void submitReading(Person credentials) {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(
            USE_FUNC_SAVE_SENSOR_READING_QUERY)) {

            connection.setAutoCommit(false);

            preparedStatement.setInt(1,
                peopleRepository.getIdByUsername(credentials.getUsername()));
            preparedStatement.setString(2, credentials.getSensors().get(0).getType().toString());
            preparedStatement.setDouble(3,
                credentials.getSensors().get(0).getReadings().get(0).getIndication());
            preparedStatement.setTimestamp(4,
                Timestamp.valueOf(credentials.getSensors().get(0).getReadings().get(0).getDate()));

            preparedStatement.executeQuery();

            connection.commit();
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "There is a problem with submitting. Please try again later.\n" + e.getMessage());
        }
    }
}