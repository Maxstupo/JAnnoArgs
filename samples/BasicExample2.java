import com.github.maxstupo.jannoargs.CmdArgument;
import com.github.maxstupo.jannoargs.JAnnoArgs;

/**
 *
 * @author Maxstupo
 */
public class BasicExample2 {

	// Use 'desc' to describe the argument for the generated help.
	@CmdArgument(key = "ip", desc = "The ip of the server.")
	private String ip;

	@CmdArgument(key = "port", desc = "The port of the server.")
	private int port;

	@CmdArgument(key = "user", desc = "The username for the login.")
	private String username;

	@CmdArgument(key = "password")
	private String password;

	// Use 'hide' to prevent the argument being listed in the generated help.
	@CmdArgument(key = "debug", hide = true)
	private boolean debug;

	@CmdArgument(key = "log", desc = "If true the program will log to file.")
	private boolean logToFile;

	@CmdArgument(key = "gui")
	private boolean guiEnabled = true;

	// Syntax for args:
	// 	For boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.
	// 	For value fields prefix the key with double hyphens -- followed by a space and the value. 	
	public static void main(String[] args) {
		BasicExample2 basicExample = new BasicExample2();

		// Parse argument array and set fields.
		JAnnoArgs.get().parseArguments(true, "A basic example of JAnnoArgs", true, basicExample, commandLineArguments);

		// Display variables
		JAnnoArgs.printCmdArgumentFields(basicExample);
	}

}
