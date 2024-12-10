CHIPS filing mock
==========
This service mocks the filing backend processing. 

It consumes [FilingReceived](https://github.com/companieshouse/chs-kafka-schemas/blob/master/schemas/filing-received.avsc) objects from a Kafka topic and calls the [Kafka API](https://github.com/companieshouse/chs-kafka-api) to process the filing.

The filing will automatically be accepted unless it matches one of the pre-determined reject criteria:
- A change of address transaction using one of the Companies House's post codes (CF14 3UZ, BT28BG, SW1H9EX, EH39FF).
- A change of registered email address transaction where the email address belongs to Companies House (@companieshouse.gov.uk).
- An insolvency transaction (the logic is looking for a submission kind containing the string 'insolvency') where the first practitioner associated with the insolvency case is using one of the Companies House's post codes (CF14 3UZ, BT28BG, SW1H9EX, EH39FF).

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

## Terraform ECS

The Terraform code to deploy this service is kept in this repository currently - [test-data-stack](https://github.com/companieshouse/test-data-stack/tree/master)

### What does this code do?

The code present in test-data-stack repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        |test-data                                     | ECS cluster (stack) the service belongs to
**Load balancer**      |N/A                                           | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/chips-filing-mock) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/chips-filing-mock)                                  | Concourse pipeline link in shared services, runs upto a docker build
**Concourse pipeline deploy**     |[Pipeline link test-data-stack](https://ci.platform.aws.chdev.org/teams/team-platform/pipelines/test-data-stack) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/platform/team-platform/test-data-stack)                                  | Concourse pipeline link for test-data-stack - deploys the terraform code - due to be migrated to new concourse


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)
