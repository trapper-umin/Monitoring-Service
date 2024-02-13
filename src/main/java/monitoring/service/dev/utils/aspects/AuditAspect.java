package monitoring.service.dev.utils.aspects;

import monitoring.service.dev.models.Audit;
import monitoring.service.dev.repositories.IAuditRepository;
import monitoring.service.dev.repositories.jdbc.AuditRepository;
import monitoring.service.dev.services.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AuditAspect {

    private final IAuditRepository repository = new AuditRepository();
    private final AuditService auditService = new AuditService(repository);

    @Pointcut("execution(protected * monitoring.service.dev.controllers.impl.*Controller.*(..))")
    public void controllersAudit() {}

    @After("controllersAudit()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Audit audit = new Audit();
        audit.setLog("Method " + methodName + " in " + className + " was called");

        auditService.postAudit(audit);
    }
}