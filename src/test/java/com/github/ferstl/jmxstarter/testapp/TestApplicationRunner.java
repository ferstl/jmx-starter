package com.github.ferstl.jmxstarter.testapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * Prepares and runs the {@link TestApplication}.
 * <p>
 * During preparation the test application's class file is re-written to a temporary directory with byte code version
 * {@code 0x32} (Java 5) This allows older JVMs run the test application.
 * </p>
 * <p>
 * If the environment variable {@code TESTAPP_JAVA_HOME} is set the test application will be executed with the JVM in
 * {@code $TESTAPP_JAVA_HOME/bin}. Otherwise the test application is executed with the JVM defined in the system
 * property {@code java.home}.
 * </p>
 */
public final class TestApplicationRunner {

  private TestApplicationRunner() {}

  public static Process run() {
    Path classpath = prepareTestApplication();
    return startJavaProcess(classpath.toString(), TestApplication.class);
  }

  private static Path prepareTestApplication() {
    try {
      Path tempDir = Files.createTempDirectory(Paths.get("target"), "testapp");
      ClassReader classReader = new ClassReader(TestApplication.class.getName());
      ClassVersionRewriter versionRewriter = new ClassVersionRewriter();
      classReader.accept(versionRewriter, 0);
      byte[] java5Class = versionRewriter.toByteArray();
      String[] packages = TestApplication.class.getPackage().getName().split("\\.");
      Path path = tempDir;
      for (String p : packages) {
        path = path.resolve(p);
      }
      Files.createDirectories(path);

      path = path.resolve(TestApplication.class.getSimpleName() + ".class");

      Files.write(path, java5Class, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

      return tempDir;
    } catch (IOException e) {
      throw new IllegalStateException("fuck", e);
    }
  }

  private static Process startJavaProcess(String classpath, Class<?> mainClass, String... args) {
    String[] finalArgs = new String[args.length + 4];
    System.arraycopy(args, 0, finalArgs, 4, args.length);
    finalArgs[0] = getJavaCommand();
    finalArgs[1] = "-cp";
    finalArgs[2] = classpath;
    finalArgs[3] = mainClass.getName();

    try {
      System.out.println("Starting process" + Arrays.toString(finalArgs));
      return new ProcessBuilder(finalArgs).start();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to start process: " + Arrays.toString(finalArgs), e);
    }
  }

  private static String getJavaCommand() {
    String javaHome = System.getenv("TESTAPP_JAVA_HOME");
    if (javaHome == null) {
      javaHome = System.getProperty("java.home");
    }

    boolean isWindows = System.getProperty("os.name", "unknown").toLowerCase().contains("windows");
    String javaExecutable = isWindows ? "java.exe" : "java";

    return Paths.get(javaHome, "bin", javaExecutable).normalize().toString();
  }

  private static class ClassVersionRewriter extends ClassVisitor {

    private static final int JAVA_5_BYTECODE_VERSION = 0x31;

    public ClassVersionRewriter() {
      super(Opcodes.ASM5, new ClassWriter(0));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      super.visit(JAVA_5_BYTECODE_VERSION, access, name, signature, superName, interfaces);
    }

    public byte[] toByteArray() {
      return ((ClassWriter) this.cv).toByteArray();
    }
  }
}
