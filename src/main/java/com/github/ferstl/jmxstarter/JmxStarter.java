package com.github.ferstl.jmxstarter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class JmxStarter {

  private static final String WRONG_JAVA_VERSION_FORMAT = "Java specification version 1.8 or greater is required. You are using '%s'";
  private static final String WRONG_VM_FORMAT = "Oracle HotSpot VM is required. You are using '%s - %s'";

  private JmxStarter() {}

  public static void main(String[] args) {
    assertJavaVersion();
    assertOracleHotspot();
    addToolsJarToClasspath();
  }

  private static void assertJavaVersion() {
    String javaSpec = System.getProperty("java.specification.version", "0.0");
    String[] versionParts = javaSpec.split("(\\d+)\\.(\\d+)(\\.*)?");

    if (versionParts.length < 2) {
      throw new IllegalStateException(String.format(WRONG_JAVA_VERSION_FORMAT, javaSpec));
    }

    int major = Integer.valueOf(versionParts[0]);
    int minor = Integer.valueOf(versionParts[1]);
    if (major < 1 && minor < 8) {
      throw new IllegalStateException(String.format(WRONG_JAVA_VERSION_FORMAT, javaSpec));
    }
  }

  private static void assertOracleHotspot() {
    String javaVendor = System.getProperty("java.vendor", "unknown");
    String vmName = System.getProperty("java.vm.name", "unknown");

    if (!vmName.toLowerCase().contains("hotspot") || !javaVendor.toLowerCase().contains("oracle")) {
      throw new IllegalStateException(String.format(WRONG_VM_FORMAT, javaVendor, vmName));
    }
  }

  private static void addToolsJarToClasspath() {
    Path toolsPath = Paths.get(System.getProperty("java.home", "."), "..", "lib", "tools.jar");
    if (!Files.exists(toolsPath)) {
      throw new IllegalStateException("Path to tools.jar not found: " + toolsPath);
    }

    try {
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
      method.setAccessible(true);
      method.invoke(classLoader, new Object[]{toolsPath.toUri().toURL()});
    } catch (Exception e) {
      throw new IllegalStateException("Unable to add tools.jar to classpath", e);
    }
  }
}
