package monitoring.service.dev.repositories.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.utils.exceptions.CanNotDoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AdminRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>(
        "postgres:latest").withDatabaseName("db").withUsername("user").withPassword("pass");

    private static AdminRepository adminRepository;
    private static String JDBC_URL;
    private static String USERNAME;
    private static String PASSWORD;

    @BeforeEach
    void setUp() throws Exception {
        JDBC_URL = postgresqlContainer.getJdbcUrl();
        USERNAME = postgresqlContainer.getUsername();
        PASSWORD = postgresqlContainer.getPassword();

        adminRepository = new AdminRepository(JDBC_URL, USERNAME, PASSWORD);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS person;");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS person (
                        person_id SERIAL PRIMARY KEY,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(255) NOT NULL
                    )
                """);

            statement.execute("""
                    INSERT INTO person (username, password, role) VALUES
                    ('user1', 'pass1', 'USER'),
                    ('admin1', 'pass2', 'ADMIN');
                """);
        }
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        List<Person> users = adminRepository.getAllUsers();

        assertTrue(!users.isEmpty());

        assertEquals(2, users.size());

        assertTrue(users.stream()
            .anyMatch(user -> user.getUsername().equals("user1") && user.getRole() == Role.USER));
        assertTrue(users.stream()
            .anyMatch(user -> user.getUsername().equals("admin1") && user.getRole() == Role.ADMIN));
    }

    @Test
    void testSetAuthoritiesChangesRoleToAdmin() throws Exception {
        Person person = Person.builder().id(1).username("user1").password("pass1").role(Role.USER).build();
        adminRepository.setAuthorities(person);
        try (Connection conn = DriverManager.getConnection(postgresqlContainer.getJdbcUrl(),
            postgresqlContainer.getUsername(),
            postgresqlContainer.getPassword());
            PreparedStatement stmt = conn.prepareStatement("SELECT role FROM person WHERE person_id = ?")) {

            stmt.setInt(1, person.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Не удалось получить пользователя из базы данных");
                String updatedRole = rs.getString("role");
                assertEquals("ADMIN", updatedRole, "Роль пользователя не была изменена на ADMIN");
            }
        } catch (SQLException e) {
            fail("Ошибка при выполнении запроса к базе данных", e);
        }
    }

    @Test
    void testSetAuthoritiesThrowsExceptionForAdminRole() {
        Person person = Person.builder().id(1).username("admin1").password("pass2").role(Role.ADMIN).build();
        assertThrows(CanNotDoException.class, () -> adminRepository.setAuthorities(person));
    }
}