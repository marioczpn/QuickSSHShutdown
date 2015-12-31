package org.qshutdown.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The <code>FileUtil</code> class general file manipulation utilities.
 * 
 * @author Mario *Razec* Ponciano - mrazec@gmail.com
 *
 */
public class FileUtil {
	private JPanel panel = new JPanel();

	/**
	 * <b>Method:</b>
	 * <code>savePropertyFile(String hostName, String port, String passWord)</code>
	 * <p>
	 * Creating/Saving properties in a file
	 * </p>
	 * 
	 * @param hostName
	 * @param port
	 * @param passWord
	 */
	public void savePropertyFile(String hostName, String port, String passWord) {

		try (OutputStream output = new FileOutputStream("config.properties")) {
			Properties prop = new Properties();

			/* set the properties value */
			prop.setProperty("hostname", hostName);
			prop.setProperty("port", port);
			prop.setProperty("password", this.encrypt(passWord));

			/* save properties to project root folder. */
			prop.store(output, null);

			JOptionPane.showMessageDialog(panel, "Settings were saved", "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(panel, "Could not save file", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * <b>Method:</b> <code> loadPropertyFile()</code>
	 * <p>
	 * Pulling values from a property file.
	 * </p>
	 * 
	 * @return
	 */
	public Properties loadPropertyFile() {
		File file = new File("config.properties");

		Properties prop = null;

		if (file.exists()) {
			// load a properties file
			try {
				InputStream input = new FileInputStream(file);
				prop = new Properties();
				prop.load(input);
			} catch (IOException e) {
				System.out.println("Error: " + e);
				// TODO Auto-generated catch block
			}

		}
		return prop;
	}

	/**
	 * <b>Method:</b> <code> encrypt(String input)</code>
	 * <p>
	 * Encoding byte data using the Base64 encoding scheme
	 * </p>
	 * 
	 * @param input
	 * @return String
	 */
	public String encrypt(String input) {
		byte[] encryptedWord = Base64.getEncoder().encode(input.getBytes());
		return new String(encryptedWord);
	}

	/**
	 * <b>Method:</b> <code>decrypt(String input)</code>
	 * <p>
	 * Decoding byte data using the Base64 encoding scheme.
	 * </p>
	 * 
	 * @param input
	 * @return String
	 */
	public String decrypt(String input) {
		byte[] decryptedWord = Base64.getDecoder().decode(input.getBytes());
		return new String(decryptedWord);
	}

}
