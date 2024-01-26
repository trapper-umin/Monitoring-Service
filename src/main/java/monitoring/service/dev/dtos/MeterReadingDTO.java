package monitoring.service.dev.dtos;

import lombok.*;
import monitoring.service.dev.dtos.common.CommonDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeterReadingDTO implements CommonDTO {

    private double indication;

    private LocalDateTime date;
}
