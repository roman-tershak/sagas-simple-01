docker run -d --name mariadbsrv ^
    --network vnet-saga-simple-1 ^
    -p 3306:3306 ^
    -eMYSQL_ROOT_PASSWORD=htgrfedw -eMYSQL_DATABASE=sagasimple1 -eMYSQL_USER=sagau -eMYSQL_PASSWORD=sagahtgrfe ^
    mariadb:10.4