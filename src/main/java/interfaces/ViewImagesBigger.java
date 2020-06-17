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

import esferoides.Methods;
import funtions.FileFuntions;
import funtions.ShowTiff;
import funtions.Utils;

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
	private JPanelComparer JPComparer;
	private String title;
	private Map<String,Integer> indexImage;

	public ViewImagesBigger(List<ImageIcon> listImages, TabPanel tp) {
		this.listImages = listImages;
		this.indexImagenList = 0;
		this.image = listImages.get(indexImagenList);
		this.clickImageIndex = indexImagenList;
		this.indexImageView = "ImageViewer ";
		this.tp = tp;
		this.dir = this.tp.getDir();
		indexImage= new HashMap< String,Integer>();

		JPComparer = new JPanelComparer();
		JPComparer.setLabelImageIcon(image);

		if (listImages.size() == 1) {
			JPComparer.getBackButton().setEnabled(false);
			JPComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used

		if (this.tp != null) {

			JPComparer.remove(JPComparer.getPanelLabelsText());
			JPComparer.getSplitPanelLabelsImages().remove(JPComparer.getSplitPanelLabelsImages().getLeftComponent());
			JPComparer.getPanelButtons().remove(JPComparer.getExitButton());
			JPComparer.getPanelButtons().remove(JPComparer.getSelectButton());

			addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(),
					JPComparer.getTryAlgoButton());
			addListenerMenuScroll(JPComparer.getScrollButton());
initialiceMap();
			addTotab();

		} else {
			createComparer();
			addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(), false);
		}

	}

	private void initialiceMap() {
		int i=0;
		for (ImageIcon imageIcon : listImages) {
			this.indexImage.put( imageIcon.getDescription(),i);
			i++;
		}
		
	}

	private void addListenerMenuScroll(JButton scrollButton) {
		// TODO Auto-generated method stub
		scrollButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				ShowImages ima= new ShowImages(listImages, tp) ;
				tp.scrollView(ima);
				scrollButton.setEnabled(false);
			}
		});

	}

	private void addTotab() {
		// TODO Auto-generated method stub

		// Gets the name of the tab and add the title
		String nombreImagen = (new File(listImages.get(indexImagenList).getDescription())).getName();
		String title = indexImageView + nombreImagen;
		this.tp.add(title, JPComparer);
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

		JPComparer = new JPanelComparer();
		JPComparer.setLabelImageIcon(image);

		if (listImages.size() == 1) {
			JPComparer.getBackButton().setEnabled(false);
			JPComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used

		if (this.tp != null) {

			if (selectALgo) {
				createComparer();
				addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(), false);
				JPComparer.getPanelButtons().remove(JPComparer.getExitButton());

			} else {
				JPComparer.remove(JPComparer.getPanelLabelsText());
				JPComparer.getSplitPanelLabelsImages()
						.remove(JPComparer.getSplitPanelLabelsImages().getLeftComponent());
				JPComparer.getPanelButtons().remove(JPComparer.getExitButton());
				JPComparer.getPanelButtons().remove(JPComparer.getSelectButton());

				addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(),
						JPComparer.getTryAlgoButton());

				JPComparer.getPanelButtons().remove(JPComparer.getScrollButton());

			}

			addXTotab();

		} else {
			createComparer();
			addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(), false);
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
		JPComparer = new JPanelComparer();
		JPComparer.setLabelImageIcon(listImages.get(0));

		if (listImages.size() == 1) { // if there is only one image ,
										// setenable=false the back and forward
										// buttons
										// showwbigger tab
			JPComparer.getBackButton().setEnabled(false);
			JPComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used
		createComparer();
		addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(), true);
		addListenerCancelBu(JPComparer.getExitButton());

	}

	public ViewImagesBigger(List<String> imagesInPredicctions, List<String> newImagesSelected, TabPanel tp) {

		this.newDetectedImages = new ArrayList<ImageIcon>();
		this.listImages = new ArrayList<ImageIcon>();

		this.indexImagenList = 0;
		this.clickImageIndex = 0;
		this.indexImageView = "ImageViewer ";
		this.tp = tp;
		dir = this.tp.getDir();

		ImageIcon i;
		int j = 0;
		for (String string : imagesInPredicctions) {
			i = ShowTiff.showTiffToImageIcon(string);
			i.setDescription(string);
			newDetectedImages.add(i);

			i = ShowTiff.showTiffToImageIcon(newImagesSelected.get(j));
			i.setDescription(newImagesSelected.get(j));
			listImages.add(i);
			j++;
		}

		this.image = newDetectedImages.get(0);

		// Initialized the JComparer with the elements needed
		JPComparer = new JPanelComparer();
		JPComparer.setLabelImageIcon(listImages.get(0));

		if (listImages.size() == 1) { // if there is only one image ,
										// setenable=false the back and forward
										// buttons
										// showwbigger tab
			JPComparer.getBackButton().setEnabled(false);
			JPComparer.getForwarButtonButton().setEnabled(false);

		}

		// Create the comparer and adding the listener to the buttons used
		createComparer();
		addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(), true);
		addListenerCancelBu(JPComparer.getExitButton());

	}

	// GETTERS AND SETTERS
	public JPanelComparer getJPComparer() {
		return JPComparer;
	}

	public void setJPComparer(JPanelComparer jPComparer) {
		JPComparer = jPComparer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// METHODS

	public void setImage(String image) {
		
		
		indexImagenList=this.indexImage.get(image);
		 moreActionChangeIndexIma();
	}
	
	
	/**
	 * Creates the comparer if the comparer is in the algorithmView frame
	 */
	private void createComparer() {

		if (al != null) {
			ImageIcon ico = ShowTiff.showTiffToImageIcon(al.getImage().getAbsolutePath());
			JPComparer.setOriginalImaLbIcon(ico);
			JPComparer.getPanelButtons().remove(JPComparer.getTryAlgoButton());
			JPComparer.getPanelButtons().remove(JPComparer.getExitButton());
			JPComparer.getPanelButtons().remove(JPComparer.getSelectButton());

		} else {
			JPComparer.getPanelButtons().remove(JPComparer.getScrollButton());
			JPComparer.setOriginalImaLbIcon(image);
			JPComparer.getPanelButtons().remove(JPComparer.getTryAlgoButton());
			addListenerSelectButton(JPComparer.getSelectButton());
		}

		JPComparer.repaint();

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

		originalImaLb.setIcon(JPComparer.getLabelImageIcon());
		JPComparer.repaint();

		JOptionPane.showMessageDialog(Utils.mainFrame, "Image changed");

	}

	/**
	 * Changes the name shown in the tab of the comparer/viewImagenBigger or changes
	 * the selected image in the case of the AlgorithmView
	 */
	private void moreActionChangeIndexIma() {
		JPComparer.setLabelImageIcon(listImages.get(indexImagenList));
		image = JPComparer.getLabelImageIcon();

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
		this.tp.add(title, JPComparer);
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
				// TODO Auto-generated method stub
				closeTab(e);
			}
		});
	}

	/**
	 * Changes the image of the label that shows the original image (the image saved
	 * in predictions)
	 */
	public void changeOriginalImageLabel() {

		JPComparer.setOriginalImaLbIcon(newDetectedImages.get(indexImagenList));
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
					boolean b = FileFuntions.saveSelectedImage(f, dir+"predictions");

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
								JPComparer.getBackButton().setEnabled(false);
								JPComparer.getForwarButtonButton().setEnabled(false);

							}
							// changes the images shown to the first in the
							// array
							image = newDetectedImages.get(indexImagenList);
							JPComparer.setOriginalImaLbIcon(image);
							JPComparer.setLabelImageIcon(listImages.get(indexImagenList));
						}
					}

					// Change the label of the original image label to the image
					// of labelImage
				} else {
					mouseSelectAction(JPComparer.getOriginalImaLb());
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

	public void backActionPerform(boolean newVsOld) {
		indexImagenList--;
		if (indexImagenList < 0) {
			indexImagenList = listImages.size() - 1;
		}
		moreActionChangeIndexIma();

		// if newvsOld we change to the original image if not we only
		// change the
		// labelImage
		if (newVsOld) {
			changeOriginalImageLabel();
		}
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

				backActionPerform(newVsOld);

			}
		});

		// forward button action
		forwardBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				indexImagenList++;
				if (indexImagenList > listImages.size() - 1) {
					indexImagenList = 0;
				}

				moreActionChangeIndexIma();
				// if newvsOld we change to the original image if not we only
				// change the
				// labelImage
				if (newVsOld) {
					changeOriginalImageLabel();
				}
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
			}}
		});

	}
	
	public void deleteTemporalAndRepaintView() {
		File temporal= null;
		if(dir.endsWith(File.separator)) {
			temporal= new File(dir+"temporal");
		}else {
			temporal= new File(dir+File.separator+"temporal");
		}
		FileFuntions.deleteFolder(temporal);
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

}
