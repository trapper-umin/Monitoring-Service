package monitoring.service.dev.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CredentialsDTOReqst implements CommonDTO {

    @NotBlank(message = "should be not blank")
    @Size(min = 4, max = 16, message = "size should be between 4 and 16")
    @Pattern(regexp = "[A-Za-z0-9]+",message = "should only contain Latin letters and digits")
    private String username;

    @NotBlank(message = "should be not blank")
    @Size(min = 4, max = 16, message = "size should be between 4 and 16")
    private String password;
}