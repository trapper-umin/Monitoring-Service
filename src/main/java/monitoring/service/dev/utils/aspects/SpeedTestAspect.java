package monitoring.service.dev.utils.aspects;

import java.time.LocalDateTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SpeedTestAspect {

    @Pointcut("@annotation(monitoring.service.dev.utils.annotations.SpeedTest)")
    public void speedTest() {
    }

    @Around("speedTest()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        double executionTime = (double) (System.currentTimeMillis() - start) / 60;
        System.out.println(
            "[" + LocalDateTime.now() + "] Method : " + joinPoint.getSignature().getName()
                + " : was called and processed in " + String.format("%.4f", executionTime)
                + " seconds");
        return proceed;
    }
}
