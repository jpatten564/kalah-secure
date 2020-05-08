docker exec -it keycloak /opt/jboss/keycloak/bin/standalone.sh \
    -Djboss.socket.binding.port-offset=100 \
    -Dkeycloak.migration.action=export \
    -Dkeycloak.migration.file=/tmp/export.json \
    -Dkeycloak.migration.provider=singleFile

docker cp keycloak:/tmp/export.json config/all.json
