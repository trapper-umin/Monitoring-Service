package monitoring.service.dev.utils.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg){
        super(msg);
    }
}
