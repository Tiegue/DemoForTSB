# 1. Test Spring Boot directly
curl http://localhost:8080/api/health

# 2. If that fails, start Spring Boot
mvn spring-boot:run

# 3. Test Kong connectivity
curl http://localhost:8000/api/health

# 4. Check Kong logs
docker-compose -f docker-compose-kong.yml logs kong | tail -20

# 5. Test login directly to Spring Boot first
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email": "tiegue303@example.com", "password": "password123!"}'

# 6. If direct login works, then test through Kong
curl -X POST http://localhost:8000/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email": "tiegue303@example.com", "password": "password123!"}'