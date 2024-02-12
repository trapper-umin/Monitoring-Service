package monitoring.service.dev.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import monitoring.service.dev.dtos.common.CommonDTO;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadingDTO implements CommonDTO {

    private double indication;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
}