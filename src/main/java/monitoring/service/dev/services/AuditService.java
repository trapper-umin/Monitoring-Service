package monitoring.service.dev.services;

import java.util.List;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.repositories.IAuditRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;

public class AuditService {

    private final IAuditRepository repository;

    public AuditService(IAuditRepository repository) {
        this.repository = repository;
    }

    public List<Audit> getAudit() {
        return repository.get();
    }

    public void postAudit(Audit audit) {
        try {
            repository.push(audit);
        } catch (ProblemWithSQLException e) {
            System.out.println(e.getMessage());
        }
    }
}