package interfaces;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import funtions.FileFuntions;
import funtions.ShowTiff;
import funtions.Utils;

/**
 * Shows the images like a pictures viewer 
 * 
 * @author Yolanda
 *
 */
public class ViewImagesBigger {

	private List<ImageIcon> listImages;
	private int indexImagenList = 0;
	private Icon image;
	private String dir;
	private TabPanel tp;
	private String indexImageView;
	private AlgorithmView al;
	private int clickImageIndex;
	private List<ImageIcon> newDetectedImages;
	private JPanelComparer jpComparer;
	private String title;
	private Map<String, Integer> indexImage;

	public ViewImagesBigger(List<ImageIcon> listImages, TabPanel tp) {
		this.listImages = listImages;
		this.indexImagenList = 0;
		this.image = listImages.get(indexImagenList);
		this.clickImageIndex = indexImagenList;
		this.indexImageView = "ImageViewer ";
		this.tp = tp;
		this.dir = this.tp.getDir();
		indexImage = new HashMap<String, Integer>();

		jpComparer = new JPanelComparer();
		jpComparer.setLabelImageIcon(image, listImages.get(indexImagenList).getDescription());

		if (listImages.size() == 1) {
			jpComparer.getBackButton().setEnabled(false);
			jpComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used

		if (this.tp != null) {

			jpComparer.remove(jpComparer.getPanelLabelsText());
			jpComparer.getSplitPanelLabelsImages().remove(jpComparer.getSplitPanelLabelsImages().getLeftComponent());
			jpComparer.getPanelButtons().remove(jpComparer.getExitButton());
			jpComparer.getPanelButtons().remove(jpComparer.getSelectButton());

			addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(),
					jpComparer.getTryAlgoButton());
			addListenerMenuScroll(jpComparer.getScrollButton());
			initialiceMap();
			addTotab();

		} else {
			createComparer();
			addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(), false);
		}

	}

	/**
	 * Creates the tab that shows the image selected in its full size
	 * 
	 * @param image      selected image
	 * @param listImages images of the new images created by the algorithms
	 * @param tp         the parent of this component and the place where it is
	 *                   going to be shown
	 * @param selectALgo true if algorithm comparer view to create a comparer
	 */
	public ViewImagesBigger(Icon image, List<ImageIcon> listImages, Component tp, boolean selectALgo) {

		this.listImages = listImages;
		this.image = image;
		this.indexImagenList = listImages.indexOf(image);
		this.clickImageIndex = indexImagenList;
		this.indexImageView = "ImageViewer ";

		if (tp.getClass().equals(TabPanel.class)) {
			this.tp = (TabPanel) tp;
			dir = this.tp.getDir();
		} else {
			if (tp.getClass().equals(AlgorithmView.class)) {
				al = (AlgorithmView) tp;
				dir = al.getDirectory();
			}
		}

		jpComparer = new JPanelComparer();
		jpComparer.setLabelImageIcon(image, listImages.get(indexImagenList).getDescription());

		if (listImages.size() == 1) {
			jpComparer.getBackButton().setEnabled(false);
			jpComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used

		if (this.tp != null) {

			if (selectALgo) {
				createComparer();
				addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(), false);
				jpComparer.getPanelButtons().remove(jpComparer.getExitButton());

			} else {
				jpComparer.remove(jpComparer.getPanelLabelsText());
				jpComparer.getSplitPanelLabelsImages()
						.remove(jpComparer.getSplitPanelLabelsImages().getLeftComponent());
				jpComparer.getPanelButtons().remove(jpComparer.getExitButton());
				jpComparer.getPanelButtons().remove(jpComparer.getSelectButton());
				jpComparer.getPanelButtons().remove(jpComparer.getScrollButton());

				addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(),
						jpComparer.getTryAlgoButton());

			}

			addXTotab();

		} else {
			createComparer();
			addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(), false);
			jpComparer.getPanelButtons().remove(jpComparer.getScrollButton());
		}

	}

	/**
	 * Creates the tab that shows the imagen selected in its full size.
	 * 
	 * @param imagesInPredicctions paths of the images tiff in prediction folder
	 * @param newImagesSelected    original name - button/image selected for this
	 *                             image
	 * @param tp                   the parent of this component and the place where
	 *                             it is going to be shown
	 */
	public ViewImagesBigger(List<String> imagesInPredicctions, Map<String, JButton> newImagesSelected, TabPanel tp) {

		this.newDetectedImages = new ArrayList<ImageIcon>();
		this.listImages = new ArrayList<ImageIcon>();

		this.indexImagenList = 0;
		this.clickImageIndex = 0;
		this.indexImageView = "ImageViewer ";
		this.tp = tp;
		dir = this.tp.getDir();

		ImageIcon i;
		JButton button;
		for (String string : imagesInPredicctions) {
			i = ShowTiff.showTiffToImageIcon(string);
			i.setDescription(string);
			newDetectedImages.add(i);

			button = newImagesSelected.get((FileFuntions.namewithoutExtension(string)).replace("_pred", ""));

			i = ShowTiff.showTiffToImageIcon(button.getName());
			i.setDescription(button.getName());
			listImages.add(i);
		}

		this.image = newDetectedImages.get(0);

		// Initialized the JComparer with the elements needed
		jpComparer = new JPanelComparer();
		jpComparer.setLabelImageIcon(listImages.get(0), listImages.get(0).getDescription());

		if (listImages.size() == 1) { // if there is only one image ,
										// setenable=false the back and forward
										// buttons
										// showwbigger tab
			jpComparer.getBackButton().setEnabled(false);
			jpComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used
		createComparer();
		addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(), true);
		addListenerCancelBu(jpComparer.getExitButton());

	}

	public ViewImagesBigger(Map<String, String> imagesInPredicctions, Map<String, String> newImagesSelected,
			TabPanel tp) {

		this.newDetectedImages = new ArrayList<ImageIcon>();
		this.listImages = new ArrayList<ImageIcon>();

		this.indexImagenList = 0;
		this.clickImageIndex = 0;
		this.indexImageView = "ImageViewer ";
		this.tp = tp;
		dir = this.tp.getDir();

		ImageIcon i;

		for (String oriname : imagesInPredicctions.keySet()) {
			if (newImagesSelected.get(oriname) != null && imagesInPredicctions.get(oriname) != null) {
				i = ShowTiff.showTiffToImageIcon(imagesInPredicctions.get(oriname));
				i.setDescription(imagesInPredicctions.get(oriname));
				newDetectedImages.add(i);

				i = ShowTiff.showTiffToImageIcon(newImagesSelected.get(oriname));
				i.setDescription(newImagesSelected.get(oriname));
				listImages.add(i);

			} else {

				if (newImagesSelected.get(oriname) != null && imagesInPredicctions.get(oriname) == null) {
					JOptionPane.showMessageDialog(Utils.mainFrame, "The image " + oriname
							+ "previously had not got a processed image.\n Saving the one detected with this algoritm");

					File f = new File(newImagesSelected.get(oriname));

					FileFuntions.saveImageNoBeforeProcess(f, this.dir, oriname);

				}
			}

		}

		this.image = newDetectedImages.get(0);

		// Initialized the JComparer with the elements needed
		jpComparer = new JPanelComparer();
		jpComparer.setLabelImageIcon(listImages.get(0), listImages.get(0).getDescription());

		if (listImages.size() == 1) { // if there is only one image ,
										// setenable=false the back and forward
										// buttons
										// showwbigger tab
			jpComparer.getBackButton().setEnabled(false);
			jpComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used
		createComparer();
		addlistenerButton(jpComparer.getBackButton(), jpComparer.getForwarButtonButton(), true);
		addListenerCancelBu(jpComparer.getExitButton());

	}

	// GETTERS AND SETTERS
	public JPanelComparer getJPComparer() {
		return jpComparer;
	}

	public void setJPComparer(JPanelComparer jPComparer) {
		jpComparer = jPComparer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ImageIcon> getListImages() {
		return listImages;
	}

	public void setListImages(List<ImageIcon> listImages) {
		this.listImages = listImages;
	}

	public void setImage(String image) {

		indexImagenList = this.indexImage.get(image);
		moreActionChangeIndexIma();
	}

	// METHODS

	/**
	 * Creates the comparer if the comparer is in the algorithmView frame
	 */
	private void createComparer() {

		if (al != null) {
			ImageIcon ico = ShowTiff.showTiffToImageIcon(al.getImage().getAbsolutePath());
			jpComparer.setOriginalImaLbIcon(ico, al.getImage().getAbsolutePath());
			jpComparer.getPanelButtons().remove(jpComparer.getTryAlgoButton());
			jpComparer.getPanelButtons().remove(jpComparer.getExitButton());
			jpComparer.getPanelButtons().remove(jpComparer.getSelectButton());

		} else {
			jpComparer.getPanelButtons().remove(jpComparer.getScrollButton());
			jpComparer.setOriginalImaLbIcon(image, listImages.get(indexImagenList).getDescription());
			jpComparer.getPanelButtons().remove(jpComparer.getTryAlgoButton());
			addListenerSelectButton(jpComparer.getSelectButton());
		}

		jpComparer.repaint();

	}

	/**
	 * Action performed to change the selected image in the comparer
	 * 
	 * @param originalImaLb label that shows the image selected in the comparer
	 */
	public void mouseSelectAction(JLabel originalImaLb) {
		JOptionPane.showMessageDialog(Utils.mainFrame, "Changing the selected image");

		tp.changeSelectedImage(listImages.get(indexImagenList).getDescription(),
				listImages.get(clickImageIndex).getDescription());

		originalImaLb.setIcon(jpComparer.getLabelImageIcon());
		jpComparer.repaint();

		JOptionPane.showMessageDialog(Utils.mainFrame, "Image changed");

	}

	/**
	 * Changes the name shown in the tab of the comparer/viewImagenBigger or changes
	 * the selected image in the case of the AlgorithmView
	 */
	public void moreActionChangeIndexIma() {
		jpComparer.setLabelImageIcon(listImages.get(indexImagenList), listImages.get(indexImagenList).getDescription());
		image = jpComparer.getLabelImageIcon();

		if (al != null && tp == null) {
			al.setSelectedBu(al.getButtonFromImage(listImages.get(indexImagenList).getDescription()));

		} else {
			changetTabTitle(tp);
		}
	}

	/**
	 * Changes the name of the image shown in the tab name
	 * 
	 * @param tp tabpanel that contains the tab to change name
	 */
	public void changetTabTitle(TabPanel tp) {

		if (tp != null) {
			int indexTab = tp.getSelectedIndex();
			String title = indexImageView + (new File(listImages.get(indexImagenList).getDescription()).getName());
			tp.setTitleAt(indexTab, title);
			tp.repaint();
			this.title = title;

			// In the case the tab has an "X" button we change to the name shown
			// in this
			// panel otherwise the name shown won't change
			JPanel Xpane = (JPanel) tp.getTabComponentAt(indexTab);
			if (Xpane != null) {
				JLabel nameXpane = (JLabel) Xpane.getComponent(0);
				nameXpane.setText(title);
				Xpane.repaint();
			}

		}
	}

	/**
	 * Adds the "X" button to a tab in a tabpanel
	 */
	private void addXTotab() {

		// Gets the name of the tab and add the title
		String nombreImagen = (new File(listImages.get(indexImagenList).getDescription())).getName();
		String title = indexImageView + nombreImagen;
		this.tp.add(title, jpComparer);
		this.tp.setSelectedIndex(this.tp.indexOfTab(title));

		// create the "X" buttton
		int index = this.tp.indexOfTab(title);
		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		JButton btnClose = new JButton("x");
		this.title = title;

		// Add the title and the button side by side in a panel
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;

		pnlTab.add(lblTitle, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		pnlTab.add(btnClose, gbc);

		// add the panel with button "X" and name to the tabpanel to create the
		// tab
		this.tp.setTabComponentAt(index, pnlTab);

		// Adds the action to perform to the "X" button
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				closeTab(e);
			}
		});
	}

	/**
	 * Changes the image of the label that shows the original image (the image saved
	 * in predictions)
	 */
	public void changeOriginalImageLabel() {

		jpComparer.setOriginalImaLbIcon(newDetectedImages.get(indexImagenList),
				newDetectedImages.get(indexImagenList).getDescription());
	}

	/**
	 * Adds the action to perform to the button given (the button select in the
	 * JPanelComparer)
	 * 
	 * @param selectButton button select in the comparer
	 */
	public void addListenerSelectButton(JButton selectButton) {
		selectButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// if newDetectedImages isn't null, we are comparing the images
				// saving in
				// predictions with the selected images of the new images
				// predicted
				// with all the algorithms
				if (newDetectedImages != null) {

					File f = new File(listImages.get(indexImagenList).getDescription());
					String saveDir = f.getAbsolutePath().replace("temporal" + File.separator + f.getName(),
							"predictions");
					boolean b = FileFuntions.saveSelectedImage(f, saveDir);

					// If we save the image
					if (b) {
						listImages.remove(indexImagenList);
						newDetectedImages.remove(indexImagenList);
						indexImagenList = 0;

						// If the isn't more images to save we change the view
						// to show the content of
						// the folder/directory
						if (listImages.size() == 0) {
							deleteTemporalAndRepaintView();

						} else {
							// if there is only one image left we disable the
							// buttons
							if (listImages.size() == 1) {
								jpComparer.getBackButton().setEnabled(false);
								jpComparer.getForwarButtonButton().setEnabled(false);

							}
							// changes the images shown to the first in the
							// array
							image = newDetectedImages.get(indexImagenList);
							jpComparer.setOriginalImaLbIcon(image,
									newDetectedImages.get(indexImagenList).getDescription());
							jpComparer.setLabelImageIcon(listImages.get(indexImagenList),
									listImages.get(indexImagenList).getDescription());
						}
					}

					// Change the label of the original image label to the image
					// of labelImage
				} else {
					mouseSelectAction(jpComparer.getOriginalImaLb());
				}

			}
		});
	}

	/**
	 * Close the tab of the button clicked
	 * 
	 * @param evt action event to perform
	 */
	public void closeTab(ActionEvent evt) {
		JButton bu = (JButton) evt.getSource();
		if (bu.getParent() != null) {
			tp.remove(tp.indexOfTabComponent(bu.getParent()));
		}
	}

	/**
	 * Action for the back and forward buttons
	 * 
	 * @param newVsOld      true if comparer
	 * @param forwardAction true if forward action false if back action
	 */
	public void backForwardActionPerform(boolean newVsOld, boolean forwardAction) {
		if (forwardAction) {
			indexImagenList++;
			if (indexImagenList > listImages.size() - 1) {
				indexImagenList = 0;
			}
		} else {
			indexImagenList--;
			if (indexImagenList < 0) {
				indexImagenList = listImages.size() - 1;
			}
		}

		moreActionChangeIndexIma();

		// if newvsOld we change to the original image if not we only
		// change the
		// labelImage
		if (newVsOld) {
			changeOriginalImageLabel();
		}

		jpComparer.restoreSizeIndicators();
	}

	/**
	 * Adds the listeners to the buttons of the comparer
	 * 
	 * @param backBu    Button to go back in the list of images
	 * @param forwardBu Button to go forward in the list of images
	 * @param newVsOld  boolean to know if we are comparing new predicted images
	 *                  with old ones
	 */
	private void addlistenerButton(JButton backBu, JButton forwardBu, boolean newVsOld) {

		// Back button action
		backBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				backForwardActionPerform(newVsOld, false);

			}
		});

		// forward button action
		forwardBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				backForwardActionPerform(newVsOld, true);

			}
		});
	}

	/**
	 * Adds the action to perform to the cancel/Exit button If we want to exit the
	 * selection/saving image view in the comparer and go to the view of the folder
	 * 
	 * @param cancelBu Button cancel/Exit in the JComparer
	 */
	private void addListenerCancelBu(JButton cancelBu) {
		cancelBu.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int op = JOptionPane.showConfirmDialog(Utils.mainFrame,
						"Do you like to finish saving/selecting new data?", "Exit saving selection",
						JOptionPane.YES_NO_OPTION);
				// If yes change the tab panel to show the view of the content
				// of the
				// folder/directory
				if (op == 0) {
					deleteTemporalAndRepaintView();
				}
			}
		});

	}

	/**
	 * Delete the temporal folders and repaints the view
	 */
	public void deleteTemporalAndRepaintView() {
		File temporal = null;
		List<String> temporalFolders = new ArrayList<String>();
		Utils.searchFoldersName(new File(this.dir), "temporal", temporalFolders, 2);

		for (String string : temporalFolders) {
			temporal = new File(string);
			FileFuntions.deleteFolder(temporal);
		}

		((ImageTreePanel) tp.getParent()).repaintTabPanel(false);

	}

	/**
	 * Adds to the buttons given
	 * 
	 * @param backBu      Back button
	 * @param forwardBu   Forward Button
	 * @param tryAlgoriBu Try another algorithm button
	 */
	private void addlistenerButton(JButton backBu, JButton forwardBu, JButton tryAlgoriBu) {

		// Adds the actions to back and forward
		addlistenerButton(backBu, forwardBu, false);

		// Adds the action to "try" button
		tryAlgoriBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (tp.getRoiModifyTab() != null) {
					tp.getRoiModifyTab().getBtnClose().doClick();
					tp.setRoiModifyTab(null);
				}

				// To the shown image we use all the algorithms
				ImageIcon i = listImages.get(listImages.indexOf(image));
				File f = new File(i.getDescription());

				boolean alreadyAlView = FileFuntions.windowAlgoWiewOfFile(f);

				if (!alreadyAlView) {
					new AlgorithmView(f);
				}

			}
		});

	}

	/**
	 * Delete an image from the list of images that this view is showing when it is
	 * not a comparer
	 */
	public void deleteImageFromListNoComparerOldVsNew() {
		listImages.remove(indexImagenList);
		indexImagenList = 0;

		// if there is only one image left we disable the
		// buttons
		if (listImages.size() == 1) {
			jpComparer.getBackButton().setEnabled(false);
			jpComparer.getForwarButtonButton().setEnabled(false);

		}
		// changes the images shown to the first in the
		// array
		if (listImages.size() != 0) {
			moreActionChangeIndexIma();
		}

	}

	/**
	 * Modify an image from the list of images that this view is showing when it is
	 * not a comparer
	 */
	public void modifyImageFromListNoComparerOldVsNew() {

		ImageIcon i = ShowTiff.showTiffToImageIcon(listImages.get(indexImagenList).getDescription());
		i.setDescription(listImages.get(indexImagenList).getDescription());

		listImages.remove(indexImagenList);
		listImages.add(indexImagenList, i);
		// changes the images shown to the first in the
		// array

		moreActionChangeIndexIma();
	}

	/**
	 * Initialize the map indexImage
	 */
	private void initialiceMap() {
		int i = 0;
		for (ImageIcon imageIcon : listImages) {
			this.indexImage.put(imageIcon.getDescription(), i);
			i++;
		}

	}

	/**
	 * Adds the action to the scrollview button
	 * 
	 * @param scrollButton button to show the images in a scroll view
	 */
	private void addListenerMenuScroll(JButton scrollButton) {

		scrollButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				ShowImages ima = new ShowImages(listImages, tp);
				tp.scrollView(ima);
				scrollButton.setEnabled(false);
			}
		});

	}

	/**
	 * Adds this element to the tab panel
	 */
	private void addTotab() {

		// Gets the name of the tab and add the title
		String nombreImagen = (new File(listImages.get(indexImagenList).getDescription())).getName();
		String title = indexImageView + nombreImagen;
		this.tp.add(title, jpComparer);
		this.tp.setSelectedIndex(this.tp.indexOfTab(title));

		// create the "X" buttton
		int index = this.tp.indexOfTab(title);
		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(title);

		// Add the title and the button side by side in a panel
		this.title = title;

		pnlTab.add(lblTitle);

		// add the panel with button "X" and name to the tabpanel to create the
		// tab
		this.tp.setTabComponentAt(index, pnlTab);
	}
}
