#!/bin/bash
##
# Start script for chips-filing-mock


PORT=8080
exec java -jar -Dserver.port="${PORT}" -XX:MaxRAMPercentage=80 "chips-filing-mock.jar"
