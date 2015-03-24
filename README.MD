# jmx-starter
*- Starts the JMX management agent in another JVM*

## Usage

    # Build
    mvn clean package
    
    # Run
    java -jar target/jmx-starter-1.0.0-SNAPSHOT.jar <pid>

- If `<pid>` is omitted, you'll be asked for it.
- If on Windows, you probably need to use `%JAVA_HOME%\bin\java` to get the JDK version instead of the JRE version


## Limitations
- Does only work on Oracle's HotSpot JVM 1.8+ (JVM running this program *and* the target JVM)
- JMX port is hard coded on 19874
- This program requires a JDK or Server JRE for execution

## TODO
- Make JMX properties configurable
- Check target JVM for Oracle HotSpot 1.8+
