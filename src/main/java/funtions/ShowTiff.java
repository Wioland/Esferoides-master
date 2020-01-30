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
	public static ImageIcon showTiffToImageIcon(String path) {

		BufferedImage image = null;
		try {
			File f = new File(path);
		//	boolean s = f.exists();
			image = ImageIO.read(f);
			//String[] formatNames = ImageIO.getReaderFormatNames();

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error cwhile trying to show the tiff file", "Error saving",
					JOptionPane.ERROR_MESSAGE);
		}
		ImageIcon imaIco = new ImageIcon(image);

		return imaIco;

	}

}
