package nz.co.tsb.demofortsb.logging;

import nz.co.tsb.demofortsb.healthCheck.MetricService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MDCAspect {

    private static final Logger log = LoggerFactory.getLogger(MDCAspect.class);
    private final MetricService metricService;

    public MDCAspect(MetricService metricService) {
        this.metricService = metricService;
    }

    @Around("@annotation(businessOperation)")
    public Object setBusinessMDC(ProceedingJoinPoint joinPoint, BusinessOperation businessOperation) throws Throwable {
        String operationName = businessOperation.value();
        MDC.put("businessId", operationName);
        long startTime = System.currentTimeMillis();
        try {
            log.info("Business operation started: {}", operationName);

            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed {} in {} ms", operationName, duration);

            metricService.recordOperation(operationName, duration, true);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Filed {} in {} ms", operationName, System.currentTimeMillis() - startTime, e);

            metricService.recordOperation(operationName, duration, false);

            throw e;
        } finally {
            MDC.remove("businessId");
        }
    }
}