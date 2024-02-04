package monitoring.service.dev.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
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

            Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(jdbcConnection);

            database.setDefaultSchemaName("liquibase");

            liquibase.update(new Contexts(), new LabelExpression());

        } catch (SQLException e) {
            System.out.println("SQL: " + e.getMessage());
        } catch (LiquibaseException e) {
            throw new RuntimeException("Liquibase: ", e);
        }
    }
}