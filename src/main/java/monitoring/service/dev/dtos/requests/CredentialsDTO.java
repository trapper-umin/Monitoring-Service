package monitoring.service.dev.dtos.requests;

import lombok.*;
import monitoring.service.dev.common.Role;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.common.CommonDTO;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsDTO implements CommonDTO {

    private String username;

    private String password;

    List<SensorDTO> sensors;

    private Role role;
}
