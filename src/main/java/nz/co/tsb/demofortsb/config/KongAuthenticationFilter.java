package nz.co.tsb.demofortsb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Filter to validate requests are coming through Kong API Gateway
 * Checks for Kong-specific headers to prevent direct backend access
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Execute Only after MDCFilter
public class KongAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(KongAuthenticationFilter.class);

//    private static final String KONG_AUTH_HEADER = "X-Kong-Auth";
//    private static final String KONG_PROXY_HEADER = "X-Kong-Proxy";
    private static final String HDR_KONG_AUTH  = "X-Kong-Auth";
    private static final String HDR_KONG_PROXY = "X-Kong-Proxy";


    private static final Set<String> EXACT_WHITELIST = Set.of(
            "/favicon.ico"
    );
    private static final List<String> PREFIX_WHITELIST = List.of(
            "/actuator/",
            "/h2-console",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars/",
            "/static/"
    );

    private final ObjectMapper objectMapper;

    @Value("${kong.shared.secret:shared-secret-12345}")
    private String expectedSecret;

    public KongAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        if (EXACT_WHITELIST.contains(path)) return true;
        for (String prefix : PREFIX_WHITELIST) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        final String kongProxy = req.getHeader(HDR_KONG_PROXY);
        final String kongAuth  = req.getHeader(HDR_KONG_AUTH);

        final boolean isFromKong =
                "true".equalsIgnoreCase(kongProxy) &&
                        expectedSecret.equals(kongAuth);

        if (!isFromKong) {
            logger.warn("Direct access blocked: path={}, ip={}, {}={}, {}={}",
                    req.getRequestURI(),
                    req.getRemoteAddr(),
                    HDR_KONG_PROXY, kongProxy,
                    HDR_KONG_AUTH, (kongAuth == null ? "null" : "***"));
            writeForbidden(res, req.getRequestURI());
            return;
        }

        chain.doFilter(req, res);
    }


    private void writeForbidden(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        var body = Map.of(
                "error", "Forbidden",
                "message", "Access denied. Requests must go through API Gateway.",
                "timestamp", Instant.now().toString(),
                "path", path
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

//------ below is old code, NOT CLEAN, KEPT FOR COPARISON AND LEARNING----------------------------
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response,
//                         FilterChain chain) throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//
//
//        String requestUri = httpRequest.getRequestURI();
//        String method = httpRequest.getMethod();
//
//        // Skip validation for actuator health checks (allow internal monitoring)
//        if (requestUri.startsWith("/actuator/health")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Skip validation for error pages
//        if (requestUri.startsWith("/error")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Validate Kong authentication header, only for debugging.
//        String kongAuth = httpRequest.getHeader(KONG_AUTH_HEADER);
//        String kongProxy = httpRequest.getHeader(KONG_PROXY_HEADER);
//
//
//        logger.info("Kong Auth Header: {}", kongAuth);
//        logger.info("Kong Proxy Header: {}", kongProxy);
//
//        if (!expectedSecret.equals(kongAuth)) {
//            logger.warn("Unauthorized direct access attempt - Missing or invalid Kong auth header. " +
//                            "URI: {}, Method: {}, Kong-Auth: {}, Remote-IP: {}",
//                    requestUri, method, kongAuth, httpRequest.getRemoteAddr());
//
//            sendForbiddenResponse(httpResponse, "Access denied. Requests must go through API Gateway.");
//            return;
//        }
//
//        if (!"true".equals(kongProxy)) {
//            logger.warn("Unauthorized direct access attempt - Missing Kong proxy header. " +
//                            "URI: {}, Method: {}, Remote-IP: {}",
//                    requestUri, method, httpRequest.getRemoteAddr());
//
//            sendForbiddenResponse(httpResponse, "Access denied. Invalid request source.");
//            return;
//        }
//
//        // Log successful Kong request (debug level)
//        logger.debug("Valid Kong request - URI: {}, Method: {}, Request-ID: {}",
//                requestUri, method, httpRequest.getHeader("X-Request-Id"));
//
//        // Request is valid, continue processing
//        chain.doFilter(request, response);
//    }

//    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(String.format(
//                "{\"error\":\"Forbidden\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
//                message, java.time.Instant.now()));
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        logger.info("Kong Authentication Filter initialized with secret: {}",
//                expectedSecret.substring(0, Math.min(4, expectedSecret.length())) + "***");
//    }

    @Override
    public void destroy() {
        logger.info("Kong Authentication Filter destroyed");
    }
}