package monitoring.service.dev.repositories;

import monitoring.service.dev.models.Audit;

import java.util.ArrayList;
import java.util.List;

public class AuditRepository {

    private static AuditRepository instance;

    private static final List<Audit> auditDB = new ArrayList<>();

    private AuditRepository(){}

    public static AuditRepository getInstance(){
        if(instance==null){
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
