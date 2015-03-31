package com.github.ferstl.jmxstarter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import org.junit.After;
import org.junit.Test;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse();
    assertTrue(optionsOptional.isPresent());

    JmxStarterOptions options = optionsOptional.get();
    assertEquals(7091, options.jmxPort);
    assertEquals(7091, options.rmiPort);
    assertEquals("4242", options.pid);
  }

  @Test
  public void nonDefaults() {
    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse("-p", "9998", "-r", "9999", "4242");
    assertTrue(optionsOptional.isPresent());

    JmxStarterOptions options = optionsOptional.get();
    assertEquals(9998, options.jmxPort);
    assertEquals(9999, options.rmiPort);
    assertEquals("4242", options.pid);
  }

  @Test
  public void nonDefaultsWithLongOptions() {
    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse("--jmx-port", "9998", "--rmi-port", "9999", "4242");
    assertTrue(optionsOptional.isPresent());

    JmxStarterOptions options = optionsOptional.get();
    assertEquals(9998, options.jmxPort);
    assertEquals(9999, options.rmiPort);
    assertEquals("4242", options.pid);
  }

  @Test
  public void nonNumericJmxPort() {
    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse("-p", "notnumeric");
    assertFalse(optionsOptional.isPresent());
  }

  @Test
  public void nonNumericRmiPort() {
    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse("-r", "notnumeric");
    assertFalse(optionsOptional.isPresent());
  }

  @Test
  public void nonNumericRmiPid() {
    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse("notnumeric");
    assertFalse(optionsOptional.isPresent());
  }

  @Test
  public void multiplePids() {
    Optional<JmxStarterOptions> optionsOptional = JmxStarterOptions.parse("4242", "4243");
    assertFalse(optionsOptional.isPresent());
  }

  private static void setInput(String s) {
    System.setIn(new ByteArrayInputStream(s.getBytes(defaultCharset())));
  }
}
