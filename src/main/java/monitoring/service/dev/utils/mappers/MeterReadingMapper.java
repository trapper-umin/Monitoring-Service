package monitoring.service.dev.utils.mappers;

import monitoring.service.dev.dtos.MeterReadingDTO;
import monitoring.service.dev.models.Reading;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeterReadingMapper {

    MeterReadingMapper INSTANCE = Mappers.getMapper(MeterReadingMapper.class);

    MeterReadingDTO convertToMeterReadingDTO(Reading reading);
    Reading convertToMeterReading(MeterReadingDTO meterReadingDTO);
}
