package monitoring.service.dev.dtos;

import lombok.*;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.dtos.common.CommonDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO implements CommonDTO {

    private SensorType type;
}
