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
		String sudoShutdown = Constants.SUDO_CMD.concat(Constants.SHUTDOWN_CMD);
		boolean isExecCmd = sendCommands(sudoShutdown);
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
		String sudoReboot = Constants.SUDO_CMD.concat(Constants.REBOOT_CMD);
		boolean isExecCmd = sendCommands(sudoReboot);
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
		boolean isExecCmd = false;
		if (command == null || "".equalsIgnoreCase(command)) {
			return false;
		}

		SSHClient sshMan = new SSHClient();
		List<String> commands = new ArrayList<String>();
		commands.add(command);
		isExecCmd = sshMan.executeCommands(commands);
		if(isExecCmd) {
			isExecCmd = sshMan.close();	
		}
		
		return isExecCmd;
	}

}
