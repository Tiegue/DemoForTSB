# create role + db
docker exec -it postgres-local psql -U postgres -c "CREATE USER konga WITH PASSWORD 'konga' LOGIN;"
docker exec -it postgres-local psql -U postgres -c "CREATE DATABASE konga OWNER konga;"

# grant privileges on the DB & public schema (so Konga can create tables)
docker exec -it postgres-local psql -U postgres -d konga -c "GRANT ALL PRIVILEGES ON DATABASE konga TO konga;"
docker exec -it postgres-local psql -U postgres -d konga -c "GRANT USAGE, CREATE ON SCHEMA public TO konga;"

# (often needed by Konga for UUIDs — safe to run)
docker exec -it postgres-local psql -U postgres -d konga -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"
docker exec -it postgres-local psql -U postgres -d konga -c "CREATE EXTENSION IF NOT EXISTS pgcrypto;"


# if in docker desktop:
-- See what DBs exist
\l

-- If there is no 'konga' DB yet, create it and give ownership to konga:
CREATE DATABASE konga OWNER konga;

-- Switch into it:
\c konga

-- Ensure schema privileges so Konga can create tables:
GRANT USAGE, CREATE ON SCHEMA public TO konga;
ALTER SCHEMA public OWNER TO konga;

-- (Often helpful) Enable extensions:
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;
