package interfaces;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class OurProgressBar extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a progress bar
	 * 
	 * @param frameDad jframe parent to show under
	 */
	public OurProgressBar(JFrame frameDad) {
		super(frameDad);
		// setModal(true);
		setTitle("Work in progress");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setString("");
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		Border border = BorderFactory.createTitledBorder("Processing...");
		progressBar.setBorder(border);
		Container content = getContentPane();
		content.add(progressBar, BorderLayout.NORTH);
		setSize(300, 100);
		setMinimumSize(new Dimension(300, 100));
		setLocationRelativeTo(frameDad);

		setUndecorated(true);
		setVisible(true);
//		toFront();
//		setAlwaysOnTop(true);

	}

}
