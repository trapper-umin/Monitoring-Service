package monitoring.service.dev.repositories.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class PeopleRepository implements IPeopleRepository {

    private static final String REGISTRATION_QUERY = "INSERT INTO person (username, password, role) VALUES (?, ?, ?);";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT * FROM person WHERE username=?;";
    private static final String GET_PERSON_ID_QUERY = "SELECT person_id FROM person WHERE username=?;";

    @Override
    public Person registration(Person person) {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statementForReg = connection.prepareStatement(
            REGISTRATION_QUERY)) {

            connection.setAutoCommit(false);

            statementForReg.setString(1, person.getUsername());
            statementForReg.setString(2, person.getPassword());
            statementForReg.setString(3, Role.USER.toString());
            statementForReg.executeUpdate();

            connection.commit();
            return person;
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with registration. Please try again later.\n" + e.getMessage());
        }
    }

    @Override
    public Optional<Person> findByUsername(String username) {
        Person person = null;
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(
            FIND_BY_USERNAME_QUERY)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    person = Person.builder().id(resultSet.getInt("person_id"))
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .role(resultSet.getString("role").equals("ADMIN") ? Role.ADMIN : Role.USER)
                        .build();
                }
            }
            return Optional.ofNullable(person);
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with authentication. Please try again later.");
        }
    }

    public int getIdByUsername(String username) {
        int id = 0;
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            GET_PERSON_ID_QUERY)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    id = resultSet.getInt("person_id");
                }
            }
            return id;
        } catch (SQLException e) {
            throw new ProblemWithSQLException(e.getMessage());
        }
    }
}