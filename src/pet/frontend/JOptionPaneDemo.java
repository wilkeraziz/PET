/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pet.frontend;

import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

public class JOptionPaneDemo extends JFrame implements ActionListener {

	Container contentPane = null;
	private JButton jbnDialog;
	String ButtonLabels;
	private JRadioButton[] dialogTypeButtons;
	private JRadioButton[] messageTypeButtons;
	private int[] messageTypes = { JOptionPane.PLAIN_MESSAGE,
			JOptionPane.INFORMATION_MESSAGE, JOptionPane.QUESTION_MESSAGE,
			JOptionPane.WARNING_MESSAGE, JOptionPane.ERROR_MESSAGE };
	private ButtonGroup messageTypeButtonGroup, buttonTypeButtonGroup,
			dialogTypeButtonGroup;
	private JRadioButton[] optionTypeButtons;
	private int[] OptionTypes = { JOptionPane.DEFAULT_OPTION,
			JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.OK_CANCEL_OPTION };
	public static void main(String[] args) {
		new JOptionPaneDemo();
	}
	public JOptionPaneDemo() {
		super("JOptionPane Source Demo");
		addWindowListener(new WindowListener());
		contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(0, 1));
		JPanel jplButton = new JPanel();
		jbnDialog = new JButton("Show an Option Pane");
		jbnDialog.addActionListener(this);
		jplButton.add(jbnDialog);
		contentPane.add(jplButton);
		createRadioButtonGroupings();
		ButtonLabels = "Button1 Button2 Button3";
		pack();
		setVisible(true);
	}
	public void createRadioButtonGroupings() {
		JPanel jplDialogType = new JPanel();
		dialogTypeButtonGroup = new ButtonGroup();
		dialogTypeButtons = new JRadioButton[] {
				new JRadioButton("Show Message", true),
				new JRadioButton("Get Confirmation"),
				new JRadioButton("Collect Input"),
				new JRadioButton("Present Options") };
		for (int i = 0; i < dialogTypeButtons.length; i++) {
			dialogTypeButtonGroup.add(dialogTypeButtons[i]);
			jplDialogType.add(dialogTypeButtons[i]);
		}
		contentPane.add(jplDialogType);
		JPanel jplMessageType = new JPanel();
		messageTypeButtonGroup = new ButtonGroup();
		messageTypeButtons = new JRadioButton[] {
				new JRadioButton("Plain"),
				new JRadioButton("Information", true),
				new JRadioButton("Question"), new JRadioButton("Warning"),
				new JRadioButton("Error") };
		for (int i = 0; i < messageTypeButtons.length; i++) {
			messageTypeButtonGroup.add(messageTypeButtons[i]);
			jplMessageType.add(messageTypeButtons[i]);
		}
		contentPane.add(jplMessageType);
		JPanel jplButtonType = new JPanel();
		buttonTypeButtonGroup = new ButtonGroup();
		optionTypeButtons = new JRadioButton[] {
				new JRadioButton("Default", true),
				new JRadioButton("Yes/No"),
				new JRadioButton("Yes/No/Cancel"),
				new JRadioButton("OK/Cancel") };
		for (int i = 0; i < optionTypeButtons.length; i++) {
			buttonTypeButtonGroup.add(optionTypeButtons[i]);
			jplButtonType.add(optionTypeButtons[i]);
		}
		contentPane.add(jplButtonType);
	}
	// Windows Listener for Window Closing
	public class WindowListener extends WindowAdapter {

		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}
	public void actionPerformed(ActionEvent event) {
		/*
		 * dialogTypeButtons =
		 *
		 * new JRadioButton[] { new JRadioButton("Show Message", true),
		 *
		 * new JRadioButton("Get Confirmation"),
		 *
		 * new JRadioButton("Collect Input"),
		 *
		 * new JRadioButton("Present Options") };
		 */
		if (dialogTypeButtons[0].isSelected()) {
			JOptionPane.showMessageDialog(this, "Show Message",
					"Simple Dialog", getMessageType());
		} else if (dialogTypeButtons[1].isSelected()) {
			JOptionPane.showConfirmDialog(this, "Get Confirmation",
					"Simple Dialog", getButtonType(), getMessageType());
		} else if (dialogTypeButtons[2].isSelected()) {
			JOptionPane.showInputDialog(this, "Collect Input",
					"Simple Dialog", getMessageType(), null, null, null);
		} else if (dialogTypeButtons[3].isSelected()) {
			JOptionPane.showOptionDialog(this, "Present Options",
					"Simple Dialog", getButtonType(), getMessageType(),
					null, substrings(ButtonLabels), null);
		}
	}
	private int getAssociatedType(AbstractButton[] buttons, int[] types) {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].isSelected()) {
				return (types[i]);
			}
		}
		return (types[0]);
	}
	private int getMessageType() {
		return (getAssociatedType(messageTypeButtons, messageTypes));
	}
	private int getButtonType() {
		return (getAssociatedType(optionTypeButtons, OptionTypes));
	}
	private String[] substrings(String string) {
		StringTokenizer tok = new StringTokenizer(string);
		String[] substrings = new String[tok.countTokens()];
		for (int i = 0; i < substrings.length; i++)
			substrings[i] = tok.nextToken();
		return (substrings);
	}
}