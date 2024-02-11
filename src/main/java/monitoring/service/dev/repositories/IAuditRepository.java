package monitoring.service.dev.repositories;

import java.util.List;
import monitoring.service.dev.models.Audit;

public interface IAuditRepository {

    List<Audit> get();

    void push(Audit audit);
}
