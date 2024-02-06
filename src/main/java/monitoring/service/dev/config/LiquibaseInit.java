package monitoring.service.dev.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;

public class LiquibaseInit {

    private static final String CREATE_SCHEMA_LIQUIBASE = "CREATE SCHEMA IF NOT EXISTS liquibase";

    public void init() {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD);
            Statement statement = connection.createStatement()) {

            statement.execute(CREATE_SCHEMA_LIQUIBASE);

            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(jdbcConnection);
            CommandScope updateCommand = new CommandScope("update")
                .addArgumentValue("defaultSchemaName", AppConstants.DEFAULT_LIQUIBASE_SCHEMA)
                .addArgumentValue("changeLogFile", "db.changelog/changelog.yaml")
                .addArgumentValue("url", AppConstants.JDBC_URL)
                .addArgumentValue("username", AppConstants.JDBC_USERNAME)
                .addArgumentValue("password", AppConstants.JDBC_PASSWORD);

            updateCommand.execute();

        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException("Error running Liquibase: ", e);
        }
    }
}