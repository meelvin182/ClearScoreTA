#!/bin/bash
set -x
export API_CSCARD_APIURL="${CSCARDS_ENDPOINT}"
export API_SCOREDCARDS_APIURL="${SCOREDCARDS_ENDPOINT}"
export SERVER_PORT="${HTTP_PORT}"
./gradlew clean build bootRun