#!/usr/bin/env sh
set -eu

envsubst '${API_HOST} ${API_PORT}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf
envsubst '${KEYCLOAK_HOST} ${KEYCLOAK_PORT}' < /usr/share/nginx/html/keycloak.json.template > /usr/share/nginx/html/keycloak.json

exec "$@"