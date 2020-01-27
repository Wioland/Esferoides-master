package interfaces;

import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import funtions.FileFuntions;
import funtions.Utils;

public class ShowAllAlgorithmImages extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowAllAlgorithmImages(String originalImage, TabPanel tp) {

		setLayout(new GridLayout(2, 0));

		String directory = originalImage.replace((new File(originalImage).getName()), "temporal");

		// para cada imagen original creamos un showimages mirando cuales de las nuevas
		// imagenes
		// creadas contienen el nombre de la imagen original sin el sufijo
		List<String> aux = new ArrayList<String>();
		JLabel originalImageLAbelName = new JLabel();

		String imageName = FileFuntions.namewithoutExtension(originalImage);
		originalImageLAbelName.setText(imageName);

		Utils.searchDirectory(imageName + ".*\\.tiff", new File(directory), aux);

		ShowImages sI = new ShowImages(tp, aux);

		this.add(originalImageLAbelName);
		this.add(sI);

	}

}
