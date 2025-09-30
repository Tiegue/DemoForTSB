# demo-for-tsb development log

```

make dev-local         # runs tests
make dev-local-fast    # skips tests (faster)
make dev-local-jar     # runs the built JAR
make obs-up      # start Prometheus + Grafana in Docker
make obs-logs    # tail their logs
make obs-down    # stop them
```

## Stage1 
### Step 1.1: Create project directory structure
- build architecture (project tree)
  - customer entity and related controller, service.
  - Disable all auth.
- basic pom.xml
### Step 1.2: Design an exception handler aspect based on the one in HappyGigs.
- BASE EXCEPTION HIERARCHY
  - Exception for resource not found scenarios
  - Exception for unauthorized access
  - Exception for forbidden operations
  - Exception for data validation errors
- DOMAIN-SPECIFIC EXCEPTIONS(Customer & Account)
### Step 1.3: Configure docker 
- Add docker-compose.yml, Makefile, Dockerfile, and .dockerignore
### Step 1.4 Add observability features
- Implement prometheus, grafana, postgresql, app
- Add ops directory for prometheus, grafana, postgresql
- Add health check.
- Add seq structured logging with gelf
- logback: add both correlationId and businessId which shows readable info in console.

success!!!
#### Quick checks
##### 1) App health
curl -s http://localhost:8080/actuator/health

##### 2) Prometheus can scrape the app
open http://localhost:9090/targets     # demofortsb-app should be UP
open http://localhost:9090/graph        # try: up{job="demofortsb-app"}

##### 3) Metrics exposed by Spring/ Micrometer
open http://localhost:8080/actuator/prometheus

##### 4) Grafana login
open http://localhost:3000              # admin / admin

Add Grafana → Prometheus datasource (one-time)

In Grafana UI:

Settings (gear) → Data sources → Add data source → Prometheus

URL: http://prometheus:9090

Save & test → should show “Data source is working”.

Then Dashboards → Import and try these Micrometer-friendly dashboards:

JVM (Micrometer): 4701

Spring Boot 3 / Micrometer: 19004 (or any you prefer)

##### 5) Optional: auto-provision Grafana (skip the UI clicks)
docker-compose.yml – add to the grafana service:

grafana:
# ...
volumes:
- grafana-data:/var/lib/grafana
- ./ops/grafana/provisioning:/etc/grafana/provisioning:ro


ops/grafana/provisioning/datasources/datasource.yml

apiVersion: 1
datasources:
- name: Prometheus
  type: prometheus
  access: proxy
  url: http://prometheus:9090
  isDefault: true


ops/grafana/provisioning/dashboards/dashboards.yml

apiVersion: 1
providers:
- name: 'Default'
  orgId: 1
  folder: ''
  type: file
  options:
  path: /etc/grafana/provisioning/dashboards/json


ops/grafana/provisioning/dashboards/json/README.md

Drop exported dashboard JSON files here to auto-import on startup.

```aiignore
postgresdb command in docker cli
psql -U tsb -d demofortsb
enter pw tsb
pg_isready -U tsb -d demofortsb
\dt # show table
\l
\q # quit
```

## to-do list, done 
- structured logging: seq, logback
- exception handling deal with traceid
- health check

# stage 2 Enhancing Customer Entity with Password Security, Data Masking, OpenAPI Configuration, and Grafana Monitoring
## step 2.1 update customer entity adding password field, create its DTOs
## step 2.2 implement sensitive data masking, and logback masking
## step 2.3 configue openApi
export json formate apis, can be imported to postman collection by each controller.
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs/customers
#!/bin/bash
```aiignore


# Simple export script for development
# Usage: ./export.sh

echo "Exporting Postman collections..."

# Create collections directory
mkdir -p collections

# Export customer APIs
curl http://localhost:8080/v3/api-docs/customers > collections/customers-api.json
echo "Customer APIs exported"

# Export all APIs
curl http://localhost:8080/v3/api-docs/all > collections/all-apis.json
echo "All APIs exported"

echo "Import the JSON files into Postman (File > Import)"
echo "Set baseUrl = http://localhost:8080 in your Postman environment"
```
## step 2.4 run success, can show on grafana, req,etc
##### 1) App health
curl -s http://localhost:8080/actuator/health

##### 2) Prometheus can scrape the app
open http://localhost:9090/targets     # demofortsb-app should be UP
open http://localhost:9090/graph        # try: up{job="demofortsb-app"}

##### 3) Metrics exposed by Spring/ Micrometer
open http://localhost:8080/actuator/prometheus

