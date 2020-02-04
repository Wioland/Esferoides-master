package interfaces;

import java.awt.Color;
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

import funtions.FileFuntions;
import funtions.ShowTiff;

public class ShowImages extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> listImages;
	private Map<String, JButton> listImagesPrev;
	private Map<String, Long> lastModifyImage;

	private List<ImageIcon> imageIcon;

	/*
	 * Mostrar las imagenes tiff como botones
	 */
	public ShowImages(String directory, Component tp) {

		this.setLayout(new GridLayout(0, 1));

		listImages = new ArrayList<String>();
		listImagesPrev = new HashMap<String, JButton>();
		lastModifyImage = new HashMap<String, Long>();

		File folder = new File(directory);
		createImageButton(folder, tp);

	}

	public ShowImages(TabPanel tp, List<String> images, String originalName) {

		this.setLayout(new GridLayout(0, 2));

		listImages = new ArrayList<String>();
		listImagesPrev = new HashMap<String, JButton>();
		lastModifyImage = new HashMap<String, Long>();

		createImageButton(tp, images, originalName);

	}

	// GETTERS Y SETTERS

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

	public List<String> getListImages() {
		return listImages;
	}

	public void setListImages(List<String> listImages) {
		this.listImages = listImages;
	}

	// METHOS

	public void createImageButton(File folder, Component tp) {

		listImages = FileFuntions.checkTiffNotPredictionsFolder(folder);

		Collections.sort(listImages);
		imageIcon = new ArrayList<ImageIcon>();
		ImageIcon iconoEscala;
		JButton imageView;
		File faux;

		for (String name : listImages) {
			// convertir a formato que se pueda ver
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);
			image.setDescription(name);

			// aniadir a button
			// Obtiene un icono en escala con las dimensiones especificadas
			iconoEscala = new ImageIcon(image.getImage().getScaledInstance(700, 700, java.awt.Image.SCALE_DEFAULT));
			imageView = new JButton(iconoEscala);
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

							new ViewImagesBigger(image, imageIcon, tap, false);

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

			faux = new File(name);
			lastModifyImage.put(name, faux.lastModified());

			this.add(imageView);
		}
	}

	public void createImageButton(TabPanel tp, List<String> images, String origianlName) {

		listImages = images;

		Collections.sort(listImages);
		imageIcon = new ArrayList<ImageIcon>();
		ImageIcon iconoEscala;
		JButton imageView;
		// File faux;

		for (String name : listImages) {
			// convertir a formato que se pueda ver
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);
			image.setDescription(name);

			// aniadir a button
			// Obtiene un icono en escala con las dimensiones especificadas
			iconoEscala = new ImageIcon(image.getImage().getScaledInstance(300, 300, java.awt.Image.SCALE_DEFAULT));
			imageView = new JButton(iconoEscala);
			imageView.setIcon(iconoEscala);
			imageView.setName(name);

			// these next two lines do the magic..
			imageView.setContentAreaFilled(false);
			imageView.setOpaque(true);
			imageIcon.add(image);

			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					clickImageButtonAlgoritm(e, tp, image, origianlName);
				}
			});

			listImagesPrev.put(name, imageView);

			// Poner a la hora de hacer definitivo el guardado
//			faux = new File(name);
//			lastModifyImage.put(name, faux.lastModified());

			this.add(imageView);
		}
	}

	private void clickImageButtonAlgoritm(MouseEvent e, TabPanel tp, ImageIcon image, String origianlName) {
		if (e.getClickCount() == 2 && !e.isConsumed()) {
			e.consume();

			// si solo se realiza un click se cambia la seleccionada, se se hacen dos se
			// habre un comparadar si se puede
			String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
			if (tp != null && tp.indexOfTab(nombreTab) == -1) {
				int index = -1;
				int i = 0;

				while (index == -1 && i < tp.getComponentCount() - 1) {
					if (tp.getTitleAt(i).contains(origianlName)) {
						index = i;
					} else {
						i++;
					}
				}

				if (index == -1) {
					// si no hay ningun tab que contenga el nombre del original de este tipo de
					// imagen se crea un comparador
					new ViewImagesBigger(image, imageIcon, tp, true);
				} else {
					// se pone el foco a al tab de ese tipo de imagenes
					tp.setSelectedIndex(index);
				}
			}

		}

		// se añade o se sustituye la imagen definitiva a guardar de ese tipo por la
		// seleccionada actual
		JButton oldSelected = tp.getOriginalNewSelected().get(origianlName);
		if (oldSelected != null) {
			oldSelected.setBackground(null);
		}
		JButton buttonSelected = (JButton) e.getSource();
		buttonSelected.setBackground(Color.yellow);
		tp.getOriginalNewSelected().put(origianlName, buttonSelected);

	}
}
