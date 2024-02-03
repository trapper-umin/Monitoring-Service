package monitoring.service.dev.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class LiquibaseInit {

    public void init() {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD)) {

            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Liquibase liquibase = new Liquibase("db.changelog/changelog.yaml",
                new ClassLoaderResourceAccessor(), jdbcConnection);

            liquibase.update("");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}