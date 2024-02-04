package monitoring.service.dev.repositories;

import monitoring.service.dev.repositories.jdbc.PeopleRepository;

public class RepositoryFactory {

    public static IPeopleRepository getRepository() {
        return new PeopleRepository();
    }
}