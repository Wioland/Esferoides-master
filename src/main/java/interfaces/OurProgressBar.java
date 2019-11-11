package interfaces;

import java.awt.BorderLayout;
import java.awt.Container;


import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class OurProgressBar extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OurProgressBar() {
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
		setVisible(true);
	}

}
