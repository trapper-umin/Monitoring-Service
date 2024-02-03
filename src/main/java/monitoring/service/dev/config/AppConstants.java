package monitoring.service.dev.config;

public class AppConstants {
    public static final String JDBC_URL = "jdbc:postgresql://localhost:5433/postgres";
    public static final String JDBC_USERNAME = "postgres";
    public static final String JDBC_PASSWORD = "postgres";
    public static final String COMMAND_REGISTER = "/register";
    public static final String COMMAND_LOGIN = "/login";
    public static final String COMMAND_EXIT = "/exit";
    public static final String COMMAND_SUBMIT = "/submit";
    public static final String COMMAND_GET = "/get";
    public static final String COMMAND_GET_MONTHLY = "/getMonthly";
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
}
