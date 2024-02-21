package monitoring.service.dev.utils.mappers;

import monitoring.service.dev.dtos.ReadingDTO;
import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.Reading;
import monitoring.service.dev.models.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface SensorAndMeterReadingMapMapper {

    SensorMapper sensorMapper = Mappers.getMapper(SensorMapper.class);
    ReadingMapper READING_MAPPER = Mappers.getMapper(ReadingMapper.class);

    default Map<SensorDTO, ReadingDTO> convertToDtoMap(Map<Sensor, Reading> entity) {
        if (entity == null) {
            return null;
        }

        Map<SensorDTO, ReadingDTO> dto = new HashMap<>();
        for (Map.Entry<Sensor, Reading> entry : entity.entrySet()) {
            dto.put(sensorMapper.convertToSensorDTO(entry.getKey()),
                READING_MAPPER.convertToMeterReadingDTO(entry.getValue()));
        }

        return dto;
    }
}