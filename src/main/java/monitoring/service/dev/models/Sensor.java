package monitoring.service.dev.models;

import lombok.*;
import monitoring.service.dev.common.SensorType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {

    private SensorType type;

    private List<MeterReading> readings = new ArrayList<>();;


    public void addReadings(List<MeterReading> newReadings) {
        List<MeterReading> modifiableList = new ArrayList<>(newReadings);
        this.readings.addAll(modifiableList);
    }
}
