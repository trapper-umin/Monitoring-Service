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

    private static final PeopleRepository peopleRepository = new PeopleRepository(); //TODO
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
        SELECT save_sensor_reading(?, ?, ?, ?);
        """;

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