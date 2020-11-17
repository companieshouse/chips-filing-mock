FROM centos:7

RUN yum update -y && \
    yum install -y \
    epel-release-7 \
    zip \
    unzip \
    java-1.8.0-openjdk \
    maven \
    make && \
    yum clean all

COPY chips-filing-mock.jar /opt/chips-filing-mock/
COPY start-ecs /usr/local/bin/

RUN chmod 555 /usr/local/bin/start-ecs

CMD ["start-ecs"]
