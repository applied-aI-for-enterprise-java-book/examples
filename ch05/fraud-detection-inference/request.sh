#!/bin/bash

curl -X POST localhost:8080/inference \
     -H 'Content-type:application/json' \
     -d '{"txId": "1234",
          "distanceFromLastTransaction": 0.3111400080477545,
          "ratioToMedianPrice": 1.9459399775518593,
          "usedChip": true,
          "usedPinNumber": true,
          "onlineOrder": false}'

echo -e '\n'

curl -X POST localhost:8080/inference \
     -H 'Content-type:application/json' \
     -d '{"txId": "5678",
          "distanceFromLastTransaction": 0.3111400080477545,
          "ratioToMedianPrice": 1.9459399775518593,
          "usedChip": true,
          "usedPinNumber": false,
          "onlineOrder": false}'