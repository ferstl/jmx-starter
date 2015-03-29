package com.github.ferstl.jmxstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class JmxStarter {

  private static final Pattern JAVA_SPEC_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.*)?");
  private static final String WRONG_JAVA_VERSION_FORMAT = "Java specification version 1.8 or greater is required. You are using '%s'";
  private static final String WRONG_JVM_FORMAT = "Oracle HotSpot JVM is required. You are using '%s - %s'";

  private JmxStarter() {}

  public static void main(String[] args) {
    init();

    String pid = getPid(args);
    try (URLClassLoader classLoader = createToolsClassLoader()) {
      Consumer<String> attacher = loadAttacher(classLoader);
      attacher.accept(pid);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to attach to process " + pid, e);
    }

  }

  private static void init() {
    Properties systemProperties = System.getProperties();

    assertJavaVersion(systemProperties);
    assertOracleHotspot(systemProperties);
  }

  static void assertJavaVersion(Properties systemProperties) {
    String javaSpec = systemProperties.getProperty("java.specification.version", "0.0");
    Matcher javaSpecMatcher = JAVA_SPEC_VERSION_PATTERN.matcher(javaSpec);
    if (!javaSpecMatcher.matches()) {
      throw new IllegalStateException(String.format(WRONG_JAVA_VERSION_FORMAT, javaSpec));
    }

    int major = Integer.parseInt(javaSpecMatcher.group(1));
    int minor = Integer.parseInt(javaSpecMatcher.group(2));
    if (major < 1 && minor < 8) {
      throw new IllegalStateException(String.format(WRONG_JAVA_VERSION_FORMAT, javaSpec));
    }
  }

  static void assertOracleHotspot(Properties systemProperties) {
    String javaVendor = systemProperties.getProperty("java.vendor", "unknown");
    String vmName = systemProperties.getProperty("java.vm.name", "unknown");

    if (!vmName.toLowerCase().contains("hotspot") || !javaVendor.toLowerCase().contains("oracle")) {
      throw new IllegalStateException(String.format(WRONG_JVM_FORMAT, javaVendor, vmName));
    }
  }

  private static String getPid(String[] args) {
    if (args.length == 0) {
      // System.console() is null on windows :-(
      System.out.println("Enter PID:");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));

      try {
        return br.readLine();
      } catch (IOException e) {
        throw new IllegalStateException("Unable to read console", e);
      }
    }

    return args[0];
  }

  // TODO: Make configurable
  static Properties managementProperties() {
    Properties props = new Properties();
    props.put("com.sun.management.jmxremote.port", "19874");
    props.put("com.sun.management.jmxremote.rmi.port", "19874");
    props.put("com.sun.management.jmxremote.authenticate", "false");
    props.put("com.sun.management.jmxremote.ssl", "false");

    return props;
  }

  // We must avoid references to LoadedWithToolsJar since will be loaded
  // with a different class loader
  // referencing it directly will result in a ClassCastException
  @SuppressWarnings("unchecked")
  private static Consumer<String> loadAttacher(ClassLoader classLoader) {
    try {
      Class<?> clazz = Class.forName("com.github.ferstl.jmxstarter.LoadedWithToolsJar", false, classLoader);
      return (Consumer<String>) clazz.getConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unable to load class", e);
    }
  }

  private static URLClassLoader createToolsClassLoader() {
    Path toolsPath = Paths.get(System.getProperty("java.home", "."), "..", "lib", "tools.jar").normalize();
    if (!Files.exists(toolsPath)) {
      throw new IllegalStateException("Path to tools.jar not found: " + toolsPath);
    }

    try {
      ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
      if (!(systemClassLoader instanceof URLClassLoader)) {
        throw new IllegalStateException("expect system class loader to be a URLClassLoader but was: " + systemClassLoader.getClass());
      }
      // We have to have the URLs of the system class loader in the new class loader
      // instead of having the system class loader as a parent.
      // If the system class loader is the parent then it will be the defining class loader
      // of LoadedWithToolsJar which means it will be used to try to load com.sun.tools.attach.VirtualMachine
      // which will fail.
      URL[] systemUrls = ((URLClassLoader) systemClassLoader).getURLs();
      int systemUrlsLength = systemUrls.length;
      URL[] urls = new URL[systemUrlsLength + 1];
      System.arraycopy(systemUrls, 0, urls, 0, systemUrlsLength);
      urls[systemUrlsLength] = toolsPath.toUri().toURL();
      // parent = null means the bootstrap class loader is the parent
      return new URLClassLoader(urls, null);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to add tools.jar to classpath", e);
    }
  }

}
