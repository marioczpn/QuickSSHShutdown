package org.qshutdown.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.qshutdown.sshclient.SSHManager;
import org.qshutdown.utils.FileUtil;

/**
 * The Graphical user interface.
 * 
 * @author Mario *Razec* Ponciano - mrazec@gmail.com
 *
 */
public class SettingsWindowSwing extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnReboot;
	private JButton btnShutdown;
	private JPanel panel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private String passWord = "";
	private String hostName = "";
	private String port = "";

	private FileUtil fileUtil = new FileUtil();

	/**
	 * <b>Method:</b>
	 * <code>populateFields(JTextField jtextHostname, JTextField jtextPort, JPasswordField jpassword)</code>
	 * <p>
	 * Populate form fields if records exists in the property file.
	 * </p>
	 * 
	 * @param jtextHostname
	 * @param jtextPort
	 * @param jpassword
	 */
	public void populateFields(JTextField jtextHostname, JTextField jtextPort, JPasswordField jpassword) {

		Properties prop = fileUtil.loadPropertyFile();
		if (prop != null) {
			// get the property value and print it out
			this.hostName = prop.getProperty("hostname");
			this.port = prop.getProperty("port");
			this.passWord = prop.getProperty("password");

			if (hostName != null || !"".equals(hostName) && port != null || !"".equals(port) && passWord != null
					|| !"".equals(passWord)) {
				jtextHostname.setText(prop.getProperty("hostname"));
				jtextPort.setText(prop.getProperty("port"));
				jpassword.setText(prop.getProperty("password"));
			}
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsWindowSwing frame = new SettingsWindowSwing();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SettingsWindowSwing() {
		setTitle("Quick Shutdown");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 444, 222);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel.setBounds(6, 6, 432, 189);
		contentPane.add(panel);

		JLabel lblHostname = new JLabel("Hostname:");
		lblHostname.setBounds(20, 11, 68, 16);

		textField = new JTextField();
		textField.setToolTipText("user@hostname");
		textField.setBounds(93, 6, 196, 26);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Port:");
		lblNewLabel.setBounds(301, 11, 29, 16);

		textField_1 = new JTextField();
		textField_1.setToolTipText("22");
		textField_1.setBounds(342, 6, 78, 26);
		textField_1.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(20, 49, 78, 16);
		panel.setLayout(null);
		panel.add(lblHostname);
		panel.add(textField);
		panel.add(lblNewLabel);
		panel.add(textField_1);
		panel.add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setToolTipText("Password from server");
		passwordField.setBounds(93, 44, 327, 26);
		panel.add(passwordField);

		// populates fields
		populateFields(textField, textField_1, passwordField);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String hostName = textField.getText();
				String port = textField_1.getText();
				String password = new String(passwordField.getPassword());

				/* Saves new entry in the property file */
				fileUtil.savePropertyFile(hostName, port, password);

			}
		});
		btnSave.setBounds(305, 73, 114, 35);
		panel.add(btnSave);

		JLabel label = new JLabel("Sending Commands:");
		label.setBounds(20, 118, 129, 16);
		panel.add(label);

		btnReboot = new JButton("Reboot");
		btnReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restartServer().start();
			}
		});
		btnReboot.setBounds(19, 140, 117, 40);
		panel.add(btnReboot);

		btnShutdown = new JButton("Shutdown");
		btnShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				shutdownServer().start();

			}
		});
		btnShutdown.setBounds(303, 141, 117, 38);
		panel.add(btnShutdown);

		JSeparator separator = new JSeparator();
		separator.setBounds(34, 107, 368, 12);
		panel.add(separator);
		setLocationRelativeTo(null);
	}

	private Thread restartServer() {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				btnReboot.setText("Please Wait...");
				btnReboot.setEnabled(false);
				SSHManager ssh = new SSHManager();
				boolean isReboot = ssh.reboot();
				if (isReboot) {
					JOptionPane.showMessageDialog(panel, "Server is Rebooted!!!", "Info", JOptionPane.OK_OPTION);

				} else {
					JOptionPane.showMessageDialog(panel, "Server CANNOT restart.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}

				btnReboot.setText("Reboot");
				btnReboot.setEnabled(true);
			}

		});
		return t1;
	}

	private Thread shutdownServer() {
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				btnShutdown.setText("Please Wait...");
				btnShutdown.setEnabled(false);
				SSHManager ssh = new SSHManager();
				boolean isShutdown = ssh.shutdown();
				if (isShutdown) {
					JOptionPane.showMessageDialog(panel, "Server is OFF!!!", "Info", JOptionPane.OK_OPTION);

				} else {
					JOptionPane.showMessageDialog(panel, "Server CANNOT shutdown.", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				btnShutdown.setText("Shutdown");
				btnShutdown.setEnabled(true);
			}
		});
		return t1;
	}

}
