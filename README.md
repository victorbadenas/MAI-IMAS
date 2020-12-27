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

## Requirements

- Maven 3+
- Java 6+ (_Java 8 Recommended_)

Operating System dependent installation instructions:

### Ubuntu (18.04, 20.04)

```bash
sudo apt-get install openjdk-8-jdk
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
sudo apt-get install maven
```

### Windows 10

1. Download Java 8 JDK from [Oracle's Website](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html).
2. Run the downloaded installer (e.g., "jdk-8.0.{x}_windows-x86_bin.exe"), which installs both the JDK and JRE.
3. Include JDK's "bin" Directory in the PATH  
  3.1 Launch "Control Panel" ⇒ (Optional) "System and Security" ⇒ "System" ⇒ Click "Advanced system settings" on the left pane.  
  3.2 Switch to "Advanced" tab ⇒ Click "Environment Variables" button.  
  3.3 Under "System Variables" (the bottom pane), scroll down to select variable "Path" ⇒ Click "Edit...".  
  3.4 You shall see a TABLE listing all the existing PATH entries. Click "New" ⇒ Click "Browse" and navigate to your JDK's "bin" directory, i.e., "c:\Program Files\Java\jdk-8.0.{x}\bin", where {x} is your installation update number ⇒ Select "Move Up" to move this entry all the way to the TOP.  

    **Note: If you have started CMD, you need to re-start for the new environment settings to take effect.**

4. Verify the JDK Installation  
  4.1 Launch a CMD  
  4.2 Issue "path" command to list the contents of the PATH environment variable. Check to make sure that your JDK's "bin" is listed in the PATH
  4.3 Issue "javac -version", "java -version" commands to verify that JDK/JRE are properly installed and display their version

5. Download [maven](https://maven.apache.org/download.cgi). You just have to unzip it to a folder, no installation process is required. 
6. Repeat Step 3 to include in the `path` the folder you just unzipped in the last step.
7. Repeat Step 4 to check that maven is correctly installed, issuing "mvn -version" command.

For more information, check the following: [JDK Installation](https://www3.ntu.edu.sg/home/ehchua/programming/howto/JDK_Howto.html), [Maven Installation](https://mkyong.com/maven/how-to-install-maven-in-windows/).

## Execution instructions

The first time you execute it, it's important to include the `Dmaven.wagon.http.ssl.insecure` and `maven.wagon.http.ssl.allowall` as the `jade` jar is downloaded from their own [jar respository](https://jade.tilab.com/maven/), which contains an incorrect SSL certificate that maven consider unacceptable. Therefore, this flags allow us to bypass this check and download the jar right away the first time.

An alternative option would be installing the jade repository SSL certificates into our machine, which would be more complex.

```bash
# Run this line only the first time (as it downloads all the dependencies using maven)
mvn clean compile package -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
# Run this alternative line the rest of times
mvn clean compile package
# Run FA-DSS
java -jar target/imas-platform-1.0.0.jar -conf imas-platform.properties
```
