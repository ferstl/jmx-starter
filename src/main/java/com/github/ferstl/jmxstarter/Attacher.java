package com.github.ferstl.jmxstarter;

import java.util.Properties;
import java.util.function.Consumer;
import com.sun.tools.attach.VirtualMachine;
import static com.github.ferstl.jmxstarter.VmAssert.assertOracleHotspot;


// Must be public otherwise we have to use setAccessible(true)
public final class Attacher implements Consumer<String> {

  private final Properties managementProperties;

  public Attacher(Properties managementProperties) {
    this.managementProperties = managementProperties;
  }

  @Override
  public void accept(String pid) {
    try {
      VirtualMachine vm = VirtualMachine.attach(pid);

      Properties targetVmProperties = vm.getSystemProperties();
      // TODO: Probably check for a specific version which does not work anymore.
      // assertJavaVersion(targetVmProperties);
      assertOracleHotspot(targetVmProperties);

      vm.startManagementAgent(this.managementProperties);

      vm.detach();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to attach to process " + pid, e);
    }

  }

}
