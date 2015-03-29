package com.github.ferstl.jmxstarter;

import java.util.Properties;
import java.util.function.Consumer;
import com.sun.tools.attach.VirtualMachine;
import static com.github.ferstl.jmxstarter.JmxStarter.assertJavaVersion;
import static com.github.ferstl.jmxstarter.JmxStarter.assertOracleHotspot;
import static com.github.ferstl.jmxstarter.JmxStarter.managementProperties;


// Must be public otherwise we have to use setAccessible(true)
public final class LoadedWithToolsJar implements Consumer<String> {

  @Override
  public void accept(String pid) {
    try {
      VirtualMachine vm = VirtualMachine.attach(pid);

      Properties targetVmProperties = vm.getSystemProperties();
      assertJavaVersion(targetVmProperties);
      assertOracleHotspot(targetVmProperties);

      vm.startManagementAgent(managementProperties());

      vm.detach();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to attach to process " + pid, e);
    }

  }

}
