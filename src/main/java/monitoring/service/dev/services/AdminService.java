package monitoring.service.dev.services;

import java.util.List;
import monitoring.service.dev.dtos.responses.CredentialsDTOResp;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IAdminRepository;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.utils.exceptions.NotFoundException;
import monitoring.service.dev.utils.mappers.PersonMapper;
import org.mapstruct.factory.Mappers;

public class AdminService {

    private static final PersonMapper pMapper = Mappers.getMapper(PersonMapper.class);
    private final IAdminRepository adminRepository;
    private final IPeopleRepository peopleRepository;

    public AdminService(IPeopleRepository peopleRepository, IAdminRepository adminRepository) {
        this.peopleRepository = peopleRepository;
        this.adminRepository = adminRepository;
    }

    public List<CredentialsDTOResp> getAllUsers() {
        return pMapper.convertToCredentialsDTOList(adminRepository.getAllUsers());
    }

    public void setAuthorities(String username) {
        Person person = peopleRepository.findByUsername(username).orElseThrow(
            () -> new NotFoundException("user with username '" + username + "' was not found"));

        adminRepository.setAuthorities(person);
    }

    public void deleteAuthorities(String username) {
        Person person = peopleRepository.findByUsername(username).orElseThrow(
            () -> new NotFoundException("user with username '" + username + "' was not found"));

        adminRepository.deleteAuthorities(person);
    }
}
