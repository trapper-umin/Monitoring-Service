package monitoring.service.dev.services;

import monitoring.service.dev.models.Audit;
import monitoring.service.dev.repositories.AuditRepository;

import java.util.List;

public class AuditService {

    private static AuditService instance;
    private static final AuditRepository repository = AuditRepository.getInstance();

    private AuditService(){}

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }


    public List<Audit> getAudit() {
        return repository.get();
    }

    public void postAudit(Audit audit) {
        repository.push(audit);
    }
}
