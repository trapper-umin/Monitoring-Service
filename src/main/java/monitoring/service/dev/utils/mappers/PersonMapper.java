package monitoring.service.dev.utils.mappers;

import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.requests.SensorDTOWithOneReadingReqst;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.models.Reading;
import monitoring.service.dev.models.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person convertToPerson(CredentialsDTOResp credentials);

    CredentialsDTOResp convertToCredentialsDTO(Person person);

    CredentialsDTOReqst convertToCredentialsDTOReqst(Person person);

    List<UserDTOResp> convertToCredentialsDTOList(List<Person> people);

    @Mappings({
        @Mapping(source = "username", target = "username"),
        @Mapping(source = "password", target = "password"),
        @Mapping(source = "sensor", target = "sensors", qualifiedByName = "sensorDTOToSensorList")
    })
    Person convertToPerson(CredentialsDTOWithSensorReqst credentials);

    @org.mapstruct.Named("sensorDTOToSensorList")
    default List<Sensor> sensorDTOToSensorList(SensorDTOWithOneReadingReqst sensorDTO) {
        if (sensorDTO == null) {
            return null;
        }

        List<Sensor> sensors = new ArrayList<>();
        Sensor sensor = new Sensor();
        sensor.setType(sensorDTO.getType());
        List<Reading> readings = new ArrayList<>();
        Reading reading = new Reading();
        reading.setIndication(sensorDTO.getReading().getIndication());
        reading.setDate(sensorDTO.getReading().getDate());
        readings.add(reading);
        sensor.setReadings(readings);
        sensors.add(sensor);
        return sensors;
    }
}