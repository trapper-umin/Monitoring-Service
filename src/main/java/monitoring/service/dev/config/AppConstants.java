package monitoring.service.dev.config;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConstants {

    @Value("${config.jdbc.url}")
    private String jdbcUrl;

    @Value("${config.jdbc.username}")
    private String jdbcUsername;

    @Value("${config.jdbc.password}")
    private String jdbcPassword;

    @Value("${config.liquibase.default-schema}")
    private String defaultLiquibaseSchema;

    @Value("${config.jwt.secret-key}")
    private String secretJwtKey;

    @Value("${config.jwt.expiration-time}")
    private int expirationJwtTime;

    public static String JDBC_URL;
    public static String JDBC_USERNAME;
    public static String JDBC_PASSWORD;
    public static String DEFAULT_LIQUIBASE_SCHEMA;
    public static String SECRET_JWT_KEY;
    public static int EXPIRATION_JWT_TIME;

    @PostConstruct
    public void init() {
        JDBC_URL = jdbcUrl;
        JDBC_USERNAME = jdbcUsername;
        JDBC_PASSWORD = jdbcPassword;
        DEFAULT_LIQUIBASE_SCHEMA = defaultLiquibaseSchema;
        SECRET_JWT_KEY = secretJwtKey;
        EXPIRATION_JWT_TIME = expirationJwtTime;
    }
    public static final String COMMAND_REGISTER = "/register";
    public static final String COMMAND_LOGIN = "/authenticate";
    public static final String COMMAND_EXIT = "/exit";
    public static final String COMMAND_SUBMIT = "/submit";
    public static final String COMMAND_CURRENT = "/current";
    public static final String COMMAND_GET_MONTHLY = "/monthly";
    public static final String COMMAND_HISTORY = "/history";
    public static final String COMMAND_RIGHTS = "/rights";
    public static final String COMMAND_AUDIT = "/audit";
    public static final String COMMAND_LOGOUT = "/logout";
    public static final String ARG_USERNAME = "-u";
    public static final String ARG_PASSWORD = "-p";
    public static final String ARG_ACTION = "-a";
    public static final String ARG_SCANNER = "-s";
    public static final String ARG_INDICATION = "-i";
    public static final String ARG_MONTH = "-m";
    public static final String ARG_YEAR = "-y";
    public static final String UPGRADE = "upgrade";
    public static final String DOWNGRADE = "downgrade";
    public static final int MIN_YEAR_BORDER = 1900;
    public static final int MAX_YEAR_BORDER = 2500;
    public static final int AMOUNT_OF_PARAMS_IN_SUBMIT = 4;
}