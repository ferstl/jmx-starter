package com.github.ferstl.jmxstarter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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

  @Parameter(names = {"-h", "--help"}, help = true, description = "Prints this help message")
  boolean help;

  String pid;


  private JmxStarterOptions() {}

  private void postParse() {
    if (this.pidList.size() == 1) {
      this.pid = this.pidList.get(0);
    } else if (this.pidList.size() == 0) {
      this.pid = readPidFromStdIn();
    } else {
      throw new ParameterException("Only one PID is possible");
    }

  }

  private static String readPidFromStdIn() {
    // System.console() is null on windows :-(
    System.out.println("Enter PID:");
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));

    try {
      return br.readLine();
    } catch (IOException e) {
      throw new ParameterException(e);
    }
  }

  /**
   * Create an instance of this class by parsing the given arguments. If the arguments cannot be parsed, a help message
   * will be printed to {@link System#err} an a {@link ParameterException} is thrown. If one of the arguments is
   * {@code -h} or {@code --help}, a help message will be printed to {@link System#out}.
   *
   * @param args Arguments to parse
   * @throws ParameterException in case the arguments cannot be parsed.
   */
  public static JmxStarterOptions parse(String... args) {
    JmxStarterOptions options = new JmxStarterOptions();
    JCommander jcmd = new JCommander(options);
    jcmd.setProgramName(PROGRAM_NAME);

    try {
      jcmd.parse(args);

      if (options.help) {
        jcmd.usage();
      } else {
        options.postParse();
      }
    } catch (ParameterException e) {
      StringBuilder sb = new StringBuilder(e.getMessage()).append("\n");
      jcmd.usage(sb);
      System.err.println(sb);

      throw e;
    }

    return options;
  }
}