##### 4) Grafana login
open http://localhost:3000              # admin / admin
##### 5) seq 
structrued log.
http://localhost:5341
http://localhost:5341/#/events?range=7d
 - update customer
 - customer dtos:
   Complete DTOs Set:
   Request DTOs:

CustomerRegistrationRequest - Full registration with all fields + password
CustomerLoginRequest - Login using nationalId + password
PasswordUpdateRequest - Change password with current password verification
AdminPasswordResetRequest - Admin can reset any customer's password
PasswordResetRequest - Forgot password using nationalId
PasswordResetConfirmRequest - Reset password with token
CustomerUpdateRequest - Update profile (no email/nationalId/password)

Response DTOs:

CustomerResponse - Safe customer data (no sensitive fields)
LoginResponse - Login success with optional JWT token
SuccessResponse - Generic success messages
ErrorResponse - Generic error handling
ValidationErrorResponse - Detailed validation errors
### Multipole layers sensitive data solution.

# Stage 3: jwt login security

Login - Get JWT token
Register - Create new users
Role Assignment - Admin (nationalId=123456789) vs User
Protected Endpoints - Test authentication

Implementation Order:

Add dependencies
Create 4 security classes (JwtUtil, CustomUserDetailsService, JwtAuthFilter, SecurityConfig)
Create AuthController (only login/register)
Create TestController to verify it works
Run tests to confirm everything works

## @businessOperation add metrics in it,can automatically log + record Prometheus metrics per operation)? That way it’s not just logs, but also monitoring.
use AOP to add buinessId operation, MDCFilter for reques-scoped technical context to provide generic request context OncePerRequestFilter.(traceId, correlationId, spanId, restUri)
- MDCAspect, MDC.put and clear in it, also define log format, so do not need add log in real classes, just add annotaion @BusinessOperation to the class. the code will be clean.
- MDCAspect, handle exception and log error.
- MDCAspect, integrate with metric service to record metrics. 
example:
Dirty code
```aiignore
    @GetMapping("/admin-status")
    public ResponseEntity<?> checkAdminStatus() {
        String businessId = "check-admin-status";
        MDC.put("businessId", businessId);
        try {
            Customer admin = customerRepository.findByNationalId(adminNationalId)
                    .orElse(null);

            Map<String, Object> status = new HashMap<>();

            if (admin == null) {
                status.put("exists", false);
                status.put("message", "Admin user not found");
            } else {
                String maskNationalId = dataMaskingService.maskNationalId(admin.getNationalId());
                status.put("exists", true);
                status.put("email", admin.getEmail());
                status.put("hasPassword", admin.getPasswordHash() != null && !admin.getPasswordHash().isEmpty());
                status.put("isActive", admin.isActive());
                status.put("nationalId", maskNationalId);
            }

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            logger.error("Failed to check admin status", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to check admin status");
        }
    }
change to clean code
```
```aiignore
    @GetMapping("/admin-status")
    @BusinessOperation("check-admin-status")
    public ResponseEntity<?> checkAdminStatus() {
        String maskedNationalId = dataMaskingService.maskNationalId(adminNationalId);
        
        Customer admin = customerRepository.findByNationalId(adminNationalId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", maskedNationalId));

        Map<String, Object> status = Map.of(
            "exists", true,
            "email", admin.getEmail(),
            "hasPassword", admin.getPasswordHash() != null && !admin.getPasswordHash().isEmpty(),
            "nationalId", maskedNationalId);
        
        return ResponseEntity.ok(status);
    }

```
In this controller/service method, do not need to write try/catch for logging.

The method can just throw, and the aspect + a global exception handler will take care of logging and clean response.

## account & transaction 
- entities with builder
- liquibase with test data
- simple repository

# polish logging system while adding kong api gateway
## IN MDCFfilter class,using kong's X-Request-id as correlation id, finally generates a new uuid if not exists.
change the name X-Correlatio-id to X-Request-Id,as the name X-Request_Id is the better standard.
so change related file: MDCFilter.java, logback-spring.yml

## Micrometer Tracing manage traceId/spanId in spring boot.
since you already run Grafana + Prometheus + Seq, the cleanest move is to add Grafana Tempo for traces and wire Spring’s Micrometer/OTel to it.


# study and polish security implementation and login workflow
current workflow is correct
How the Authentication Flow Works:

Login Request: When a user attempts to login in AuthController.login(), the method calls:

java   authentication = authenticationManager.authenticate(
new UsernamePasswordAuthenticationToken(email, request.getPassword())
);

Authentication Manager: The AuthenticationManager (configured in SecurityConfig) uses a ProviderManager with a DaoAuthenticationProvider.
DaoAuthenticationProvider Configuration: In SecurityConfig.daoAuthenticationProvider():

