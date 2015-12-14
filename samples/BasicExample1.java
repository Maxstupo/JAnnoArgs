package com.github.maxstupo.jannoargs.test;

import com.github.maxstupo.jannoargs.CmdArgument;
import com.github.maxstupo.jannoargs.JAnnoArgs;

/**
 *
 * @author Maxstupo
 */
public class BasicExample1 {

	@CmdArgument(key = "ip")
	private String ip;

	@CmdArgument(key = "port")
	private int port;

	@CmdArgument(key = "user")
	private String username;

	@CmdArgument(key = "password")
	private String password;

	@CmdArgument(key = "debug")
	private boolean debug;

	@CmdArgument(key = "log")
	private boolean logToFile;

	@CmdArgument(key = "gui")
	private boolean guiEnabled = true;

	public static void main(String[] args) {
		BasicExample1 basicExample = new BasicExample1();

		// For boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.
		// For value fields prefix the key with double hyphens -- followed by a space and the value. 
		String[] commandLineArguments = "-gui +log --ip localhost +debug --port 8080 --user Admin --password 1234".split(" ");

		// Parse string array and set fields.
		JAnnoArgs.get().parseArguments(basicExample, commandLineArguments);

		// Display variables
		System.out.println("Ip: " + basicExample.ip);
		System.out.println("Port: " + basicExample.port);
		System.out.println("Username: " + basicExample.username);
		System.out.println("Password: " + basicExample.password);
		System.out.println("Debug: " + basicExample.debug);
		System.out.println("Log: " + basicExample.logToFile);
		System.out.println("GuiEnabled: " + basicExample.guiEnabled);
	}

}
