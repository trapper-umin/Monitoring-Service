package monitoring.service.dev.dtos.requests;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import monitoring.service.dev.common.SensorType;
import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.dtos.common.CommonDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTOWithOneReadingReqst implements CommonDTO {

    private SensorType type;

    private ReadingDTO reading;
}
