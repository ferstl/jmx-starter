package com.github.ferstl.jmxstarter;

import java.util.Optional;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;


public final class JmxStarterOptions {

  @Parameter(names = {"-p", "--jmx-port"}, description = "JMX port")
  int jmxPort = 7091;

  @Parameter(names = {"-r", "--rmi-port"}, description = "RMI registry port")
  int rmiPort = this.jmxPort;


  private JmxStarterOptions() {}

  public static Optional<JmxStarterOptions> parse(String... args) {
    JmxStarterOptions options = new JmxStarterOptions();
    JCommander jcmd = new JCommander(options);

    try {
      jcmd.parse(args);
    } catch (ParameterException e) {
      StringBuilder sb = new StringBuilder(e.getMessage()).append("\n");
      jcmd.usage(sb);
      System.err.println(sb);

      return Optional.empty();
    }

    return Optional.of(options);
  }

}
