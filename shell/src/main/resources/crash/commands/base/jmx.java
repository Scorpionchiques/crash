package crash.commands.base;

import org.crsh.cli.Argument;
import org.crsh.cli.Command;
import org.crsh.cli.Named;
import org.crsh.cli.Option;
import org.crsh.cli.Usage;
import org.crsh.cli.Required;
import org.crsh.command.BaseCommand;
import org.crsh.command.InvocationContext;
import org.crsh.command.Pipe;
import org.crsh.command.ScriptException;

import javax.management.AttributeNotFoundException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** @author Julien Viet */
@Usage("Java Management Extensions")
public class jmx extends BaseCommand {

  @Usage("find mbeans")
  @Command
  public void find(
      InvocationContext<ObjectName> context,
      @Usage("the object name pattern for the query")
      @Option(names = {"p", "pattern"})
      String pattern) throws Exception {

    //
    ObjectName patternName = pattern != null ? ObjectName.getInstance(pattern) : null;
    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
    Set<ObjectInstance> instances = server.queryMBeans(patternName, null);
    for (ObjectInstance instance : instances) {
      context.provide(instance.getObjectName());
    }
  }

  @Usage("provide the mbean info of the specifie managed bean")
  @Command
  @Named("info")
  public MBeanInfo info(@Required @Argument @Usage("a managed bean object name") ObjectName mbean) {
    try {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      return server.getMBeanInfo(mbean);
    }
    catch (JMException e) {
      throw new ScriptException("Could not retrieve mbean " + mbean + "info", e);
    }
  }

  @Usage("get attributes of a managed bean")
  @Command
  public Pipe<ObjectName, Map> get(
      @Usage("specifies a managed bean attribute name")
      @Option(names = {"a","attributes"}) final List<String> attributes,
      @Usage("a managed bean object name")
      @Argument(name = "mbean") final List<ObjectName> mbeans
  ) {

    //
    return new Pipe<ObjectName, Map>() {

      /** . */
      private MBeanServer server;

      /** . */
      private List<ObjectName> buffer;

      @Override
      public void open() throws ScriptException {
        this.server = ManagementFactory.getPlatformMBeanServer();
        this.buffer = new ArrayList<ObjectName>();

        //
        if (mbeans != null) {
          buffer.addAll(mbeans);
        }
      }

      @Override
      public void provide(ObjectName name) throws IOException {
        buffer.add(name);
      }

      @Override
      public void close() throws ScriptException, IOException {

        // Determine attribute names
        String[] names;
        if (attributes == null) {
          LinkedHashSet<String> tmp = new LinkedHashSet<String>();
          for (ObjectName mbean : buffer) {
            MBeanInfo mbeanInfo;
            try {
              mbeanInfo = server.getMBeanInfo(mbean);
            }
            catch (JMException e) {
              throw new ScriptException(e);
            }
            for (MBeanAttributeInfo attributeInfo : mbeanInfo.getAttributes()) {
              if (attributeInfo.isReadable()) {
                tmp.add(attributeInfo.getName());
              }
            }
          }
          names = tmp.toArray(new String[tmp.size()]);
        } else {
          names = attributes.toArray(new String[attributes.size()]);
        }

        // Produce the output
        for (ObjectName mbean : buffer) {
          LinkedHashMap<String, Object> tuple = new LinkedHashMap<String, Object>();
          tuple.put("MBean", mbean);
          for (String name : names) {
            Object value;
            try {
              value = server.getAttribute(mbean, name);
            }
            catch (AttributeNotFoundException e) {
              value = null;
            }
            catch (JMException e) {
              throw new ScriptException(e);
            }
            tuple.put(name, value);
          }
          context.provide(tuple);
        }
      }
    };
  }
}
