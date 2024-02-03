package monitoring.service.dev.repositories.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Sensor;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class PeopleRepository implements IPeopleRepository {

    private final String registrationQuery = "INSERT INTO person (username, password, role) VALUES (?, ?, ?)";
    private final String findByUserNameQuery = "SELECT * FROM person WHERE username=?";

    @Override
    public void registration(Person person) {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME,
            AppConstants.JDBC_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement(
            registrationQuery)) {

            connection.setAutoCommit(false);

            preparedStatement.setString(1, person.getUsername());
            preparedStatement.setString(2, person.getPassword());
            preparedStatement.setString(3, Role.USER.toString());

            preparedStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            throw new ProblemWithSQLException("Problem with registration. Please try again later.");
        }
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        Person person = null;
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(findByUserNameQuery)) {

            connection.setAutoCommit(false);

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    person = Person.builder()
                        .id(resultSet.getInt("person_id"))
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .role(resultSet.getString("role").equals("ADMIN") ? Role.ADMIN : Role.USER)
                        .build();
                }
            }

            connection.commit();
            return Optional.ofNullable(person);
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with authentication. Please try again later.");
        }
    }

    @Override
    public List<Sensor> getCurrentReadings(Person credentials) {
        return null;
    }

    @Override
    public List<Sensor> getMonthlyReadings(Person credentials, String month, String year) {
        return null;
    }

    @Override
    public void submitReading(Person credentials) {

    }

    @Override
    public List<Person> getAllUsers() {
        return null;
    }

    @Override
    public void setAuthorities(Person person) {

    }

    @Override
    public void deleteAuthorities(Person person) {

    }
}
