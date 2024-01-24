package monitoring.service.dev.models;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {

    private int id;

    private int indication;

    private LocalDateTime date;
}
