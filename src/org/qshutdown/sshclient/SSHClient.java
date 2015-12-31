package org.qshutdown.sshclient;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import org.qshutdown.utils.FileUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * The <code>SSHClient</code> class manages SSH actions.
 * <p>
 * A session class representing the connection service running on top of the SSH
 * transport layer.
 * </p>
 * 
 * @author Mario *Razec* Ponciano - mrazec@gmail.com
 *
 */
public class SSHClient {

	private Session session;
	private ChannelShell channel;
	private String username = "";
	private String password = "";
	private String hostname = "";
	private int port;

	public SSHClient() {
		init();
	}

	/**
	 * <b>Method:</b> <code>init()</code>
	 * <p>
	 * It will start all global variables.
	 * </p>
	 */
	private void init() {
		FileUtil fileUtil = new FileUtil();
		Properties prop = fileUtil.loadPropertyFile();
	

		if(prop != null){
			// set the user
			this.username = prop.getProperty("hostname").substring(0, prop.getProperty("hostname").indexOf('@'));
	
			// get the property value and print it out
			this.hostname = prop.getProperty("hostname").substring(prop.getProperty("hostname").indexOf('@') + 1);
			this.port = Integer.valueOf(prop.getProperty("port"));
			this.password = fileUtil.decrypt(prop.getProperty("password"));
		}
	}

	/**
	 * <b>Method:</b> <code>getSession()</code>
	 * <p>
	 * Returns a Session, it represents a connection to a SSH server.
	 * </p>
	 * 
	 * @return session
	 */
	private Session getSession() {
		if (session == null || !session.isConnected()) {
			session = connect(hostname, username, this.password, port);
		}
		return session;
	}

	/**
	 * <b>Method:</b> <code>Method: getCHannel()</code>
	 * <p>
	 * Returns a new channel of some type over the connection.
	 * </p>
	 * 
	 * @return channel
	 */
	private Channel getChannel() {
		if (channel == null || !channel.isConnected()) {
			try {
				boolean isSessionConnected = getSession().isConnected();
				if (isSessionConnected) {
					channel = (ChannelShell) getSession().openChannel("shell");
					channel.connect();
				}

			} catch (Exception e) {
				System.out.println("Error while opening channel: " + e);
			}
		}
		return channel;
	}

	/**
	 * <b>Method:</b>
	 * <code>connect(String hostname, String username, String password, int port)</code>
	 * <p>
	 * Opens the SSH's connection.
	 * </p>
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 * @param port
	 * 
	 * @return session
	 */
	public Session connect(String hostname, String username, String password, int port) {

		JSch jSch = new JSch();

		try {

			session = jSch.getSession(username, hostname, port);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);

			System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
			session.connect();
			System.out.println("Connected!");
		} catch (Exception e) {
			System.out.println("An error occurred while connecting to " + hostname + ": " + e);
		}

		return session;

	}

	/**
	 * <b>Method:</b> <code>executeCommands(List<String> commands)</code>
	 * <p>
	 * Send commands to SSH and read the output.
	 * </p>
	 * 
	 * @param commands
	 * @return boolean (Success=true/Fail=false)
	 */
	public boolean executeCommands(List<String> commands) {
		boolean isExecCmd = false;

		try {
			Channel channel = getChannel();
			boolean isChannelConnected = channel.isConnected();

			if (isChannelConnected) {
				System.out.println("Sending commands...");
				sendCommands(channel, commands);

				readChannelOutput(channel);
				System.out.println("Finished sending commands!");

				/* Succes */
				isExecCmd = true;
			} else {
				/* Fail: Channel cannot connect */
				isExecCmd = false;
			}

		} catch (Exception e) {
			System.out.println("An error ocurred during executeCommands: " + e);

			/* Fail flag */
			isExecCmd = false;
		}
		return isExecCmd;
	}

	/**
	 * <b>Method:</b>
	 * <code>sendCommands(Channel channel, List<String> commands)</code>
	 * <p>
	 * Send commands to SSH's session.
	 * </p>
	 * 
	 * @param channel
	 * @param commands
	 */
	public void sendCommands(Channel channel, List<String> commands) {

		try {
			PrintStream out = new PrintStream(channel.getOutputStream());

			out.println("#!/bin/bash");
			for (String command : commands) {
				out.println(command);
			}
			out.println("exit");

			out.flush();
		} catch (Exception e) {
			System.out.println("Error while sending commands: " + e);
		}

	}

	/**
	 * <b>Method:</b> <code>readChannelOutput(Channel channel)</code>
	 * <p>
	 * Read the channel output of SSH's session.
	 * </p>
	 * 
	 * @param channel
	 */
	private static void readChannelOutput(Channel channel) {

		byte[] buffer = new byte[1024];

		try {
			InputStream in = channel.getInputStream();
			String line = "";
			while (true) {
				while (in.available() > 0) {
					int i = in.read(buffer, 0, 1024);
					if (i < 0) {
						break;
					}
					line = new String(buffer, 0, i);
					System.out.println(line);
				}

				if (line.contains("logout")) {
					break;
				}

				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
		} catch (Exception e) {
			System.out.println("Error while reading channel output: " + e);
		}

	}

	/**
	 * <b>Method:</b> <code>close()</code>
	 * <p>
	 * Closes the connection to the server. If this session is not connected,
	 * this method is a no-op.
	 * </p>
	 */
	public boolean close() {
		boolean isClosed = false;
		if (channel != null && channel.isConnected() && session.isConnected()) {
			channel.disconnect();
			session.disconnect();
			isClosed = true;
		}
		System.out.println("Disconnected channel and session");

		return isClosed;
	}

}