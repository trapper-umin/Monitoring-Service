package monitoring.service.dev.services.db;

import java.util.List;
import monitoring.service.dev.dtos.responses.UserDTOResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IAdminRepository;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.repositories.jdbc.AdminRepository;
import monitoring.service.dev.repositories.jdbc.PeopleRepository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final PersonMapper pMapper;
    private final IAdminRepository adminRepository;
    private final IPeopleRepository peopleRepository;

    public AdminService(PersonMapper pMapper, PeopleRepository peopleRepository,
        AdminRepository adminRepository) {

        this.pMapper = pMapper;
        this.peopleRepository = peopleRepository;
        this.adminRepository = adminRepository;
    }

    public List<UserDTOResp> getAllUsers() {
        return pMapper.convertToCredentialsDTOList(adminRepository.getAllUsers());
    }

    public void setAuthorities(String username) throws ProblemWithSQLException, NotFoundException {
        Person person = peopleRepository.findByUsername(username).orElseThrow(
            () -> new NotFoundException("user with username '" + username + "' was not found"));

        adminRepository.setAuthorities(person);
    }

    public void deleteAuthorities(String username) throws ProblemWithSQLException, NotFoundException{
        Person person = peopleRepository.findByUsername(username).orElseThrow(
            () -> new NotFoundException("user with username '" + username + "' was not found"));

        adminRepository.deleteAuthorities(person);
    }
}