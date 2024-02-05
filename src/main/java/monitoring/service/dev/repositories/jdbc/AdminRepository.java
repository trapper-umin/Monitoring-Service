package monitoring.service.dev.repositories.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.common.Role;
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

    private final String url;
    private final String username;
    private final String password;

    public AdminRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public List<Person> getAllUsers() {
        List<Person> people = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
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

    /**
     * Устанавливает пользователю роль администратора.
     * <p>
     * Этот метод изменяет роль пользователя на администратора ({@link Role#ADMIN}), если текущая роль пользователя
     * не является администратором. Если пользователь уже имеет роль администратора, метод выбрасывает исключение
     * {@link CanNotDoException} с сообщением о том, что изменение роли невозможно.
     * <p>
     * Используется для повышения прав обычных пользователей до уровня администратора в рамках системы.
     * Операция изменения роли делегируется приватному методу {@code setRole}, который выполняет непосредственное
     * изменение роли в базе данных.
     *
     * @param person Объект {@link Person}, представляющий пользователя, чья роль должна быть изменена.
     * @throws CanNotDoException если пользователь уже является администратором.
     */
    @Override
    public void setAuthorities(Person person) {
        if (person.getRole().equals(Role.ADMIN)) {
            throw new CanNotDoException(
                "so " + person.getUsername() + " has a role " + person.getRole());
        }

        setRole(person.getId(), Role.ADMIN);
    }

    /**
     * Снимает с пользователя роль администратора.
     * <p>
     * Этот метод изменяет роль указанного пользователя на обычного пользователя ({@link Role#USER}),
     * если его текущая роль не является ролью обычного пользователя. Если пользователь уже имеет роль обычного пользователя,
     * метод выбрасывает исключение {@link CanNotDoException} с сообщением о том, что изменение роли невозможно.
     * <p>
     * Используется для понижения прав администраторов до уровня обычных пользователей в системе.
     * Операция изменения роли делегируется приватному методу {@code setRole}, который выполняет непосредственное
     * изменение роли в базе данных.
     *
     * @param person Объект {@link Person}, представляющий пользователя, чья роль должна быть изменена.
     * @throws CanNotDoException если пользователь уже является обычным пользователем.
     */
    @Override
    public void deleteAuthorities(Person person) {
        if (person.getRole().equals(Role.USER)) {
            throw new CanNotDoException(
                "so " + person.getUsername() + " has a role " + person.getRole());
        }

        setRole(person.getId(), Role.USER);
    }

    /**
     * Изменяет роль пользователя в базе данных.
     * <p>
     * Этот приватный метод обновляет роль пользователя, идентифицируемого по его идентификатору, на указанную роль.
     * Изменение производится путем выполнения SQL-запроса, определенного в {@code SET_ROLE_QUERY},
     * с передачей параметров: новой роли пользователя и его идентификатора.
     * <p>
     * Операция выполняется в рамках транзакции: изменения подтверждаются только после успешного выполнения
     * обновления. В случае возникновения исключения в процессе выполнения запроса транзакция откатывается,
     * и выбрасывается исключение {@link ProblemWithSQLException}, содержащее сообщение об ошибке.
     *
     * @param id Идентификатор пользователя, чья роль должна быть изменена.
     * @param role Новая роль, которая должна быть установлена для пользователя.
     * @throws ProblemWithSQLException если происходит ошибка при выполнении запроса к базе данных.
     */

    private void setRole(int id, Role role) throws ProblemWithSQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(SET_ROLE_QUERY)) {

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