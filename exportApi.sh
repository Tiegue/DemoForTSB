#!/bin/bash

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