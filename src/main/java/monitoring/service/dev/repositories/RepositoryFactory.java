package monitoring.service.dev.repositories;

public class RepositoryFactory {
    public static IPeopleRepository getRepository(){
        return PeopleRepository.getInstance();
    }
}
