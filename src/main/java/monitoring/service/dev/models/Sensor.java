package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.common.SensorType;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    private SensorType type;

    List<MeterReading> readings;
}
