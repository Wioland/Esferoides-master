package interfaces;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import funtions.FileFuntions;
import funtions.Utils;

public class ShowAllAlgorithmImages extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int labelsFontSize=14;

	public ShowAllAlgorithmImages(String originalImage, TabPanel tp) {

		// We wants to search in the temporal folder
		String directory = originalImage.replace((new File(originalImage).getName()), "temporal");

		// For each original image we create a ShowImages searching for the images that
		// contains the name of the original image without the extension
		List<String> aux = new ArrayList<String>();
		JLabel originalImageLAbelName = new JLabel();

		// For each image we create a label with it name without extension.
		// We put it in bold arial, the background in white and the label panel with a
		// black border
		String imageName = FileFuntions.namewithoutExtension(originalImage);
		originalImageLAbelName.setText(imageName);
		Font newFont = new Font("Arial", Font.BOLD, labelsFontSize);
		originalImageLAbelName.setFont(newFont);
		originalImageLAbelName.setHorizontalAlignment(JLabel.CENTER);
		originalImageLAbelName.setVerticalAlignment(JLabel.CENTER);
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		originalImageLAbelName.setBorder(border);
		originalImageLAbelName.setBackground(Color.white);
		originalImageLAbelName.setOpaque(true);

		Utils.searchDirectory(imageName + ".*\\.tiff", new File(directory), aux);

		ShowImages sI = new ShowImages(tp, aux, imageName);

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 0;
		constraints.weighty = 0;

		constraints.gridx = 0;
		constraints.gridy = 0;

		this.add(originalImageLAbelName, constraints);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;

		JScrollPane s = new JScrollPane(sI);

		this.add(s, constraints);

	}

}
