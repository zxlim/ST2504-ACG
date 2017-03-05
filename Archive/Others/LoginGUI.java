
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame {

	LoginGUI() {
		try {
			//Login Panel
			JPanel loginPanel = new JPanel(new GridLayout(4,2));

			JLabel userLabel = new JLabel("Username\t");
			JTextField userInput = new JTextField(15);
			JLabel pwLabel = new JLabel("Password\t");
			JPasswordField pwInput = new JPasswordField(15);
			JLabel addrLabel = new JLabel("Server Address\t");
			JTextField addrInput = new JTextField(15);
			JLabel portLabel = new JLabel("Server Port\t");
			JTextField portInput = new JTextField(15);
			loginPanel.add(userLabel);
			loginPanel.add(userInput);
			loginPanel.add(pwLabel);
			loginPanel.add(pwInput);
			loginPanel.add(addrLabel);
			loginPanel.add(addrInput);
			loginPanel.add(portLabel);
			loginPanel.add(portInput);

			int result = JOptionPane.showConfirmDialog(null, loginPanel, "PPAP Secure Chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				System.out.println("[DEBUG] " + userInput.getText() + " : " + new String(pwInput.getPassword()));
			} else {
				//
			}
		} catch (NullPointerException e) {
			//
		}
	}

	//Start the Client GUI
	public static void main(String[] args) {
		new LoginGUI();
	}
}
