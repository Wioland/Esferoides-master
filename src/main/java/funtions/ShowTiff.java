package funtions;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ShowTiff {

	/*
	 * Leer imagenes y pasarlas a imagenIcon
	 */
	/**
	 * For showing a tiff image in the interface we transform it to an imageicon
	 * since otherwise it won't appear
	 * 
	 * @param path The path of the image tiff image to show
	 * @return an imageIcon with the tiff image
	 */
	public static ImageIcon showTiffToImageIcon(String path) {

		BufferedImage image = null;
		try {
			File f = new File(path);

			image = ImageIO.read(f);
			// String[] formatNames = ImageIO.getReaderFormatNames();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error while trying to show the tiff file", "Error saving",
					JOptionPane.ERROR_MESSAGE);
		}
		ImageIcon imaIco = new ImageIcon(image);

		return imaIco;

	}

}
