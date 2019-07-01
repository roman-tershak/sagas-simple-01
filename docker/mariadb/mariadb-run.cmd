docker run -d --name mariadb-service ^
    --network vnet-saga-simple-1 ^
    -eMYSQL_ROOT_PASSWORD=htgrfedw -eMYSQL_DATABASE=sagasimple1 -eMYSQL_USER=sagau -eMYSQL_PASSWORD=sagahtgrfe ^
    mariadb:10.4