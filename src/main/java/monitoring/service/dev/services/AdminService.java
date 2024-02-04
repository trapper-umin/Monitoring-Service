package monitoring.service.dev.services;

import monitoring.service.dev.dtos.requests.CredentialsDTO;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IAdminRepository;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.RepositoryFactory;
import monitoring.service.dev.repositories.jdbc.AdminRepository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

public class AdminService {

    private static AdminService instance;
    private static final IAdminRepository adminRepository = new AdminRepository(); //TODO
    private static final IPeopleRepository peopleRepository = RepositoryFactory.getRepository(); //TODO
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
        return pMapper.convertToCredentialsDTOList(adminRepository.getAllUsers());
    }

    public void setAuthorities(String username) {
        Person person = peopleRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with username '"+username+"' was not found"));

        adminRepository.setAuthorities(person);
    }

    public void deleteAuthorities(String username) {
        Person person = peopleRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user with username '"+username+"' was not found"));

        adminRepository.deleteAuthorities(person);
    }
}
