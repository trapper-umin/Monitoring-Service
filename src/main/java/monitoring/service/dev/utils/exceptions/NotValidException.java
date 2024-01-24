package monitoring.service.dev.utils.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotValidException extends RuntimeException{

    public NotValidException(String msg){
        super(msg);
    }
}
