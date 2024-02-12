package monitoring.service.dev.utils.mappers;

import java.util.List;
import monitoring.service.dev.dtos.requests.CredentialsDTOReqst;
import monitoring.service.dev.dtos.requests.CredentialsDTOWithSensorReqst;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person convertToPerson(CredentialsDTOResp credentials);

    CredentialsDTOResp convertToCredentialsDTO(Person person);

    CredentialsDTOReqst convertToCredentialsDTOReqst(Person person);

    List<CredentialsDTOResp> convertToCredentialsDTOList(List<Person> people);

    Person convertToPerson(CredentialsDTOWithSensorReqst credentials);
}