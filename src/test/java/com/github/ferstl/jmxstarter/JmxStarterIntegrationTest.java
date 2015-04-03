package com.github.ferstl.jmxstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JmxStarterIntegrationTest {

  private static final String JAVA_COMMAND = getJavaCommand();
  private static final String TEST_APP_CLASSPATH = "target/test-classes";

  @Test
  public void startManagementAgent() {
    Process testApp = startJavaProcess(TEST_APP_CLASSPATH, TestApplication.class);
    String testAppPid = readPid(testApp);

    // Start the management agent in the test application
    JmxStarter.main(new String[]{testAppPid});

    // Try to connect to the application
    verifyManagementAgent();

    try {
      testApp.destroy();
      testApp.waitFor();
    } catch (InterruptedException e) {
      fail("interrupted");
    }
  }

  private void verifyManagementAgent() {
    JMXServiceURL jmxServiceUrl = createJmxServiceUrl();
    ObjectName runtimeMxBeanObjectName = createRuntimeMXBeanObjectName();

    try (JMXConnector jmxc = JMXConnectorFactory.connect(jmxServiceUrl)) {
      MBeanServerConnection connection = jmxc.getMBeanServerConnection();
      RuntimeMXBean proxy = JMX.newMXBeanProxy(connection, runtimeMxBeanObjectName, RuntimeMXBean.class);
      assertNotNull(proxy.getName());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to connect to test process");
    }
  }

  private ObjectName createRuntimeMXBeanObjectName() {
    try {
      return new ObjectName("java.lang:type=Runtime");
    } catch (MalformedObjectNameException e) {
      throw new IllegalArgumentException("Unable to create object name for runtime MXBean", e);
    }
  }

  private JMXServiceURL createJmxServiceUrl() {
    try {
      return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:7091/jmxrmi");
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Cannot create JMX service URL", e);
    }
  }

  private static Process startJavaProcess(String classpath, Class<?> mainClass, String... args) {
    String[] finalArgs = new String[args.length + 4];
    System.arraycopy(args, 0, finalArgs, 4, args.length);
    finalArgs[0] = JAVA_COMMAND;
    finalArgs[1] = "-cp";
    finalArgs[2] = classpath;
    finalArgs[3] = mainClass.getName();

    try {
      return new ProcessBuilder(finalArgs).start();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to start process: " + Arrays.toString(finalArgs), e);
    }
  }

  private static String getJavaCommand() {
    boolean isWindows = System.getProperty("os.name", "unknown").toLowerCase().contains("windows");
    String javaHome = System.getProperty("java.home");
    String javaExecutable = isWindows ? "java.exe" : "java";

    return Paths.get(javaHome, "bin", javaExecutable).normalize().toString();
  }

  private static String readPid(Process process) {
    BufferedReader pidReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    try {
      return pidReader.readLine();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read PID from process.", e);
    }
  }
}
