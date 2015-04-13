package com.github.ferstl.jmxstarter;

import java.util.Properties;
import java.util.function.Consumer;
import com.beust.jcommander.ParameterException;
import static com.github.ferstl.jmxstarter.VmAssert.assertJavaVersion;


public final class JmxStarter {

  private JmxStarter() {}

  public static void main(String[] args) {
    try {
      JmxStarterOptions options = init(args);
      if (options.help) {
        return;
      }

      Consumer<String> attacher = AttacherLoader.loadAttacher(options);
      attacher.accept(options.pid);
    } catch (ParameterException e) {
      System.exit(1);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static JmxStarterOptions init(String[] args) {
    Properties systemProperties = System.getProperties();
    assertJavaVersion(systemProperties, 1, 8);

    return JmxStarterOptions.parse(args);
  }
}
