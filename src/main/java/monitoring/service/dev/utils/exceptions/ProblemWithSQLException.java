package monitoring.service.dev.utils.exceptions;

public class ProblemWithSQLException extends RuntimeException {

    public ProblemWithSQLException(String msg) {
        super(msg);
    }
}
