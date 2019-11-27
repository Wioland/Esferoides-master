package interfaces;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	public ShowImages(String directory, Component tp) {
		this.dir = directory;
		this.setLayout(new GridLayout(0, 1));

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
					} else {
						if (tp.getClass().equals(AlgorithmView.class)) {
							al = (AlgorithmView) tp;
						}
					}
					String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
					if (tap != null && tap.indexOfTab(nombreTab) == -1) {
						ViewImagesBigger viewImageBig = new ViewImagesBigger(image, listIm, dir, false, tap);

					} else {

						mouseClick(e, image, al);
					}
				}
			});

			listImagesPrev.add(imageView);

			this.add(imageView);
		}

	}

	public void mouseClick(MouseEvent me, ImageIcon imageIcon, AlgorithmView al) {
		if (!me.isConsumed()) {
			switch (me.getClickCount()) {
			case 1:
				al.setSelectedBu((JButton) me.getSource());
				al.getSelectedBu().setName(((JButton) me.getSource()).getName());
				System.out.println(((JButton) me.getSource()).getName());
				break;
			case 2:
				me.consume();
				ViewImagesBigger vi = new ViewImagesBigger(imageIcon, al.getImageIcoList(), al.getDirectory(), true,
						null);
				al.getOpenWindows().add(vi);
				break;

			default:
				break;
			}

		}

	}

}
