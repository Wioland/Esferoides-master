package interfaces;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	private Map<String, JButton> listImagesPrev;
	private Map<String, Long> lastModifyImage;
	private String dir;
	private List<ImageIcon> imageIcon;

	/*
	 * Mostrar las imagenes tiff como botones
	 */
	public ShowImages(String directory, Component tp) {
		this.dir = directory;
		this.setLayout(new GridLayout(0, 1));

		listImages = new ArrayList<String>();
		listImagesPrev = new HashMap<String, JButton>();
		lastModifyImage = new HashMap<String, Long>();

		File folder = new File(dir);
		createImageButton(folder, tp);

	}

	public List<ImageIcon> getImageIcon() {
		return imageIcon;
	}

	public void setImageIcon(List<ImageIcon> imageIcon) {
		this.imageIcon = imageIcon;
	}

	public Map<String, Long> getLastModifyImage() {
		return lastModifyImage;
	}

	public void setLastModifyImage(Map<String, Long> lastModifyImage) {
		this.lastModifyImage = lastModifyImage;
	}

	public Map<String, JButton> getListImagesPrev() {
		return listImagesPrev;
	}

	public void setListImagesPrev(Map<String, JButton> listImagesPrev) {
		this.listImagesPrev = listImagesPrev;
	}

	public void createImageButton(File folder, Component tp) {

		Utils.search(".*\\.tiff", folder, listImages);
		Collections.sort(listImages);
		imageIcon = new ArrayList<ImageIcon>();

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

			imageIcon.add(image);

			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					AlgorithmView al = null;
					TabPanel tap = null;

					if (tp.getClass().equals(TabPanel.class)) {
						tap = (TabPanel) tp;
						String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
						if (tap != null && tap.indexOfTab(nombreTab) == -1) {

							ViewImagesBigger viewImageBig = new ViewImagesBigger(image, imageIcon, tap);

						}
					} else {
						if (tp.getClass().equals(AlgorithmView.class)) {
							al = (AlgorithmView) tp;
							al.mouseClick(e, image);
						}
					}

				}
			});

			listImagesPrev.put(name, imageView);

			File faux = new File(name);
			lastModifyImage.put(name, faux.lastModified());

			this.add(imageView);
		}
	}

}
