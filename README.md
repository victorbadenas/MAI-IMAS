# MAI-IMAS

Practical work for IMAS subject in MAI

First delivery of the practical work

## Goals

Creation of the envioronment and initialisation of all elements.

## Description

The main goal of this activity is that all of you begin the implementation of the practical work. At this point, it is required that the agent-based system has been initialised according to the current requirements.

The following aspects will be evaluated in this activity:

- Creation of the Java project and import all required libraries (e. g. JADE, third-party entities).

- Creation of the first (and required) agents.

- Distinguish the different requests sent by users and distinguish these basic functionalities.

- Creation of all intelligent agents as requested (e.g. fuzzy agents), and maybe, initialisation of all them according to the given parameters.

## Execution instructions

### Ubuntu (18.04, 20.04)

First execution including maven install, `JAVA_HOME` environment variable, jdk installation and flags to bypass jade certificates.

```bash
sudo apt-get install maven
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
sudo apt-get install openjdk-8-jdk
mvn clean compile package -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
java -jar target/imas-platform-1.0.0.jar -conf imas-platform.properties
```

For all other execution cases, only run:

```bash
mvn clean compile package
java -jar target/imas-platform-1.0.0.jar -conf imas-platform.properties
```

### Windows 10

