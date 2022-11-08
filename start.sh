#!/bin/bash
set -x

echo "API_CSCARD_APIURL=${CSCARDS_ENDPOINT}"
echo "API_SCOREDCARDS_APIURL=${SCOREDCARDS_ENDPOINT}"


docker run -p $HTTP_PORT:8081 -it --env API_CSCARD_APIURL="${CSCARDS_ENDPOINT}" \
                                  --env API_SCOREDCARDS_APIURL="${SCOREDCARDS_ENDPOINT}" $(docker build -q .)