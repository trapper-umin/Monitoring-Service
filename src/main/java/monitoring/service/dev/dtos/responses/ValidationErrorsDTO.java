package monitoring.service.dev.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import monitoring.service.dev.dtos.common.CommonDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorsDTO implements CommonDTO {

    private String field;

    private String message;
}
