package nz.co.tsb.demofortsb.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MDCAspect {

    @Around("@annotation(businessOperation)")
    public Object setBusinessMDC(ProceedingJoinPoint joinPoint, BusinessOperation businessOperation) throws Throwable {
        MDC.put("businessId", businessOperation.value());
        try {
            return joinPoint.proceed();
        } finally {
            MDC.remove("businessId");
        }
    }
}