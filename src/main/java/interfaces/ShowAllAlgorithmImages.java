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

	public ShowAllAlgorithmImages(String originalImage, TabPanel tp) {

		String directory = originalImage.replace((new File(originalImage).getName()), "temporal");

		// para cada imagen original creamos un showimages mirando cuales de las nuevas
		// imagenes
		// creadas contienen el nombre de la imagen original sin el sufijo
		List<String> aux = new ArrayList<String>();
		// JPanel labelPanel = new JPanel();
		JLabel originalImageLAbelName = new JLabel();

		String imageName = FileFuntions.namewithoutExtension(originalImage);
		originalImageLAbelName.setText(imageName);
		Font newFont = new Font("Arial", Font.BOLD, 12);
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

//		labelPanel.add(originalImageLAbelName);
//		this.add(labelPanel,constraints);
		this.add(originalImageLAbelName, constraints);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;

		JScrollPane s = new JScrollPane(sI);

		this.add(s, constraints);
		// this.add(sI,constraints);

	}

}
