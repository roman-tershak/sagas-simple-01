FROM blazemeter/taurus
RUN mkdir -p /bzt-configs
COPY test-loader.yml /bzt-configs/
WORKDIR /bzt-configs
ENTRYPOINT ["bash", "-c", "bzt -l /tmp/artifacts/bzt.log test-loader.yml"]