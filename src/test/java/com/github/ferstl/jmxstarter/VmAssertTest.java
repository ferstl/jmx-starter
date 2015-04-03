package com.github.ferstl.jmxstarter;

import java.util.Properties;
import org.junit.Test;


public class VmAssertTest {

  @Test
  public void assertJavaVersionOk() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "1.8");

    VmAssert.assertJavaVersion(props);
  }

  @Test
  public void assertJavaVersionWithFutureMajorVersion() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "2.0");

    VmAssert.assertJavaVersion(props);
  }

  @Test
  public void assertJavaVersionWithFutureMinorVersion() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "1.9");

    VmAssert.assertJavaVersion(props);
  }

  @Test(expected = IllegalStateException.class)
  public void assertJavaVersionTooOld() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "1.7");

    VmAssert.assertJavaVersion(props);
  }

  @Test(expected = IllegalStateException.class)
  public void assertJavaVersionUnknownFormat() {
    Properties props = new Properties();
    props.setProperty(VmAssert.JAVA_SPEC_VERSION_PROP, "a.b.c");

    VmAssert.assertJavaVersion(props);
  }

  @Test
  public void assertOracleHotspotOk() {
    Properties props = new Properties();
    props.put(VmAssert.JAVA_VENDOR_PROP, "Oracle Corporation");
    props.put(VmAssert.JAVA_VM_NAME_PROP, "Java HotSpot(TM) 64-Bit Server VM");

    VmAssert.assertOracleHotspot(props);
  }

  @Test(expected = IllegalStateException.class)
  public void assertOracleHotspotWrongVendor() {
    Properties props = new Properties();
    props.put(VmAssert.JAVA_VENDOR_PROP, "Apple Inc.");
    props.put(VmAssert.JAVA_VM_NAME_PROP, "Java HotSpot(TM) 64-Bit Server VM");

    VmAssert.assertOracleHotspot(props);
  }

  @Test(expected = IllegalStateException.class)
  public void assertOracleHotspotWrongVm() {
    Properties props = new Properties();
    props.put(VmAssert.JAVA_VENDOR_PROP, "Oracle Corporation");
    props.put(VmAssert.JAVA_VM_NAME_PROP, "OpenJDK 64-Bit Server VM");

    VmAssert.assertOracleHotspot(props);
  }
}
