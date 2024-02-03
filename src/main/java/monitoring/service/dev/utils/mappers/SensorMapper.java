package monitoring.service.dev.utils.mappers;

import monitoring.service.dev.dtos.SensorDTO;
import monitoring.service.dev.models.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SensorMapper {

    SensorMapper INSTANCE = Mappers.getMapper(SensorMapper.class);

    SensorDTO convertToSensorDTO(Sensor sensor);

    Sensor convertToSensor(SensorDTO sensorDTO);

}