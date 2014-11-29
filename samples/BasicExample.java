package com.github.maxstupo.jannoargs.examples;

import com.github.maxstupo.jannoargs.AnnoArg;
import com.github.maxstupo.jannoargs.JAnnoArgs;

/**
 * @author Maxstupo
 */
public class BasicExample {

	@AnnoArg(key = "ip")
	private static String ip;

	@AnnoArg(key = "port")
	private static int port;

	@AnnoArg(key = "user")
	private static String username;

	@AnnoArg(key = "password")
	private static String password;

	@AnnoArg(key = "user", desc = "Description about this command-line argument", hidden = false)
	private static boolean debug;

	@AnnoArg(key = "log", desc = "Description about this command-line argument")
	private static boolean log;

	public static void main(String[] args) {
		BasicExample basicExample = new BasicExample();

		// For boolean type arguments use a single hyphen, and for value type arguments (e.g. Strings) use double hyphens
		// (--) followed by a space and the value.
		String[] customCommandLineArguments = "-log --ip localhost -debug --port 8080 --user Admin --password 1234".split(" ");

		// Read arguments and set fields from a string array.
		JAnnoArgs.parse(basicExample, false, customCommandLineArguments);

		// JAnnoArgs.parse(basicExample, false, args); // Example 2

		// Display variables
		System.out.println("Ip: " + ip);
		System.out.println("Port: " + port);
		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
		System.out.println("Debug: " + debug);
		System.out.println("Log: " + log);
	}
}
