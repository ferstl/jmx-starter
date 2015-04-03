package com.github.ferstl.jmxstarter;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


final class VmAssert {

  private static final Pattern JAVA_SPEC_VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.*)?");
  private static final String WRONG_JAVA_VERSION_FORMAT = "Java specification version 1.8 or greater is required. You are using '%s'";
  private static final String WRONG_JVM_FORMAT = "Oracle HotSpot JVM is required. You are using '%s - %s'";
  static final String JAVA_SPEC_VERSION_PROP = "java.specification.version";
  static final String JAVA_VENDOR_PROP = "java.vendor";
  static final String JAVA_VM_NAME_PROP = "java.vm.name";

  private VmAssert() {}

  static void assertJavaVersion(Properties systemProperties) {
    String javaSpec = systemProperties.getProperty(JAVA_SPEC_VERSION_PROP, "0.0");
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
    String javaVendor = systemProperties.getProperty(JAVA_VENDOR_PROP, "unknown");
    String vmName = systemProperties.getProperty(JAVA_VM_NAME_PROP, "unknown");

    if (!vmName.toLowerCase().contains("hotspot") || !javaVendor.toLowerCase().contains("oracle")) {
      throw new IllegalStateException(String.format(WRONG_JVM_FORMAT, javaVendor, vmName));
    }
  }
}
