// src/main/java/nz/co/tsb/demofortsb/logging/CorrelationFilter.java
package nz.co.tsb.demofortsb.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter extends OncePerRequestFilter {


    @Override public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) req;
        String corrId = Optional.ofNullable(http.getHeader("X-Correlation-Id"))
                .orElse(UUID.randomUUID().toString());
        String traceId = corrId.replace("-", "");
        String spanId = Optional.ofNullable(http.getHeader("X-Span-Id"))
                .orElse(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        String requestUri = http.getRequestURI();

        MDC.put("correlationId", corrId);
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);
        MDC.put("requestUri", requestUri);

        res.setHeader("X-Correlation-Id", corrId);
        res.setHeader("X-Trace-Id", traceId);
        res.setHeader("X-Span-Id", spanId);
        try { chain.doFilter(req, res); }
        finally { MDC.clear(); }
    }
}
