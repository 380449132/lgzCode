@echo off
start cmd /k "cd %1 %2 &&jar tf consumer-0.0.1-SNAPSHOT.jar &&jar xf consumer-0.0.1-SNAPSHOT.jar BOOT-INF/classes/application.yml &&jar uf consumer-0.0.1-SNAPSHOT.jar BOOT-INF/classes/application.yml &&java -jar consumer-0.0.1-SNAPSHOT.jar --server.port=8083"
exit
