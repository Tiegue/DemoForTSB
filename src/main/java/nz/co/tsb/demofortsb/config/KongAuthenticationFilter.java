package nz.co.tsb.demofortsb.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to validate requests are coming through Kong API Gateway
 * Checks for Kong-specific headers to prevent direct backend access
 */
@Component
@Order(1) // Execute before other filters
public class KongAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(KongAuthenticationFilter.class);

    private static final String KONG_AUTH_HEADER = "X-Kong-Auth";
    private static final String KONG_PROXY_HEADER = "X-Kong-Proxy";

    @Value("${kong.shared.secret:shared-secret-12345}")
    private String expectedSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;



        String requestUri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Skip validation for actuator health checks (allow internal monitoring)
        if (requestUri.startsWith("/actuator/health")) {
            chain.doFilter(request, response);
            return;
        }

        // Skip validation for error pages
        if (requestUri.startsWith("/error")) {
            chain.doFilter(request, response);
            return;
        }

        // Validate Kong authentication header, only for debugging.
        String kongAuth = httpRequest.getHeader(KONG_AUTH_HEADER);
        String kongProxy = httpRequest.getHeader(KONG_PROXY_HEADER);


        logger.info("Kong Auth Header: {}", kongAuth);
        logger.info("Kong Proxy Header: {}", kongProxy);

        if (!expectedSecret.equals(kongAuth)) {
            logger.warn("Unauthorized direct access attempt - Missing or invalid Kong auth header. " +
                            "URI: {}, Method: {}, Kong-Auth: {}, Remote-IP: {}",
                    requestUri, method, kongAuth, httpRequest.getRemoteAddr());

            sendForbiddenResponse(httpResponse, "Access denied. Requests must go through API Gateway.");
            return;
        }

        if (!"true".equals(kongProxy)) {
            logger.warn("Unauthorized direct access attempt - Missing Kong proxy header. " +
                            "URI: {}, Method: {}, Remote-IP: {}",
                    requestUri, method, httpRequest.getRemoteAddr());

            sendForbiddenResponse(httpResponse, "Access denied. Invalid request source.");
            return;
        }

        // Log successful Kong request (debug level)
        logger.debug("Valid Kong request - URI: {}, Method: {}, Request-ID: {}",
                requestUri, method, httpRequest.getHeader("X-Request-Id"));

        // Request is valid, continue processing
        chain.doFilter(request, response);
    }

    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
                "{\"error\":\"Forbidden\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message, java.time.Instant.now()));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Kong Authentication Filter initialized with secret: {}",
                expectedSecret.substring(0, Math.min(4, expectedSecret.length())) + "***");
    }

    @Override
    public void destroy() {
        logger.info("Kong Authentication Filter destroyed");
    }
}