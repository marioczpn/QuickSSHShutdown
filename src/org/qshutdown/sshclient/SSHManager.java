package org.qshutdown.sshclient;

import java.util.ArrayList;
import java.util.List;

import org.qshutdown.utils.Constants;

/**
 * The <code>SSHManager</code> class manages SSH actions.
 * 
 * @author Mario *Razec* Ponciano - mrazec@gmail.com
 *
 */
public class SSHManager {

	/**
	 * <b>Method:</b> <code>shutdown()</code>
	 * <p>
	 * Shutdown the remote SSH's server.
	 * </p>
	 * 
	 * @return boolean (Success=true/Fail=false)
	 */
	public boolean shutdown() {
		boolean isExecCmd = sendCommands(Constants.SHUTDOWN_CMD);
		return isExecCmd;
	}

	/**
	 * <b>Method:</b> <code>reboot()</code>
	 * <p>
	 * Reboot the remote SSH's server.
	 * </p>
	 * 
	 * @return boolean (Success=true/Fail=false)
	 */
	public boolean reboot() {
		boolean isExecCmd = sendCommands(Constants.REBOOT_CMD);
		return isExecCmd;
	}

	/**
	 * <b>Method:</b> <code>sendCommands(String command)</code>
	 * <p>
	 * Execute a command directly into ssh session.
	 * </p>
	 * 
	 * @param command
	 * @return boolean (Success=true/Fail=false)
	 */
	public boolean sendCommands(String command) {

		if (command == null || "".equalsIgnoreCase(command)) {
			return false;
		}

		SSHClient sshMan = new SSHClient();
		List<String> commands = new ArrayList<String>();
		commands.add(command);
		boolean isExecCmd = sshMan.executeCommands(commands);
		if(isExecCmd) {
			isExecCmd = sshMan.close();	
		}
		
		return isExecCmd;
	}

}
