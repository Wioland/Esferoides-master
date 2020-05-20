package interfaces;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import funtions.FileFuntions;
import funtions.PropertiesFileFuntions;

public class VersionInfo extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textVersion;
	public JScrollPane scrollPa;

	public VersionInfo() {
		// TODO Auto-generated constructor stub
	}

	public VersionInfo(Frame owner) {
		super(owner);

		textVersion = new JTextArea();
		scrollPa = new JScrollPane(textVersion);

		scrollPa.setVisible(true);
		this.getContentPane().add(scrollPa);

		this.setMinimumSize(new Dimension(500, 500));
		this.setVisible(true);

	}

	public void inicialice(String currentVer, String newVer) {

		PropertiesFileFuntions prop = new PropertiesFileFuntions();

		String urlFile = prop.getProp().getProperty("urlFileUpgrades");
		List<String> updates = FileFuntions.readFile(urlFile);
		String versTex = "The current version is " + currentVer + " a new version exist " + newVer + "."
				+ "\n  The new upgrades are:+ \n \n";

		for (String string : updates) {
			versTex += string + "\n";

		}

		textVersion.setText(versTex);
		textVersion.setEnabled(false);

		scrollPa.repaint();

		this.repaint();

		this.toFront();
		this.requestFocus();
	}

}
