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

	/**
	 * For showing the tiff images like buttons
	 * 
	 * @param directory the current directory path
	 * @param tp        the container where the buttons are going to be shown
	 */
	public ShowImages(String directory, Component tp) {
		// we put them in one column
		initializeComponents(1);

		File folder = new File(directory);
		createImageButton(folder, tp, "");

	}

	/**
	 * For showing the tiff images like buttons
	 * 
	 * @param tp           the tabpanel in which the buttons are going to be shown
	 * @param images       the images we want to show in the buttons
	 * @param originalName the original name of the group of files (the name of the
	 *                     nd2 or tiff file)
	 */
	public ShowImages(TabPanel tp, List<String> images, String originalName) {
		// we put then in two columns
		initializeComponents(2);
		createImageButton(tp, images, originalName);

	}

	/**
	 * For showing the tiff images like buttons in the algoritm view
	 * 
	 * @param directory     the current working folder path
	 * @param algorithmView the Jframe for showing the algorithm images
	 * @param nameFileNoExt name of the file without extension. the file we wants to
	 *                      find it's tiff in the predictions folder if "" all the
	 *                      files
	 */
	public ShowImages(String directory, AlgorithmView algorithmView, String nameFileNoExt) {
		// we put them in one column
		initializeComponents(1);

		File folder = new File(directory);
		createImageButton(folder, algorithmView, nameFileNoExt);
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

	// METHODS

	/**
	 * Initialize the components of the class and set the number of columns of the
	 * layout
	 * 
	 * @param columns number of columns we want to have in the layout
	 */
	public void initializeComponents(int columns) {
		this.setLayout(new GridLayout(0, columns));

		listImages = new ArrayList<String>();
		listImagesPrev = new HashMap<String, JButton>();
		lastModifyImage = new HashMap<String, Long>();
	}

	/**
	 * Creates the buttons to show the images of the folder given
	 * 
	 * @param folder        The location of the images
	 * @param nameFileNoExt name of the file without extension of the file we wants
	 *                      to find it's tiff in the predictions folder if "" all
	 *                      the files
	 * @param tp            the place we are going to show the images
	 */
	public void createImageButton(File folder, Component tp, String nameFileNoExt) {

		// We checks if the tiff files are in the predictions folder, if not we
		// ask to
		// move it there
		listImages = FileFuntions.checkTiffNotPredictionsFolder(folder, nameFileNoExt);

		Collections.sort(listImages);
		imageIcon = new ArrayList<ImageIcon>();
		ImageIcon iconoEscala;
		JButton imageView;
		File faux;

		for (String name : listImages) {
			// we transform the images to imageIcon for showing them in the
			// interface
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);
			image.setDescription(name);

			// add the button
			// Get a icon with the specific dimension
			iconoEscala = new ImageIcon(image.getImage().getScaledInstance(700, 700, java.awt.Image.SCALE_DEFAULT));
			imageView = new JButton(iconoEscala);
			imageView.setIcon(iconoEscala);
			imageView.setName(name);

			imageIcon.add(image);

			// Adds the mouse click actions of the button
			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					AlgorithmView al = null;
					TabPanel tap = null;

					// if the button is inside a tab panel we create a new tab
					if (tp.getClass().equals(TabPanel.class)) {
						tap = (TabPanel) tp;
						String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
						if (tap != null) {

							tap.setSelectedIndex(tap.indexOfTab(tap.getViewImagen().getTitle()));

							if (tap.indexOfTab(nombreTab) == -1) {

								tap.getViewImagen().setImage(image.getDescription());
							}
//							new ViewImagesBigger(imageIcon.get(listImages.indexOf(name)), imageIcon, tap, false);

						}
					} else {
						// if we are in an algorithm view we change create a
						// comparer
						if (tp.getClass().equals(AlgorithmView.class)) {
							al = (AlgorithmView) tp;
							al.mouseClick(e, image);
						}
					}

				}
			});

			listImagesPrev.put(name, imageView);
			// save the last time the file was modify
			faux = new File(name);
			lastModifyImage.put(name, faux.lastModified());

			this.add(imageView);
		}
	}

	/**
	 * Creates the buttons of the list of images given
	 * 
	 * @param tp           the tap panel to show the buttons
	 * @param images       the list of images to show
	 * @param origianlName the name of the nd2 or tif file without extension
	 */
	public void createImageButton(TabPanel tp, List<String> images, String origianlName) {

		listImages = images;

		Collections.sort(listImages);
		imageIcon = new ArrayList<ImageIcon>();
		ImageIcon iconoEscala;
		JButton imageView;

		for (String name : listImages) {
			// change to a imageIcon the tiff to show
			ImageIcon image = ShowTiff.showTiffToImageIcon(name);
			image.setDescription(name);

			// add the button
			// Get a icon with the specific dimension
			iconoEscala = new ImageIcon(image.getImage().getScaledInstance(300, 300, java.awt.Image.SCALE_DEFAULT));
			imageView = new JButton(iconoEscala);
			imageView.setIcon(iconoEscala);
			imageView.setName(name);

			// we put the button to opaque in order to see the background if we
			// changed it
			imageView.setContentAreaFilled(false);
			imageView.setOpaque(true);
			imageIcon.add(image);

			imageView.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					clickImageButtonAlgoritm(e, tp, image, origianlName);
				}
			});

			listImagesPrev.put(name, imageView);

			this.add(imageView);
		}
	}

	/**
	 * Adds the actions to the buttons to select or open a comparer
	 * 
	 * @param e            mouse even
	 * @param tp           the tab panel where the images are
	 * @param image        the image of the button clicked
	 * @param origianlName the name of the nd2 or tif image without extension
	 */
	private void clickImageButtonAlgoritm(MouseEvent e, TabPanel tp, ImageIcon image, String origianlName) {
		if (e.getClickCount() == 2 && !e.isConsumed()) {
			e.consume();

			// If only one click is performed we changed the selected image, if
			// two we
			// create a comparer if we can because there is no other open with
			// the same
			// image
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
					// if there is no tab with the name of the original image we
					// create a comparer
					new ViewImagesBigger(image, imageIcon, tp, true);
				} else {
					// if there is already a tab for this image we gave it the
					// focus
					tp.setSelectedIndex(index);
				}
			}

		}

		// add or exchange the final image of this type for the selected one
		JButton oldSelected = tp.getOriginalNewSelected().get(origianlName);
		if (oldSelected != null) {
			oldSelected.setBackground(null);
		}
		JButton buttonSelected = (JButton) e.getSource();
		buttonSelected.setBackground(Color.yellow);
		tp.getOriginalNewSelected().put(origianlName, buttonSelected);

	}
}
