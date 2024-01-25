package monitoring.service.dev.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeterReadingDTO {

    private int indication;

    private LocalDateTime date;
}
