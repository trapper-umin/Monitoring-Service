package monitoring.service.dev.repositories.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.repositories.IAuditRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepository implements IAuditRepository {

    private static final String GET_AUDIT = """
        SELECT * FROM audit;
        """;

    private static final String INSERT_AUDIT = """
        INSERT INTO audit(log) VALUES(?);
        """;

    @Override
    public List<Audit> get() {
        List<Audit> audits = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            GET_AUDIT)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    audits.add(Audit.builder().log(resultSet.getString("log")).build());
                }
            }

            return audits;
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with getting audit. Please try again later.\n" + e.getMessage());
        }
    }

    @Override
    public void push(Audit audit) {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            PreparedStatement statement = connection.prepareStatement(
            INSERT_AUDIT)) {

            connection.setAutoCommit(false);

            statement.setString(1, audit.getLog());
            statement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            throw new ProblemWithSQLException(
                "Problem with pushing audit. Please try again later.\n" + e.getMessage());
        }
    }
}