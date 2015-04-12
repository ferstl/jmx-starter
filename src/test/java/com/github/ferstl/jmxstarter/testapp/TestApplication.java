package com.github.ferstl.jmxstarter.testapp;

import java.lang.management.ManagementFactory;

/**
 * Application that writes it PID to stdout and waits for 2 minutes.
 */
final class TestApplication {

  private static final int WAIT_TIME = 120_000;

  private TestApplication() {}

  public static void main(String[] args) {
    System.out.println(getPid());

    try {
      Thread.sleep(WAIT_TIME);
    } catch (InterruptedException e) {
      System.exit(1);
    }
  }

  private static String getPid() {
    String procName = ManagementFactory.getRuntimeMXBean().getName();
    return procName.substring(0, procName.indexOf('@'));
  }
}
