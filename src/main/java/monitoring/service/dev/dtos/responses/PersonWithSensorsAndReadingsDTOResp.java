package monitoring.service.dev.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import monitoring.service.dev.dtos.SensorDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonWithSensorsAndReadingsDTOResp {

    private int status;

    private String operation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String user;

    private List<SensorDTO> sensors;
}