java   DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
provider.setUserDetailsService(customerUserDetailsService);
provider.setPasswordEncoder(passwordEncoder());

Behind the Scenes: When authenticate() is called, the DaoAuthenticationProvider automatically:

Calls customerUserDetailsService.loadUserByUsername(email) to retrieve the user
Compares the provided password with the stored password hash using BCrypt
Returns an authenticated Authentication object if successful

This reduces your database queries from 4 to 2:

Before: Check exists → Check active → Authenticate (loadUserByUsername) → Get role → Get customer for response
After: Authenticate (loadUserByUsername) → Get customer for response

# Improve login related. use record as login DTOs.
two records:
record LoginUserInfo
record SecureLoginResponse
...
Pure Data Records (recommended):
Records contain ONLY data fields
Utility class provides computed methods
Cleaner separation of concerns

🚀 Why Records are PERFECT for Secure Login DTOs!
You're absolutely right to suggest records! Here are the compelling reasons:
✅ Security Benefits of Records

Immutable by Design - Records can't be modified after creation, preventing accidental data exposure
Compile-time Safety - All fields are final, eliminating security bugs from mutable state
No Reflection Vulnerabilities - Records are more resistant to reflection-based attacks
Clear Intent - Records signal "this is pure data, don't modify it"

✅ Code Quality Benefits

Concise - No boilerplate getters/setters/equals/hashCode
Type Safety - Compiler enforces structure
Modern Java - Best practices for Java 17+
Self-Documenting - Structure is immediately clear

✅ API Benefits

Predictable JSON - Jackson handles records beautifully
Factory Methods - Clean creation patterns like LoginUserInfo.from(customer)
Computed Properties - Methods like displayName() and isExpired()
Validation - Can add validation in compact constructors


# https implementation guide by claude
details see notion/kiwibank-api management junior role
## configure java https
Current Situation
✅ What's Working:

Spring Boot has HTTPS configured on port 9443 (local development)
Kong exposes port 8443 for SSL in docker-compose
HTTP endpoints work through Kong on port 8000

❌ What's Missing:

Kong doesn't have SSL certificates configured
Kong service points to HTTP backend (not HTTPS)
No SSL termination strategy defined
## Complete Kong HTTPS Configuration
1. Generate SSL Certificates
2. Update docker-compose-kong.yml
3. Update kong.yml for SSL Routes
4. Update Application Configuration
5. Update Security Configuration

6. Testing Commands
7. Production Considerations
https://claude.ai/chat/08076a76-b33e-4c2b-b23a-7a92ee8b7519
https://claude.ai/public/artifacts/3b908440-3e7d-46c1-ae45-1c4b335f9a45
https://claude.ai/chat/77193a35-a7d3-42b9-b3ed-29e30c845494


# Step-by-Step CI/CD Implementation Guide
https://claude.ai/public/artifacts/72358924-028d-4770-a916-700e9a5d0988

## Phase 0: Prerequisites & Preparation (30 minutes)
- Step 0.1 Verify Your Environment
- Step 0.2 Prepare Your DemoFortsb Repository
- Step 0.3 Create Directory Structure
## Phase 1: Build Automation Scripts (1 hour)
Should use local properties or docker properties, instead of default
- Step 1.1 Create the Build Script
- Step 1.2 Create the Test Script
  - Add JaCoCo plugin to pom.xml.
    JaCoCo Plugin Purpose
    JaCoCo (Java Code Coverage) is a code coverage tool that measures how much of your code is actually tested by your unit tests.
    What it does:
    Tracks test coverage - Shows which lines of your code are executed during tests
Generates reports - Creates HTML/XML reports showing:
% of code covered by tests
Which lines are tested (green) vs untested (red)
Branch coverage (if/else statements)
Coverage per class, package, and method
Quality assurance - Helps you:
Find untested code
Ensure critical code paths have tests
Meet coverage targets (e.g., "we need 80% coverage")

Example Report:
After running tests with JaCoCo, you get a report like:
Service Layer:        85% coverage
Controller Layer:     72% coverage
UserService.java:     90% coverage ✅
PaymentService.java:  45% coverage ⚠️
**Maybe use Qodana instead of jacoco

- Step 1.3 Create Wait-for-Services Script

## Phase 2: Test Infrastructure Setup (1.5 hours)
This is duplicate as compose file have postgres and redis. so jump to phase3
- Step 2.1 Create Test Docker Compose

## Phase 3: First GitHub Actions Workflow 
- Step 3.1: Create Simple CI Workflow
- 



