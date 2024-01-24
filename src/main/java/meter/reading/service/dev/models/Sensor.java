package meter.reading.service.dev.models;

import lombok.*;
import meter.reading.service.dev.common.SensorType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    private int id;

    private SensorType type;
}
