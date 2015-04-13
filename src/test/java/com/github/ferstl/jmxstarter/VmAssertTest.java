package com.github.ferstl.jmxstarter;

import java.util.Properties;
import org.junit.Test;


public class VmAssertTest {

  @Test
  public void assertJavaVersionOk() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "1.8");

    VmAssert.assertJavaVersion(props, 1, 8);
  }

  @Test
  public void assertJavaVersionWithFutureMajorVersion() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "2.0");

    VmAssert.assertJavaVersion(props, 1, 8);
  }

  @Test
  public void assertJavaVersionWithFutureMinorVersion() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "1.9");

    VmAssert.assertJavaVersion(props, 1, 8);
  }

  @Test(expected = IllegalStateException.class)
  public void assertJavaVersionTooOld() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "1.7");

    VmAssert.assertJavaVersion(props, 1, 8);
  }

  @Test(expected = IllegalStateException.class)
  public void assertJavaVersionUnknownFormat() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "a.b.c");

    VmAssert.assertJavaVersion(props, 1, 8);
  }
}
