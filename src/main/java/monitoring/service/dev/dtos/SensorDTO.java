package monitoring.service.dev.dtos;

import lombok.*;
import monitoring.service.dev.common.SensorType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO {

    private SensorType type;
}
