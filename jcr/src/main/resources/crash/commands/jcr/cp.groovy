import org.crsh.command.ScriptException;
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Usage
import org.crsh.jcr.command.Path
import org.crsh.cmdline.annotations.Required
import org.crsh.cmdline.annotations.Argument

public class cp extends org.crsh.jcr.command.JCRCommand {

  @Usage("copy a node to another")
  @Command
  @Man("""\
The cp command copies a node to a target location in the JCR tree.

[/registry]% cp foo bar""")
  public void main(
    @Required @Path @Argument @Usage("the path of the source node to copy") @Argument String source,
    @Required @Path @Argument @Usage("the path of the target node to be copied") @Argument String target) throws ScriptException {
    assertConnected();

    //
    def sourceNode = findItemByPath(source);

    //
    def targetPath = absolutePath(target);

    //
    sourceNode.session.workspace.copy(sourceNode.path, targetPath);
  }
}