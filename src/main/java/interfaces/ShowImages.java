package interfaces;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import funtions.ShowTiff;
import funtions.Utils;

public class ShowImages extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> listImages;
	private List<JButton> listImagesPrev;
	private List<ImageIcon> listIm;
	private String dir;

	/*
	 * Mostrar las imagenes tiff como botones
	 */
	public ShowImages(String directory) {
		this.dir = directory;
		this.setLayout(new GridLayout(0, 4));

		listImages = new ArrayList<String>();
		listIm = new ArrayList<ImageIcon>();
		listImagesPrev = new ArrayList<JButton>();
		File folder = new File(dir);

		Utils.search(".*\\.tiff", folder, listImages);
		Collections.sort(listImages);

		for (String name : listImages) {
			// convertir a formato que se pueda ver
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);
			image.setDescription(name);
			listIm.add(image);
			// aniadir a button
			// Obtiene un icono en escala con las dimensiones especificadas
			ImageIcon iconoEscala = new ImageIcon(
					image.getImage().getScaledInstance(200, 200, java.awt.Image.SCALE_DEFAULT));
			JButton button = new JButton(iconoEscala);

			button.addActionListener(new ActionListener() {
				// si se genera el click que muestre un visualizador de imagenes
				public void actionPerformed(ActionEvent e) {
					JButton b = (JButton) e.getSource();
					ViewImagesBigger viewImageBig = new ViewImagesBigger(image, listIm, dir,false);
				}
			});

			listImagesPrev.add(button);

			this.add(button);
		}
		

	}

}
