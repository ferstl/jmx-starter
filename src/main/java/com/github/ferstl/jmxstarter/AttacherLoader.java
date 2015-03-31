package com.github.ferstl.jmxstarter;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Consumer;


public final class AttacherLoader {

  private AttacherLoader() {}

  @SuppressWarnings("unchecked")
  public static Consumer<String> loadAttacher(Properties props) {
    // We must avoid references to LoadedWithToolsJar since will be loaded
    // with a different class loader
    // referencing it directly will result in a ClassCastException
    try (URLClassLoader classLoader = createToolsClassLoader()) {
      Class<?> clazz = Class.forName("com.github.ferstl.jmxstarter.LoadedWithToolsJar", false, classLoader);
      return (Consumer<String>) clazz.getConstructor(Properties.class).newInstance(props);
    } catch (ReflectiveOperationException | IOException e) {
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
