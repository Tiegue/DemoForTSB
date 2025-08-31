#!/bin/bash

# Simple export script for development
# Usage: ./exportApi.sh

echo "Exporting Postman collections..."

# Create collections directory
mkdir -p ApiCollections

# Get current timestamp in YYYYMMDD_HHMMSS format
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Export customer APIs
curl http://localhost:8080/v3/api-docs/all -o ApiCollections/openapi-all-${TIMESTAMP}.json
echo "✓ ApiCollections/openapi-all-${TIMESTAMP}.json"

curl http://localhost:8080/v3/api-docs/authentication -o ApiCollections/openapi-auth-${TIMESTAMP}.json
echo "✓ ApiCollections/openapi-auth-${TIMESTAMP}.json"

curl http://localhost:8080/v3/api-docs/customers -o ApiCollections/openapi-customers-${TIMESTAMP}.json
echo "✓ ApiCollections/openapi-customers-${TIMESTAMP}.json"

echo "Import the JSON files into Postman (File > Import)"
echo "Set baseUrl = http://localhost:8080 in Postman environment"