CHIPS filing mock
==========
This service mocks the filing backend processing. 

It consumes [FilingReceived](https://github.com/companieshouse/chs-kafka-schemas/blob/master/schemas/filing-received.avsc) objects from a Kafka topic and calls the [Kafka API](https://github.com/companieshouse/chs-kafka-api) to process the filing.

The filing will automatically be accepted unless it matches one of the pre-determined reject criteria:
- A change of address transaction using one of the Companies House's post codes (CF14 3UZ, BT28BG, SW1H9EX, EH39FF).

As new filings are exposed to external software vendors more reject criteria should be added to this list and the relevant public documentation. 

Requirements
------------
In order to run the API locally you'll need the following installed on your machine:

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Kafka](https://kafka.apache.org)


Getting started
---------------
1. Run `make`
2. Run `./start.sh`

## Environment Variables
The supported environmental variables have been categorised by use case and are as follows.

### Code Analysis Variables
| Name                   | Description                                                                                                                               | Mandatory | Default | Example          |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|-----------|---------|------------------|
| CODE_ANALYSIS_HOST_URL | The host URL of the code analysis server. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                        | ✓         |         | http://HOST:PORT |
| CODE_ANALYSIS_LOGIN    | The analysis server account to use when analysing or publishing. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters) | ✓         |         | login            |
| CODE_ANALYSIS_PASSWORD | The analysis server account password. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                            | ✓         |         | password         |

### Deployment Variables
| Name                      | Description                                                       | Mandatory | Default               | Example |
|---------------------------|-------------------------------------------------------------------|-----------|-----------------------|---------|
| CONSUMER_SLEEP_MS         | Time in milliseconds the service will sleep between polling Kafka | ✓         || 10000                 |     |
| KAFKA_BROKER_ADDR         | Address of the Kafka broker                                       | ✓         || localhost:9092        |     |
| KAFKA_CONSUMER_TOPIC      | The Kafka topic to consume from                                   | ✓         || filing-received       |     |
| KAFKA_CONSUMER_TIMEOUT_MS | Timeout for consuming messages from Kafka                         | ✓         || 100                   |     |
| CHS_KAFKA_API_LOCAL_URL   | URL of the Kafka API service                                      | ✓         || http://localhost:9000 |     |
