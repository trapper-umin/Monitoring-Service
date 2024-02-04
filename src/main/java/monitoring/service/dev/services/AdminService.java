package monitoring.service.dev.services;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.PeopleRepository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class AdminService {

    private static AdminService instance;
    private static final PeopleRepository repository = PeopleRepository.getInstance();
    private static final PersonMapper pMapper = Mappers.getMapper(PersonMapper.class);

    private AdminService(){

    }

    public static AdminService getInstance(){
        if(instance==null){
            instance = new AdminService();
        }
        return instance;
    }

    public List<CredentialsDTO> getAllUsers() {
        return pMapper.convertToCredentialsDTOList(repository.getAllUsers());
    }

    public void setAuthorities(String username) {
        Person person = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with username '"+username+"' was not found"));

        repository.setAuthorities(person);
    }

    public void deleteAuthorities(String username) {
        Person person = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with username '"+username+"' was not found"));

        repository.deleteAuthorities(person);
    }
}
