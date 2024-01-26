package monitoring.service.dev.repositories;

import monitoring.service.dev.models.Person;

public class RepositoryFactory {
    public static IPeopleRepository getRepository(){
        return Repository.getInstance();
    }
}
