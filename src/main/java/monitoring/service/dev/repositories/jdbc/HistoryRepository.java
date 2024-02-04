package monitoring.service.dev.repositories.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.History;
import monitoring.service.dev.repositories.IHistoryRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class HistoryRepository implements IHistoryRepository {

    private static final String GET_HISTORY = """
        SELECT * FROM history WHERE username=?;
        """;

    private static final String INSERT_HISTORY = """
        INSERT INTO history(username, action, date) VALUES(?,?,?);
        """;

    @Override
    public List<History> get(CredentialsDTO credentials) {
        List<History> histories = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            GET_HISTORY)) {

            statement.setString(1, credentials.getUsername());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    histories.add(History.builder().username(resultSet.getString("username"))
                        .action(resultSet.getString("action"))
                        .time(resultSet.getTimestamp("date").toLocalDateTime()).build());
                }
            }

            return histories;
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with getting history. Please try again later.\n" + e.getMessage());
        }
    }

    @Override
    public void push(History history) {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            INSERT_HISTORY)) {

            connection.setAutoCommit(false);

            statement.setString(1, history.getUsername());
            statement.setString(2, history.getAction());
            statement.setTimestamp(3, Timestamp.valueOf(history.getTime()));
            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with pushing history. Please try again later.\n" + e.getMessage());
        }
    }
}