package monitoring.service.dev.repositories.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IAdminRepository;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class AdminRepository implements IAdminRepository {

    private static final String GET_ALL_USERS_QUERY = """
        SELECT * FROM person;
        """;

    private static final String SET_ROLE_QUERY = """
        UPDATE person SET role=? WHERE person_id=?;
        """;


    @Override
    public List<Person> getAllUsers() {
        List<Person> people = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            GET_ALL_USERS_QUERY); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                people.add(Person.builder().id(resultSet.getInt("person_id"))
                    .username(resultSet.getString("username"))
                    .password(resultSet.getString("password"))
                    .role(resultSet.getString("role").equals("ADMIN") ? Role.ADMIN : Role.USER)
                    .build());
            }

            return people;
        } catch (SQLException e) {
            throw new ProblemWithSQLException(e.getMessage());
        }
    }

    @Override
    public void setAuthorities(Person person) {
        if (person.getRole().equals(Role.ADMIN)) {
            throw new CanNotDoException(
                "so " + person.getUsername() + " has a role " + person.getRole());
        }

        setRole(person.getId(), Role.ADMIN);
    }

    @Override
    public void deleteAuthorities(Person person) {
        if (person.getRole().equals(Role.USER)) {
            throw new CanNotDoException(
                "so " + person.getUsername() + " has a role " + person.getRole());
        }

        setRole(person.getId(), Role.USER);
    }

    private void setRole(int id, Role role) throws ProblemWithSQLException {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            SET_ROLE_QUERY)) {

            connection.setAutoCommit(false);

            statement.setString(1, role.toString());
            statement.setInt(2, id);
            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            throw new ProblemWithSQLException(e.getMessage());
        }
    }
}