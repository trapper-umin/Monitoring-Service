package monitoring.service.dev.utils.mappers;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PersonMapper {

    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person convertToPerson(CredentialsDTO credentials);
    CredentialsDTO convertToCredentialsDTO(Person person);
    List<CredentialsDTO> convertToCredentialsDTOList(List<Person> people);
}
