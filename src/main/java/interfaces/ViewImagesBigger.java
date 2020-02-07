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

		// contenedor de botones y puesta en orden de estos

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

			}

			addXTotab();

		} else {
			createComparer();
			addlistenerButton(JPComparer.getBackButton(), JPComparer.getForwarButtonButton(), false);
		}

	}

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

		if (listImages.size() == 1) { // if there is only one image , setenable=false the back and forward buttons
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

	// METHODS

	/**
	 * Creates the co
	 */
	private void createComparer() {

		if (al != null) {
			ImageIcon ico = ShowTiff.showTiffToImageIcon(al.getImage().getAbsolutePath());
			JPComparer.setOriginalImaLbIcon(ico);
			JPComparer.getPanelButtons().remove(JPComparer.getTryAlgoButton());
			JPComparer.getPanelButtons().remove(JPComparer.getExitButton());
			JPComparer.getPanelButtons().remove(JPComparer.getSelectButton());

		} else {

			JPComparer.setOriginalImaLbIcon(image);
			JPComparer.getPanelButtons().remove(JPComparer.getTryAlgoButton());
			addListenerSelectButton(JPComparer.getSelectButton());
		}

		JPComparer.repaint();

	}

	public void mouseSelectAction(JLabel originalImaLb) {
		JOptionPane.showMessageDialog(tp.getJFrameGeneral(), "Changing the selected image");

		tp.changeSelectedImage(listImages.get(indexImagenList).getDescription(),
				listImages.get(clickImageIndex).getDescription());

		originalImaLb.setIcon(JPComparer.getLabelImageIcon());
		JPComparer.repaint();

		JOptionPane.showMessageDialog(tp.getJFrameGeneral(), "Image changed");

	}

	private void moreActionChangeIndexIma() {
		JPComparer.setLabelImageIcon(listImages.get(indexImagenList));
		image = JPComparer.getLabelImageIcon();

		if (al != null && tp == null) {
			al.setSelectedBu(al.getButtonFromImage(listImages.get(indexImagenList).getDescription()));

		} else {
			changetTabTitle(tp);
		}
	}

	public void changetTabTitle(TabPanel tp) {

		if (tp != null) {
			int indexTab = tp.getSelectedIndex();
			String title = indexImageView + (new File(listImages.get(indexImagenList).getDescription()).getName());
			tp.setTitleAt(indexTab, title);
			tp.repaint();

			JPanel Xpane = (JPanel) tp.getTabComponentAt(indexTab);
			if (Xpane != null) {
				JLabel nameXpane = (JLabel) Xpane.getComponent(0);
				nameXpane.setText(title);
				Xpane.repaint();
			}

		}
	}

	private void addXTotab() {

		String nombreImagen = (new File(listImages.get(indexImagenList).getDescription())).getName();
		String title = indexImageView + nombreImagen;
		this.tp.add(title, JPComparer);
		this.tp.setSelectedIndex(this.tp.indexOfTab(title));

		int index = this.tp.indexOfTab(title);
		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		JButton btnClose = new JButton("x");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;

		pnlTab.add(lblTitle, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		pnlTab.add(btnClose, gbc);

		this.tp.setTabComponentAt(index, pnlTab);

		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeTab(e);
			}
		});
	}

	public void changeOriginalImageLabel() {

		JPComparer.setOriginalImaLbIcon(newDetectedImages.get(indexImagenList));
	}

	public void addListenerSelectButton(JButton selectButton) {
		selectButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (newDetectedImages != null) {

					File f = new File(newDetectedImages.get(indexImagenList).getDescription());

					boolean b = FileFuntions.saveSelectedImage(f, dir);
					if (b) {
						listImages.remove(indexImagenList);
						newDetectedImages.remove(indexImagenList);
						indexImagenList = 0;

						if (listImages.size() == 0) {

							((ImageTreePanel) tp.getParent()).repaintTabPanel(false);

						} else {

							if (listImages.size() == 1) {
								JPComparer.getBackButton().setEnabled(false);
								JPComparer.getForwarButtonButton().setEnabled(false);

							}

							image = newDetectedImages.get(indexImagenList);
							JPComparer.setOriginalImaLbIcon(image);
							JPComparer.setLabelImageIcon(listImages.get(indexImagenList));
						}
					}

				} else {
					mouseSelectAction(JPComparer.getOriginalImaLb());
				}

			}
		});
	}

	/**
	 * 
	 * @param evt
	 */
	public void closeTab(ActionEvent evt) {
		JButton bu = (JButton) evt.getSource();
		if (bu.getParent() != null) {
			tp.remove(tp.indexOfTabComponent(bu.getParent()));
		}
	}

	private void addlistenerButton(JButton backBu, JButton forwardBu, boolean newVsOld) {
		// TODO Auto-generated method stub
		backBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				indexImagenList--;
				if (indexImagenList < 0) {
					indexImagenList = listImages.size() - 1;
				}
				moreActionChangeIndexIma();
				if (newVsOld) {
					changeOriginalImageLabel();
				}

			}
		});

		forwardBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				indexImagenList++;
				if (indexImagenList > listImages.size() - 1) {
					indexImagenList = 0;
				}

				moreActionChangeIndexIma();
				if (newVsOld) {
					changeOriginalImageLabel();
				}
			}
		});
	}

	private void addListenerCancelBu(JButton cancelBu) {
		cancelBu.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int op = JOptionPane.showConfirmDialog(tp.getJFrameGeneral(),
						"Do you like to finish saving/selecting new data?");
				if (op == 0) {
					((ImageTreePanel) tp.getParent()).repaintTabPanel(false);
				}

			}
		});

	}

	private void addlistenerButton(JButton backBu, JButton forwardBu, JButton tryAlgoriBu) {

		addlistenerButton(backBu, forwardBu, false);

		tryAlgoriBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				ImageIcon i = listImages.get(listImages.indexOf(image));
				File f = new File(i.getDescription());

				new AlgorithmView(f, dir);

			}
		});

	}

}
