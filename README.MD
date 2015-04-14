# jmx-starter
*- Starts the JMX management agent in another JVM*

[![Build Status](https://travis-ci.org/ferstl/jmx-starter.svg?branch=master)](https://travis-ci.org/ferstl/jmx-starter)

The jmx-starter needs to run with a JVM version 1.8 or higher and needs access to the tools.jar, which is part of Oracle JDK, Oracle Server JRE and OpenJDK. Running the jmx-starter with Oracle JRE will not work.

The target application needs to run with a JVM version 1.7 or higher. It does not need to have access to the tools.jar.

jmx-starter and target application were tested with the following Java runtimes:
- jmx-starter:
  - Oracle JDK 1.8
  - Oracle Server JRE 1.8
  - OpenJDK 1.8 ([Azul Zulu](http://www.azulsystems.com/products/zulu))
- Target application
  - Oracle JDK 1.8
  - Oracle Server JRE 1.8
  - Oracle JRE 1.8
  - Oracle JRE 1.7
  - OpenJDK 1.8 ([Azul Zulu](http://www.azulsystems.com/products/zulu))
  - OpenJDK 1.7 ([Azul Zulu](http://www.azulsystems.com/products/zulu))

Take a look at the [CI Build](https://travis-ci.org/ferstl/jmx-starter) to see the exact versions of the Java runtimes which were tested.

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

## TODO
- Attach automatically if there is only one other Java process
