#!/bin/bash
##
# Start script for chips-filing-mock


PORT=8080
exec java -jar -Dserver.port="${PORT}" "chips-filing-mock.jar"
