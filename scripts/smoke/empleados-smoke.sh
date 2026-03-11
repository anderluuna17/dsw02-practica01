#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
USER_NAME="${APP_BASIC_USER:-admin}"
USER_PASS="${APP_BASIC_PASSWORD:-admin123}"

create_response=$(curl -sS -u "$USER_NAME:$USER_PASS" \
  -H "Content-Type: application/json" \
  -X POST "$BASE_URL/api/v1/empleados" \
  -d '{"nombre":"Smoke User","direccion":"Smoke 123","telefono":"5551111111"}')

clave=$(echo "$create_response" | sed -n 's/.*"clave":"\([^"]*\)".*/\1/p')
if [[ -z "$clave" ]]; then
  echo "No se obtuvo clave en alta"
  exit 1
fi

curl -sS -u "$USER_NAME:$USER_PASS" "$BASE_URL/api/v1/empleados/$clave" >/dev/null
curl -sS -u "$USER_NAME:$USER_PASS" "$BASE_URL/api/v1/empleados?page=0&size=5" >/dev/null
curl -sS -u "$USER_NAME:$USER_PASS" -X DELETE "$BASE_URL/api/v1/empleados/$clave" >/dev/null

echo "Smoke OK for clave $clave"
