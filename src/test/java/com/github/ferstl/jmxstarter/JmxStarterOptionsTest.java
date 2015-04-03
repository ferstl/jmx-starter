package com.github.ferstl.jmxstarter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.After;
import org.junit.Test;
import com.beust.jcommander.ParameterException;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class JmxStarterOptionsTest {

  private static final InputStream SYSTEM_IN_BACKUP = System.in;

  @After
  public void after() {
    System.setIn(SYSTEM_IN_BACKUP);
  }

  @Test
  public void defaults() {
    setInput("4242");

    JmxStarterOptions options = JmxStarterOptions.parse();
    assertEquals(7091, options.jmxPort);
    assertEquals(7091, options.rmiPort);
    assertEquals("4242", options.pid);
  }

  @Test
  public void nonDefaults() {
    JmxStarterOptions options = JmxStarterOptions.parse("-p", "9998", "-r", "9999", "4242");
    assertEquals(9998, options.jmxPort);
    assertEquals(9999, options.rmiPort);
    assertEquals("4242", options.pid);
  }

  @Test
  public void nonDefaultsWithLongOptions() {
    JmxStarterOptions options = JmxStarterOptions.parse("--jmx-port", "9998", "--rmi-port", "9999", "4242");
    assertEquals(9998, options.jmxPort);
    assertEquals(9999, options.rmiPort);
    assertEquals("4242", options.pid);
  }

  @Test
  public void helpShortOption() {
    JmxStarterOptions options = JmxStarterOptions.parse("-h");
    assertTrue(options.help);
    assertNull(options.pid);
  }

  @Test
  public void helpLongOption() {
    JmxStarterOptions options = JmxStarterOptions.parse("--help");
    assertTrue(options.help);
    assertNull(options.pid);
  }

  @Test
  public void helpAndOtherOptions() {
    JmxStarterOptions options = JmxStarterOptions.parse("-p", "9998", "-r", "9999", "-h", "4242");
    assertTrue(options.help);
    assertNull(options.pid);
  }

  @Test(expected = ParameterException.class)
  public void nonNumericJmxPort() {
    JmxStarterOptions.parse("-p", "notnumeric");
  }

  @Test(expected = ParameterException.class)
  public void nonNumericRmiPort() {
    JmxStarterOptions.parse("-r", "notnumeric");
  }

  @Test(expected = ParameterException.class)
  public void nonNumericRmiPid() {
    JmxStarterOptions.parse("notnumeric");
  }

  @Test(expected = ParameterException.class)
  public void multiplePids() {
    JmxStarterOptions.parse("4242", "4243");
  }

  private static void setInput(String s) {
    System.setIn(new ByteArrayInputStream(s.getBytes(defaultCharset())));
  }
}
