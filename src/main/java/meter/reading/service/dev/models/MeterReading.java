package meter.reading.service.dev.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {

    private int id;

    private int indication;
}
