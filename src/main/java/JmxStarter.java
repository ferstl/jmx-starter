import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JmxStarter {

  public static void main(String[] args) {
    addToolsJarToClasspath();
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
