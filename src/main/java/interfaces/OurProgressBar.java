package interfaces;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

public class OurProgressBar extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String text = "Processing ";
	private String separatorText = " \\ ";
	private String textMaxElements="?";

	private JLabel textShow;
	private JLabel maxObject;
	private JLabel actualObject;
	private JLabel separator;
	private int actualElement=0;
	private boolean showProgress;


	public OurProgressBar(JFrame frameDad, boolean showProgress) {
		super(frameDad);
		this.showProgress=showProgress;
		
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
		
		if(showProgress) {
			addTextProgress();
		}
		

		setUndecorated(true);
		setVisible(true);

	}
	
	//GETTER AND SETTER

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
	
	//METHODS
	
	public void changeActualElementeText() {
		actualElement++;
		this.actualObject.setText(Integer.toString(actualElement));
		this.repaint();
	}

	public void setTextMAxObject(int elements) {
		if(!this.showProgress) {
			addTextProgress();
			this.showProgress=true;
		}
		this.textMaxElements=Integer.toString(elements);
		this.maxObject.setText(textMaxElements);
		this.repaint();
	}

	public void addTextProgress() {
		textShow = new JLabel(text);
		this.maxObject = new JLabel(textMaxElements);
		actualObject = new JLabel(Integer.toString(actualElement));
		separator = new JLabel(separatorText);
		JPanel textPanel= new JPanel(new GridLayout(0,4));
		textPanel.add(textShow);
		textPanel.add(actualObject);
		textPanel.add(separator);
		textPanel.add(this.maxObject);
		this.add(textPanel);
	}
	

}
