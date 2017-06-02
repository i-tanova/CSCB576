package tanovai.configurator.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import rmi.IServerAdminRMI;

/**
 * Start second this class
 * @author ivana
 *
 */
public class ConfiguratorUI {

	private static SelfInternalFrame login = new SelfInternalFrame("Login");
	private static SelfInternalFrame settings = new SelfInternalFrame(
			"Settings");
	private static SelfInternalFrame serverDown = new SelfInternalFrame(
			"Server down");


	private static JButton loginB;
	private static JLabel errorLabel;
	private static JTextField name;
	private static JPasswordField password;

	private static JButton stopButton;
	private static JProgressBar stopProgressBar;
	private static JLabel stopState;

	private static JLabel startState;
	private static JProgressBar startProgressBar;
	private static JFormattedTextField socket;
	
	private static JButton pauseButton;
	private static JLabel pausedState;
	private static JProgressBar pauseProgressBar;
	
	private static JComboBox themes;

	
	private static IServerAdminRMI serverAdmin;

	public static void main(String args[]) {
		grantPolicy();
		JFrame f = new JFrame("JDesktopPane Sample");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();

		content.setLayout(new BorderLayout());
		content.add(login, BorderLayout.CENTER);
		content.add(settings, BorderLayout.CENTER);
        
		f.setSize(500, 500);
		f.setVisible(true);

	}
	
	public static void grantPolicy(){
		System.setProperty("java.rmi.server.codebase",
				ConfiguratorUI.class.getProtectionDomain()
						.getCodeSource().getLocation().toString());

		System.setProperty("java.security.policy",
				System.getProperty("user.dir")+ "/Resources/rmi.policy");

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}
	
