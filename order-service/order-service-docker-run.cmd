docker run --name order-service ^
    -ti --rm ^
    --network vnet-saga-simple-1 ^
    -e SPRING_DATASOURCE_URL=jdbc:mariadb://mariadbsrv:3306/sagasimple1 ^
    -e SPRING_DATASOURCE_USERNAME=sagau ^
    -e SPRING_DATASOURCE_PASSWORD=sagahtgrfe ^
    -e SPRING_ACTIVEMQ_BROKER_URL=tcp://activemqsrv:61616 ^
    -e SPRING_JMS_REDELIVERY_POLICY_MAXIMUM_REDELIVERIES=300 ^
    -e JAVA_TOOL_OPTIONS=\"-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n\" ^
    -p 8000:8000 ^
    saga-tests-simple-1/order-service:1.0-NEST-TRANS