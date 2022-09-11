@echo off
start cmd /k "cd /d d:\eureka_server\provider8082 &&jar tf provider-0.0.1-SNAPSHOT.jar &&jar xf provider-0.0.1-SNAPSHOT.jar BOOT-INF/classes/application.yml &&jar uf provider-0.0.1-SNAPSHOT.jar BOOT-INF/classes/application.yml &&java -jar provider-0.0.1-SNAPSHOT.jar --server.port=8082"
exit