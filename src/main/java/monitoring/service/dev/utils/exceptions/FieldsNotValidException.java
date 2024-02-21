package monitoring.service.dev.utils.exceptions;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import monitoring.service.dev.dtos.responses.ValidationErrorsDTO;

@Getter
@NoArgsConstructor
public class FieldsNotValidException extends RuntimeException {

    private List<ValidationErrorsDTO> errors;

    public FieldsNotValidException(List<ValidationErrorsDTO> errors) {
        this.errors = errors;
    }
}
