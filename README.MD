# jmx-starter
*- Starts the JMX management agent in another JVM*

#Usage

```
    java -jar path/to/jarfile.jar <pid>
```

If `<pid>` is omitted, you'll be asked for it.


# Limitations
- Does only work on Oracle's HotSpot JVM 1.8+
- JMX port is hard coded on 19874
- Requires JDK or Server JRE for execution