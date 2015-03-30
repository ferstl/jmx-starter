package com.github.ferstl.jmxstarter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;


public final class JmxStarterOptions {

  private static final String PROGRAM_NAME = "java -jar jmx-starter.jar";

  // Main parameter has to be a list.
  @Parameter(validateWith = PositiveInteger.class, description = "<pidList>")
  private final List<String> pidList = new ArrayList<>();

  @Parameter(names = {"-p", "--jmx-port"}, description = "JMX port")
  int jmxPort = 7091;

  @Parameter(names = {"-r", "--rmi-port"}, description = "RMI registry port")
  int rmiPort = this.jmxPort;

  String pid;


  private JmxStarterOptions() {}

  private void postParse() {
    if (this.pidList.size() == 1) {
      this.pid = this.pidList.get(0);
    } else if (this.pidList.size() > 1) {
      throw new ParameterException("Only one PID is possible");
    }

  }

  public static Optional<JmxStarterOptions> parse(String... args) {
    JmxStarterOptions options = new JmxStarterOptions();
    JCommander jcmd = new JCommander(options);
    jcmd.setProgramName(PROGRAM_NAME);

    try {
      jcmd.parse(args);
      options.postParse();
    } catch (ParameterException e) {
      StringBuilder sb = new StringBuilder(e.getMessage()).append("\n");
      jcmd.usage(sb);
      System.err.println(sb);

      return Optional.empty();
    }

    return Optional.of(options);
  }

  public static void main(String[] args) {
    parse("-f");
  }
}
