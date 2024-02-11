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

    private int id;

    private SensorType type;

    private List<Reading> readings = new ArrayList<>();

    public void addReadings(List<Reading> newReadings) {
        List<Reading> modifiableList = new ArrayList<>(newReadings);
        this.readings.addAll(modifiableList);
    }
}