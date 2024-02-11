package monitoring.service.dev.repositories.localstorage;

import java.util.ArrayList;
import java.util.List;
import monitoring.service.dev.models.Audit;

@Deprecated
public class AuditRepository {

    private static final List<Audit> auditDB = new ArrayList<>();
    private static AuditRepository instance;

    private AuditRepository() {
    }

    public static AuditRepository getInstance() {
        if (instance == null) {
            instance = new AuditRepository();
        }
        return instance;
    }

    public List<Audit> get() {
        return auditDB;
    }

    public void push(Audit audit) {
        auditDB.add(audit);
    }
}
