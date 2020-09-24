package interfaces;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

/**
 * JDialog which represent a progressbar
 * 
 * @author Yolanda
 *
 */
public class OurProgressBar extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String text = "Processing ";
	private String separatorText = " \\ ";
	private String textMaxElements = "?";

	private JLabel textShow;
	private JLabel maxObject;
	private JLabel actualObject;
	private JLabel separator;
	private int actualElement = 0;
	private boolean showProgress;

	public OurProgressBar(JFrame frameDad, boolean showProgress) {
		super(frameDad);
		this.showProgress = showProgress;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frameDad.setEnabled(true);
			}

			public void windowClosed(WindowEvent e) {
				frameDad.setEnabled(true);
			}
		});

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

		if (showProgress) {
			addTextProgress();
		}

		setUndecorated(true);
		setVisible(true);
		
		frameDad.setEnabled(false);

	}

	// GETTER AND SETTER

	public JLabel getMaxObject() {
		return maxObject;
	}

	public void setMaxObject(JLabel maxObject) {
		this.maxObject = maxObject;
	}

	public JLabel getActualObject() {
		return actualObject;
	}

	public void setActualObject(JLabel actualObject) {
		this.actualObject = actualObject;
	}

	public String getTextMaxElements() {
		return textMaxElements;
	}

	public void setTextMaxElements(String textMaxElements) {
		this.textMaxElements = textMaxElements;
	}

	public void setTextMAxObject(int elements) {
		if (!this.showProgress) {
			addTextProgress();
			this.showProgress = true;
		}
		this.textMaxElements = Integer.toString(elements);
		this.maxObject.setText(textMaxElements);
		this.repaint();
	}

	// METHODS

	/**
	 * Adds one to the actual element and changes the text of the actual object
	 */
	public void changeActualElementeText() {
		actualElement++;
		this.actualObject.setText(Integer.toString(actualElement));
		this.repaint();
	}

	/**
	 * Adds the text of the progression to the progress bar
	 */
	public void addTextProgress() {
		textShow = new JLabel(text);
		this.maxObject = new JLabel(textMaxElements);
		actualObject = new JLabel(Integer.toString(actualElement));
		separator = new JLabel(separatorText);
		JPanel textPanel = new JPanel(new GridLayout(0, 4));
		textPanel.add(textShow);
		textPanel.add(actualObject);
		textPanel.add(separator);
		textPanel.add(this.maxObject);
		this.add(textPanel);
	}

}
