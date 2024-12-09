ARG IMAGE_VERSION="latest"
FROM 416670754337.dkr.ecr.eu-west-2.amazonaws.com/ci-corretto-runtime-21:${IMAGE_VERSION} 

WORKDIR /opt

COPY chips-filing-mock.jar /opt/chips-filing-mock/
COPY start-ecs /usr/local/bin/

RUN chmod 555 /usr/local/bin/start-ecs

CMD ["start-ecs"]
