package monitoring.service.dev.services.db;

import java.util.List;
import monitoring.service.dev.models.Audit;
import monitoring.service.dev.repositories.IAuditRepository;
import monitoring.service.dev.repositories.jdbc.AuditRepository;
import monitoring.service.dev.utils.exceptions.ProblemWithSQLException;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final IAuditRepository repository;

    public AuditService(AuditRepository repository) {
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