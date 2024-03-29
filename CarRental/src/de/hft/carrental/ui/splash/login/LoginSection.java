package de.hft.carrental.ui.splash.login;

import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.hft.carrental.database.SessionManager;
import de.hft.carrental.domain.Customer;
import de.hft.carrental.ui.WindowPageSection;
import de.hft.carrental.ui.main.MainWindow;

/**
 * This section contains a text field allowing the user to type in his login
 * name. It also shows a button that allows the user to eventually perform the
 * login.
 * 
 * @author Alexander Weickmann
 */
public final class LoginSection extends WindowPageSection implements
		ActionListener {

	private static final long serialVersionUID = 1064521119735654437L;

	private static final String TITLE = "Login";

	/** Action command that is triggered upon activation of the login button. */
	private static final String AC_LOGIN = "login";

	private JTextField loginTextField;

	/**
	 * @param loginPage
	 *            The {@link LoginPage} this section belongs to.
	 */
	public LoginSection(LoginPage loginPage) {
		super(loginPage, TITLE);
		createContents();
	}

	private void createContents() {
		loginTextField = new JTextField(18);
		loginTextField.requestFocusInWindow();
		loginTextField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Nothing to do.
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					performLogin();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// Nothing to do.
			}
		});
		add(loginTextField);

		JButton loginButton = new JButton("Login!");
		loginButton.setActionCommand(AC_LOGIN);
		loginButton.addActionListener(this);
		loginButton.setIcon(new ImageIcon("images/login.png"));
		add(loginButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(AC_LOGIN)) {
			performLogin();
		}
	}

	/**
	 * Performs the login. Shows the main window on success and displays an
	 * appropriate error message on failure.
	 */
	private void performLogin() {
		Customer user = null;
		String username = loginTextField.getText();

		Session session = SessionManager.getInstance().openSession();
		if (session == null) {
			showErrorDialog("No connection to database.");
			return;
		}

		Transaction tr = session.beginTransaction();

		String query = "from Customer where loginName = '" + username + "'";
		Object result = session.createQuery(query).uniqueResult();

		if (result == null) {
			showErrorDialog("Username not found.");
			return;
		}
		user = (Customer) result;

		tr.commit();

		getWindowPage().getWindow().setVisible(false);
		new MainWindow(user);
	}

	@Override
	protected void refresh() {
		// Nothing to do.
	}

	private void showErrorDialog(String errorText) {
		final JDialog errorDialog = new JDialog();
		errorDialog.setTitle("Error");

		MigLayout layout = new MigLayout("", "[center]", "[center]");

		errorDialog.setLayout(layout);
		errorDialog.setModalityType(ModalityType.APPLICATION_MODAL);

		JLabel errorLabel = new JLabel(errorText);
		errorDialog.add(errorLabel, "spanx, wrap");

		final JButton closeButton = new JButton("Ok");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				errorDialog.setVisible(false);
			}
		});
		errorDialog.add(closeButton);

		errorDialog.pack();
		errorDialog.setResizable(false);
		int posX = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
		int posY = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
		errorDialog.setLocation(posX, posY);

		errorDialog.setVisible(true);

		loginTextField.setText("");
		loginTextField.requestFocus();
	}

}
