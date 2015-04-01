# jmx-starter
*- Starts the JMX management agent in another JVM*

[![Build Status](https://travis-ci.org/ferstl/jmx-starter.svg)](https://travis-ci.org/ferstl/jmx-starter)

## Usage

    # Build
    mvn clean package
    
    # Run
    java -jar target/jmx-starter.jar [options] <pid>

    Options:
    -p, --jmx-port
       JMX port
       Default: 7091
    -r, --rmi-port
       RMI registry port
       Default: 7091

- If `<pid>` is omitted, you'll be asked for it.
- If on Windows, you probably need to use `%JAVA_HOME%\bin\java` to get the JDK version instead of the JRE version


## Limitations
- Does only work on Oracle's HotSpot JVM 1.8+ (JVM running this program *and* the target JVM)
- This program requires a JDK or Server JRE for execution

## TODO
- Attach automatically if there is only one other Java process
