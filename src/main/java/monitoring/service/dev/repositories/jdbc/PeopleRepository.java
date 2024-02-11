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

    /**
     * Регистрирует нового пользователя в системе.
     * <p>
     * Этот метод создаёт новую запись пользователя в базе данных, используя данные, предоставленные в объекте {@link Person}.
     * Всем пользователям по умолчанию присваивается роль {@link Role#USER}. Метод выполняет SQL-запрос
     * для добавления пользователя с указанными именем пользователя и паролем. В случае успешной регистрации
     * метод возвращает объект {@link Person}, содержащий информацию о пользователе. Транзакция подтверждается
     * при успешном выполнении.
     * <p>
     * Если в процессе возникает исключение SQL, транзакция откатывается, и выбрасывается исключение
     * {@link ProblemWithSQLException}, указывающее на проблему с процессом регистрации.
     *
     * @param person Объект {@link Person}, содержащий информацию для регистрации пользователя.
     * @return Объект {@link Person} после успешной регистрации.
     * @throws ProblemWithSQLException если при выполнении SQL-запроса на регистрацию возникли проблемы.
     */
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

    /**
     * Ищет пользователя по имени пользователя.
     * <p>
     * Этот метод выполняет поиск в базе данных для нахождения пользователя с указанным именем пользователя.
     * Если пользователь найден, возвращается {@link Optional} с объектом {@link Person},
     * содержащим всю доступную информацию о пользователе, включая его идентификатор, имя пользователя,
     * пароль и роль. Если пользователь не найден, возвращается пустой {@link Optional}.
     * <p>
     * Метод использует предопределённый SQL-запрос {@code FIND_BY_USERNAME_QUERY} для извлечения данных пользователя.
     * В случае возникновения SQL-исключения выбрасывается исключение {@link ProblemWithSQLException},
     * сигнализирующее о проблеме с аутентификацией. Это исключение должно быть обработано на более высоком уровне.
     *
     * @param username Имя пользователя, по которому осуществляется поиск.
     * @return {@link Optional<Person>} с объектом пользователя, если таковой найден, или пустой {@link Optional}, если пользователь не найден.
     * @throws ProblemWithSQLException если в процессе выполнения запроса произошла ошибка.
     */
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

    /**
     * Возвращает идентификатор пользователя по его имени пользователя.
     * <p>
     * Этот метод выполняет запрос к базе данных для получения идентификатора пользователя на основе
     * предоставленного имени пользователя. Если пользователь с таким именем существует, метод возвращает его идентификатор.
     * В противном случае возвращает 0. Метод предполагает, что имена пользователей уникальны в системе.
     * <p>
     * Использует предопределённый SQL-запрос {@code GET_PERSON_ID_QUERY} для выполнения поиска.
     * При возникновении SQL-исключения выбрасывается исключение {@link ProblemWithSQLException},
     * указывающее на проблемы при выполнении запроса.
     *
     * @param username Имя пользователя, для которого требуется найти идентификатор.
     * @return Идентификатор пользователя или 0, если пользователь с таким именем не найден.
     * @throws ProblemWithSQLException если произошла ошибка при выполнении запроса к базе данных.
     */
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