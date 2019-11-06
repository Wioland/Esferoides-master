package funtions;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ShowTiff {

	/*
	 * Leer imagenes y pasarlas a imagenIcon
	 */
	public static ImageIcon showTiffToImageIcon(String path) {

		BufferedImage image = null;
		try {
			File f = new File(path);
			image = ImageIO.read(f);
			String[] formatNames = ImageIO.getReaderFormatNames();

		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageIcon imaIco = new ImageIcon(image);

		return imaIco;

	}

}
