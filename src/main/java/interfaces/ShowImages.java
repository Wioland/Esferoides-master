package interfaces;

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

	public ShowImages(String directory) {
		this.dir = directory;
		listImages = new ArrayList<String>();
		listIm = new ArrayList<ImageIcon>();
		listImagesPrev = new ArrayList<JButton>();
		File folder = new File(dir);
		
		Utils.search(".*\\.tiff", folder, listImages);
		Collections.sort(listImages);

		for (String name : listImages) {
			// convertir a formato que se pueda ver
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);

			listIm.add(image);
			// aniadir a button

			JButton button = new JButton(image);
			button.setSize(100, 100);

			button.addActionListener(new ActionListener() {
				// si se genera el click que muestre un visualizador de imagenes
				public void actionPerformed(ActionEvent e) {
					JButton b = (JButton) e.getSource();
					ViewImagesBigger viewImageBig = new ViewImagesBigger(b.getIcon(), listIm);
				}
			});

			listImagesPrev.add(button);

			this.add(button);
		}

	}

}
