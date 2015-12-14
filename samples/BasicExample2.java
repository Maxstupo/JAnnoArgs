package com.github.maxstupo.jannoargs.test;

import com.github.maxstupo.jannoargs.CmdArgument;
import com.github.maxstupo.jannoargs.JAnnoArgs;

/**
 *
 * @author Maxstupo
 */
public class BasicExample2 {

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

	// Syntax for args:
	// 	For boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.
	// 	For value fields prefix the key with double hyphens -- followed by a space and the value. 	
	public static void main(String[] args) {
		BasicExample2 basicExample = new BasicExample2();

		// Parse argument array and set fields.
		JAnnoArgs.get().parseArguments(basicExample, args);

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