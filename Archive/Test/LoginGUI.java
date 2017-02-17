
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private int defaultPort;
	private String defaultHost;
	private JPanel loginPanel;
	private JLabel userLabel, pwLabel;
	private JTextField userInput;
	private JPasswordField pwInput;
	private JButton loginBtn;

	LoginGUI() {
		super("PPAP Chat");

		//Login Panel
		loginPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

		userLabel = new JLabel("Username ");
		userInput = new JTextField(15);
        pwLabel = new JLabel("Password ");
        pwInput = new JPasswordField(15);
		loginBtn = new JButton("Login");

		constraints.gridx = 0;
        constraints.gridy = 0;
        loginPanel.add(userLabel, constraints);

		constraints.gridx = 1;
        loginPanel.add(userInput, constraints);

		constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(pwLabel, constraints);

		constraints.gridx = 1;
        loginPanel.add(pwInput, constraints);

		constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginBtn, constraints);
		loginBtn.addActionListener(this);

		add(loginPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 200);
		setVisible(true);
		userInput.requestFocus();
	}

	//Start the Client GUI
	public static void main(String[] args) {
		new LoginGUI();
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if(o == loginBtn) {
			final String pwStr = new String (pwInput.getPassword());
			final String str = "Username: " + userInput.getText().trim() + "\nPassword: " + pwStr;
			JOptionPane.showMessageDialog(null, str);
			return;
		}
	}
}
