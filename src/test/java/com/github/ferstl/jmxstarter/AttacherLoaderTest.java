package com.github.ferstl.jmxstarter;

import java.util.Properties;
import java.util.function.Consumer;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;


public class AttacherLoaderTest {

  @Test
  public void loadAttacher() {
    Consumer<String> attacher = AttacherLoader.loadAttacher(new Properties());
    assertNotNull(attacher);
  }

}
