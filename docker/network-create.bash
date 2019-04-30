docker network create vnet-saga-simple-1

#docker run --name mariadbsrv --network vnet-saga-simple-1 -v $pwd/data/mariadb:/var/lib/mysql -eMYSQL_ROOT_PASSWORD=htgrfedw -eMYSQL_DATABASE=sagasimple1 -eMYSQL_USER=sagau -eMYSQL_PASSWORD=sagahtgrfe -d mariadb:10.4

docker run --name activemqsrv --network vnet-saga-simple-1 -d rtershak/activemq:5.15.9