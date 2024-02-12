package monitoring.service.dev.utils.mappers;

import java.util.List;
import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.models.Reading;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReadingMapper {

    ReadingMapper INSTANCE = Mappers.getMapper(ReadingMapper.class);

    ReadingDTO convertToMeterReadingDTO(Reading reading);

    Reading convertToMeterReading(ReadingDTO readingDTO);
    List<ReadingDTO> convertToReadingDTOList(List<Reading> readings);
}