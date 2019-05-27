docker run --name test-loader ^
    -ti --rm ^
    --network docker_vnet-saga-simple-1 ^
    -e CONCURRENCY=10 -e HOLD_FOR=300s -e RAMP_UP=10s ^
    rtershak/test-loader:latest