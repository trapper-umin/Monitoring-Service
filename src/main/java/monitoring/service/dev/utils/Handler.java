package monitoring.service.dev.utils;

import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.dtos.responses.ValidationErrorsDTO;
import monitoring.service.dev.utils.exceptions.FieldsNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class Handler {

    public static void handleErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ValidationErrorsDTO> dtos = new ArrayList<>();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                dtos.add(ValidationErrorsDTO.builder().field(error.getField())
                    .message(error.getDefaultMessage()).build());
            }
            throw new FieldsNotValidException(dtos);
        }
    }
}
