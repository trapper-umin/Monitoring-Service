package monitoring.service.dev.utils.mappers;

import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SensorListMapper {

    SensorListMapper INSTANCE = Mappers.getMapper(SensorListMapper.class);

    List<SensorDTO> convertToSensorDTOList(List<Sensor> sensors);
}