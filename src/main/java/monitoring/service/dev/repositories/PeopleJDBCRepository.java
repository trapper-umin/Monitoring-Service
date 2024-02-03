package monitoring.service.dev.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import monitoring.service.dev.config.AppConstants;

public class PeopleJDBCRepository {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        try (Connection connection = DriverManager.getConnection(AppConstants.JDBC_URL,
            AppConstants.JDBC_USERNAME, AppConstants.JDBC_PASSWORD)) {

            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            Liquibase liquibase = new Liquibase("db.changelog/changelog.yaml",
                new ClassLoaderResourceAccessor(), jdbcConnection);

            liquibase.update("");
            System.out.println("Database has been initialized");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
