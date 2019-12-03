package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<JButton, ImageIcon> listImagesPrev;
	private String dir;

	/*
	 * Mostrar las imagenes tiff como botones
	 */
	public ShowImages(String directory, Component tp) {
		this.dir = directory;
		this.setLayout(new GridLayout(0, 1));

		listImages = new ArrayList<String>();
		listImagesPrev = new HashMap<JButton, ImageIcon>();

		File folder = new File(dir);
		createImageButton(folder, tp);

	}
	
	
	public Map<JButton, ImageIcon> getListImagesPrev() {
		return listImagesPrev;
	}



	public void setListImagesPrev(Map<JButton, ImageIcon> listImagesPrev) {
		this.listImagesPrev = listImagesPrev;
	}


	public void createImageButton(File folder, Component tp) {

		Utils.search(".*\\.tiff", folder, listImages);
		Collections.sort(listImages);

		for (String name : listImages) {
			// convertir a formato que se pueda ver
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);
			image.setDescription(name);

			// aniadir a button
			// Obtiene un icono en escala con las dimensiones especificadas
			ImageIcon iconoEscala = new ImageIcon(
					image.getImage().getScaledInstance(700, 700, java.awt.Image.SCALE_DEFAULT));
			JButton imageView = new JButton(iconoEscala);
			imageView.setIcon(iconoEscala);
			imageView.setName(name);

			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					AlgorithmView al = null;
					TabPanel tap = null;

					if (tp.getClass().equals(TabPanel.class)) {
						tap = (TabPanel) tp;
						String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
						if (tap != null && tap.indexOfTab(nombreTab) == -1) {
							ViewImagesBigger viewImageBig = new ViewImagesBigger(image,
									new ArrayList(listImagesPrev.values()),tap);

						}
					} else {
						if (tp.getClass().equals(AlgorithmView.class)) {
							al = (AlgorithmView) tp;
							al.mouseClick(e, image);
						}
					}

				}
			});

			listImagesPrev.put(imageView, image);

			this.add(imageView);
		}
	}

}
