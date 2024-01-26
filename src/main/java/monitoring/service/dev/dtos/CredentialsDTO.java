package monitoring.service.dev.dtos;

import lombok.*;
import monitoring.service.dev.dtos.common.CommonDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsDTO implements CommonDTO {

    private String username;

    private String password;
}
