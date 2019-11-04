artifact_name       := chips-filing-mock
commit              := $(shell git rev-parse --short HEAD)
tag                 := $(shell git tag -l 'v*-rc*' --points-at HEAD)
version             := $(shell if [[ -n "$(tag)" ]]; then echo $(tag) | sed 's/^v//'; else echo $(commit); fi)
artifactory_publish := $(shell if [[ -n "$(tag)" ]]; then echo release; else echo dev; fi)

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name).jar
	rm -f ./$(artifact_name)-$(version).zip

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -DskipTests=true -Pdev
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit test-integration

.PHONY: test-unit
test-unit: clean
	mvn test

.PHONY: test-integration
test-integration:
	mvn verify -Pintegration-test

.PHONY: package
package:
	$(eval tmpdir:=$(shell mktemp -d ./target/build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar
