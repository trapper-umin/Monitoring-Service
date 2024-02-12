package monitoring.service.dev.dtos.requests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.dtos.common.CommonDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredentialsDTOWithSensorReqst implements CommonDTO {

    private String username;

    private String password;

    private SensorDTOWithOneReadingReqst sensor;
}