	private static IServerAdminRMI getRMIStub() {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(Constants.rmiHOST, Constants.rmiPort);
			return (IServerAdminRMI) registry.lookup(IServerAdminRMI.SERVICE_NAME);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static JInternalFrame createLayer(String label) {
		return new SelfInternalFrame(label);
	}

	static class SelfInternalFrame extends JInternalFrame {
		public SelfInternalFrame(String s) {
			if (s.equals("Login")) {
				createLoginPanel(s);
			} else if (s.equals("Settings")) {
				createSettingsPanel(s);
			}
		}

		/*
		 * private void createServerDownPanel(String s) {
		 * System.out.println("Down created"); Panel serverDown = new Panel(new
		 * GridLayout(3,1)); serverDown.setBackground(Color.WHITE);
		 * serverDown.add(new Panel()); serverDown.add(new Panel());
		 * serverDown.add(new JLabel("Server is down") );
		 * serverDown.setSize(300, 300); Panel common = new Panel(new
		 * FlowLayout()); common.add(new Panel()); common.add(serverDown );
		 * common.add(new Panel()); getContentPane().setLayout(new
		 * GridLayout()); getContentPane().add(common); setBounds(0, 0, 500,
		 * 500); setResizable(true); setClosable(false); setMaximizable(true);
		 * setIconifiable(true); setTitle(s); setVisible(true); }
		 */
		private void createSettingsPanel(String s) {
			Panel startBPanel = createStartButtonPanel();
			Panel pauseBPanel = createPauseButtonPanel();
			Panel stopBPanel = createStopButtonPanel();

			// first row buttons panel
			Panel configPanel = new Panel(new GridLayout(3, 1)); // 3,2
			configPanel.setBackground(Color.WHITE);
			configPanel.add(startBPanel);
			configPanel.add(pauseBPanel);
			configPanel.add(stopBPanel);
			configPanel.setSize(300, 300);

			// second row panel
			Panel second = new Panel(new GridLayout(3, 1));
			second.add(new JLabel("Set socket:"));
			Panel socketTextP = new Panel(new GridLayout(3, 3));
			socket = new JFormattedTextField(createFormatter("#####"));
			socket.setEditable(true);

			socketTextP.add(new Panel());
			socketTextP.add(new Panel());
			socketTextP.add(socket);
			socketTextP.add(new Panel());
			second.add(socketTextP);

			JLabel title = new JLabel("Themes");
			second.add(title);
			second.add(new JLabel());
			themes = new JComboBox(new String[] { "error" });
			Map<String, List> themesMap;
			try {
				themesMap = serverAdmin.getThemes();
				String[] themesArr = themesMap.keySet().toArray(new String[]{});
				for(String item : themesArr){
					themes.addItem(item);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			themes.setSize(60, 60);
			second.add(themes);

			Panel third = new Panel(new GridLayout(2, 2));
			Panel label = new Panel(new GridLayout(3, 1));
			label.add(new Panel());
			label.add(new JLabel("Add item"));
			label.add(new Panel());
			third.add(label);
			third.add(new Panel());
			Panel textP = new Panel(new GridLayout(3, 1));
			textP.setBackground(Color.red);
			JTextField newItemField = new JTextField();
			newItemField.setSize(60, 60);
			textP.add(new Panel());
			textP.add(newItemField);
			textP.add(new Panel());
			third.add(textP);
			Panel buttonP = new Panel(new GridLayout(3, 3));
			buttonP.add(new Panel());
			buttonP.add(new Panel());
			buttonP.add(new Panel());
			buttonP.add(new Panel());
			JButton addTheme = new JButton("Add theme");
			buttonP.add(addTheme);
			buttonP.add(new Panel());
			buttonP.add(new Panel());
			buttonP.add(new Panel());
			third.add(buttonP);

			Panel common = new Panel(new GridLayout(3, 1));
			common.setBackground(Color.green);
			common.add(configPanel);
			common.add(second);
			common.add(third);
			
			getContentPane().setLayout(new GridLayout());
			getContentPane().add(common);
			setBounds(0, 0, 500, 500);
			setResizable(true);
			setClosable(false);
			setMaximizable(true);
			setIconifiable(true);
			setTitle(s);
			setVisible(true);
		}

		// ////////////////////////
		private Panel createStopButtonPanel() {
			Panel stopBPanel = new Panel(new GridLayout(1, 3));// 3,3
			stopButton = new JButton("Stop");
			addStopServerAction();

			Panel stopProgressPanel = new Panel(new GridLayout(3, 3));
			stopProgressBar = new JProgressBar();
			stopProgressBar.setIndeterminate(false);
			JLabel l8 = new JLabel();
			stopProgressPanel.add(l8);
			stopProgressPanel.add(stopProgressBar);
			stopState = new JLabel();
			stopProgressPanel.add(stopState);

			stopBPanel.add(stopProgressPanel);
			stopBPanel.add(stopButton);
			return stopBPanel;
		}

		// /////////////////////
		private void addStopServerAction() {
			stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread() {
						public void run() {
							stopServer();
						}

					}.run();
				}
			});

		}

		// ///////////////////////////
		private Panel createPauseButtonPanel() {
			Panel pauseBPanel = new Panel(new GridLayout(1, 3));
		    pauseButton = new JButton("Pause");
			addPauseServerAction();
			
			Panel pauseProgressPanel = new Panel(new GridLayout(3, 3));
		     pauseProgressBar = new JProgressBar();
			// pauseProgressBar.setIndeterminate(true);
			JLabel l7 = new JLabel();
			pauseProgressPanel.add(l7);
			pauseProgressPanel.add(pauseProgressBar);
			pausedState = new JLabel();
			pauseProgressPanel.add(pausedState);

			pauseBPanel.add(pauseProgressPanel);
			pauseBPanel.add(pauseButton);
			return pauseBPanel;
		}

		private void addPauseServerAction() {
			pauseButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread() {
						public void run() {
							pauseServer();
						}

						private void pauseServer() {
							try {
								getServerAdmin().pauseServer();
								setPausedState();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}

					}.run();
				}
			});
		}

		private Panel createStartButtonPanel() {
			Panel startBPanel = new Panel(new GridLayout(1, 3));
			JButton startB = new JButton("Start");

			Panel startProgressPanel = new Panel(new GridLayout(3, 3));
			startProgressBar = new JProgressBar();
			JLabel l6 = new JLabel();
			startProgressPanel.add(l6);
			startProgressPanel.add(startProgressBar);
			startState = new JLabel();
			startProgressPanel.add(startState);

			startBPanel.add(startProgressPanel);
			startBPanel.add(startB);
			setStartedState();
			startB.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
							startServer();
				}
			});
			return startBPanel;
		}

		private void createLoginPanel(String s) {
			Panel loginPanel = new Panel(new GridLayout(3, 3));
			Panel loginBPanel = createLoginButtonPanel();
			Panel nameLPanel = createNameLabelPanel();
			Panel nameTPanel = createNameTextPanel();
			Panel passwordLPanel = createPasswordLabelPanel();
			Panel passwordTPanel = createPasswordTextPanel();

			loginPanel.add(nameLPanel);
			loginPanel.add(nameTPanel);
			loginPanel.add(passwordLPanel);
			loginPanel.add(passwordTPanel);
			loginPanel.add(loginBPanel);
			loginPanel.setSize(300, 300);

			addLoginAction();

			Panel common = new Panel(new FlowLayout());
			common.add(new Panel());
			common.add(loginPanel);
			common.add(new Panel());
			getContentPane().setLayout(new GridLayout());
			getContentPane().add(common);
			setBounds(0, 0, 500, 500);
			setResizable(true);
			setClosable(false);
			setMaximizable(true);
			setIconifiable(true);
			setTitle(s);
			setVisible(true);
		}

		private void addLoginAction() {
			loginB.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean passed = false;
					char[] enteredPass = password.getPassword();
					char[] pass = Constants.admin_password.toCharArray();
					if (enteredPass.length == pass.length) {
						int j = 0;
						for (int i = 0; i < pass.length; i++) {
							if (enteredPass[i] == pass[i]) {
								j++;
							}
						}
						if (j == enteredPass.length) {
							passed = true;
						}
					}
					if (passed && name.getText().equals(Constants.admin_user)) {
						ConfiguratorUI.login.dispose();
					} else {
						errorLabel.setText("Invalid user account or password!");
						password.setText("");
					}
				}
			});
		}

		private Panel createPasswordTextPanel() {
			Panel passwordTPanel = new Panel(new GridLayout(3, 3));
			password = new JPasswordField(8);
			passwordTPanel.add(new JLabel());
			passwordTPanel.add(password);
			return passwordTPanel;
		}

		private Panel createPasswordLabelPanel() {
			Panel passwordLPanel = new Panel(new GridLayout(3, 3));
			JLabel passwordLabel = new JLabel("Password: ");
			passwordLPanel.add(new JLabel());

			passwordLPanel.add(new JLabel());
			passwordLPanel.add(passwordLabel);
			passwordLPanel.add(new JLabel());
			return passwordLPanel;
		}

		private Panel createNameTextPanel() {
			Panel nameTPanel = new Panel(new GridLayout(3, 3));
			name = new JTextField();
			nameTPanel.add(new JLabel());
			nameTPanel.add(name);
			return nameTPanel;
		}

		private Panel createNameLabelPanel() {
			Panel nameLPanel = new Panel(new GridLayout(3, 3));
			JLabel nameLabel = new JLabel("User:");
			errorLabel = new JLabel();
			nameLPanel.add(errorLabel);
			nameLPanel.add(nameLabel);
			return nameLPanel;
		}

		private Panel createLoginButtonPanel() {
			Panel loginBPanel = new Panel(new GridLayout(3, 3));
			loginB = new JButton("Login");
			JLabel l1 = new JLabel();
			loginBPanel.add(l1);
			loginBPanel.add(loginB);
			return loginBPanel;
		}
	}

	protected static MaskFormatter createFormatter(String s) {
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(s);
			formatter.setCommitsOnValidEdit(false);
			formatter.setPlaceholder(Constants.serverPort + "");
		} catch (java.text.ParseException exc) {
			System.err.println("formatter is bad: " + exc.getMessage());
			System.exit(-1);
		}
		return formatter;
	}

	public static void setStartedState() {
		boolean isStarted = true;
		try {
	
				isStarted = getServerAdmin()
						.isServerStarted();
			
		} catch (RemoteException e2) {
			e2.printStackTrace();
		}
		if (isStarted) {
			startProgressBar.setIndeterminate(true);
			startState.setText("Server is started");
		} else {
			startProgressBar.setIndeterminate(false);
			startState.setText("");
		}
	}
	
	public static void setPausedState() {
		boolean isPaused = false;
		try {
		
				isPaused = getServerAdmin().isServerPaused();				
			
		} catch (RemoteException e2) {
			e2.printStackTrace();
		}
		if (isPaused) {
			startProgressBar.setIndeterminate(false);
			startState.setText("");
			stopProgressBar.setIndeterminate(false);
			stopState.setText("");
			pauseProgressBar.setIndeterminate(true);
			pausedState.setText("Server is paused");
		} else {
			pauseProgressBar.setIndeterminate(false);
			pausedState.setText("");
		}
	}

	private static void startServer() {
			try {
				getServerAdmin().startServer(
						Integer.parseInt(socket.getText()));
				setStartedState();
				pauseProgressBar.setIndeterminate(false);
				pausedState.setText("");
				stopProgressBar.setIndeterminate(false);
				stopState.setText("");
				System.out.println("ConfiguratorUI -> server started.");
			} catch (Exception e1) {
				startState.setText("Cannot start server");
				setCannotStartServerState();
				JOptionPane.showMessageDialog(null, "Cannot start server",
						"Error", 2);
				e1.printStackTrace();
			}
			
	}

	private static void setCannotStartServerState() {
		startState.setText("Cannot start server");
		startProgressBar.setIndeterminate(false);
	}

	private static void stopServer() {
		try {
			getServerAdmin().stopServer();
			System.out
			.println("void configurator.ui.ConfiguratorUI.stopServer() - stopped");
		startProgressBar.setIndeterminate(false);
		startState.setText("");
		pauseProgressBar.setIndeterminate(false);
		pausedState.setText("");
		stopProgressBar.setIndeterminate(true);
		stopProgressBar.setValue(0);
		stopState.setText("Server is stopped");
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}

	public static boolean sendServerRequest(int request_type) {
		Socket socket = null;
		boolean isOk = false;
		try {
			socket = new Socket(Constants.HOST, Constants.serverPort);
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			OutputStream output = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
					output, "ISO-8859-1"), true);
			if (request_type == Constants.STOP_REQ) {
				String message = "<ASK_REQUEST><TYPE>" + Constants.STOP_REQ
						+ "</TYPE></ASK_REQUEST>";
				printWriter.println(message);
				String line = null;

				while ((line = reader.readLine()) != null
						&& !line.equals("BYE")) {
					System.out.println(line);
					System.out.println(request_type);
					System.out.println(line.trim().equals(request_type + ""));
					if (line != null && line.trim().equals(request_type + "")) {
						isOk = true;
					}
				}
				// printWriter.println("BYE");
			}
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// ignore
					e.printStackTrace();
				}
			}
			
		}
		return isOk;

	}
	
	
	private static IServerAdminRMI getServerAdmin(){
		if(serverAdmin == null){
			serverAdmin = getRMIStub();
		}
		if(serverAdmin == null){
			throw new IllegalStateException("Stub not found");
		}
		return serverAdmin;
	}
}
