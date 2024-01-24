package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.common.SensorType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    private int id;

    private SensorType type;


}
