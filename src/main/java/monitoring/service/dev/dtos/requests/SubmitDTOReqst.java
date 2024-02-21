package monitoring.service.dev.dtos.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class SubmitDTOReqst implements CommonDTO {

    @NotNull(message = "should be not null")
    @Pattern(regexp = "HOT|COLD|hot|cold", message = "should be HOT/hot or COLD/cold")
    private String type;

    @NotNull(message = "should be not null")
    @DecimalMin(value = "0", inclusive = false, message = "should be greater than zero")
    private Double reading;

    @NotBlank(message = "should be not blank")
    @Pattern(regexp = "[A-Z][a-z]+", message = "the first letter should be uppercase, and the following letters should be lowercase")
    private String month;

    @NotBlank(message = "should be not blank")
    @Pattern(regexp = "[0-9]{4}", message = "should be only numbers")
    private String year;
}
