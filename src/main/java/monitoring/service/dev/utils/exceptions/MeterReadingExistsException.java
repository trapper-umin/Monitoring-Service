package monitoring.service.dev.utils.exceptions;

public class MeterReadingExistsException extends RuntimeException{

    public MeterReadingExistsException(String msg){
        super(msg);
    }
}
