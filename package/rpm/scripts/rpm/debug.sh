#!/bin/sh
nohup $JAVA_HOME/bin/java -jar $SERVER_OPTS $DEBUG_OPTS /opt/bahmni-event-log-service/lib/bahmni-event-log-service.jar >> /var/log/bahmni-event-log-service/bahmni-event-log-service.log 2>&1 &
echo $! > /var/run/bahmni-event-log-service/bahmni-event-log-service.pid
