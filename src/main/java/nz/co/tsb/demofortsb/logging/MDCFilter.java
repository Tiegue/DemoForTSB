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

    private static final String HDR_REQ_ID = "X-Request-Id";      // Kong correlation-id plugin

    @Override public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        // Prefer Kong's header; fallback to alternate; else generate
        String reqId = Optional.ofNullable(req.getHeader(HDR_REQ_ID))
                        .orElse(UUID.randomUUID().toString());

        String traceId = reqId.replace("-", "");
        String spanId = Optional.ofNullable(req.getHeader("X-Span-Id"))
                .orElse(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        String requestUri = req.getRequestURI();

        MDC.put("reqId", reqId);
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);
        MDC.put("requestUri", requestUri);

        res.setHeader("X-Request-Id", reqId);
        res.setHeader("X-Trace-Id", traceId);
        res.setHeader("X-Span-Id", spanId);
        try { chain.doFilter(req, res); }
        finally { MDC.clear(); }
    }
}
