package com.github.ferstl.jmxstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static org.junit.Assert.fail;


public class JmxStarterIntegrationTest {

  private static final String JAVA_COMMAND = getJavaCommand();
  private static final String TEST_APP_CLASSPATH = "target/test-classes";
  private static final String TEST_CLASSPATH = getClassPath();

  @Test
  public void test() {
    Process testApp = startJavaProcess(TEST_APP_CLASSPATH, TestApplication.class);
    String testAppPid = readPid(testApp);

    Process attacher = startJavaProcess(TEST_CLASSPATH, JmxStarter.class, testAppPid);
    try {
      attacher.waitFor(2, TimeUnit.SECONDS);
      testApp.destroy();
      testApp.waitFor();
    } catch (InterruptedException e) {
      fail("interrupted");
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

  private static String getClassPath() {
    return System.getProperty("java.class.path", ".");
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
