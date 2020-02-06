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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import funtions.FileFuntions;
import funtions.ShowTiff;

public class ViewImagesBigger extends JPanel {

	private static final long serialVersionUID = 1L;
	private List<ImageIcon> listImages;
	private JLabel labelImage;
	private JLabel originalImaLb;
	private int indexImagenList = 0;
	private Icon image;
	private String dir;
	private TabPanel tp;
	private String indexImageView;
	private AlgorithmView al;
	private JPanel panelButtons;
	private int clickImageIndex;
	private Map<String, JButton> newDetectedImages;

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

		// Se aniade la imagen
		labelImage = new JLabel();
		labelImage.setIcon(image);
		labelImage.setVisible(true);

		// se aniaden los botones para poder pasar las imagenes
		JButton backBu = new JButton();
		JButton forwardBu = new JButton();
		JButton tryAlgoriBu = new JButton();
		backBu.setText("<");
		forwardBu.setText(">");
		if (listImages.size() == 1) {
			backBu.setEnabled(false);
			forwardBu.setEnabled(false);

		}

		// contenedor de botones y puesta en orden de estos

		panelButtons = new JPanel();

		panelButtons.add(backBu);
		panelButtons.add(forwardBu);

		JScrollPane scrollIma = new JScrollPane(labelImage);

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridx = 0;
		constraints.gridy = 0;

		if (this.tp != null) {

			if (selectALgo) {
				createComparer(constraints);
				addlistenerButton(backBu, forwardBu, false);

			} else {
				this.add(scrollIma, constraints);
				tryAlgoriBu.setText("Try other algorithms");

				addlistenerButton(backBu, forwardBu, tryAlgoriBu);
				panelButtons.add(tryAlgoriBu);
			}

			addXTotab();

		} else {
			createComparer(constraints);
			addlistenerButton(backBu, forwardBu, false);
		}

		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 1;
		this.add(panelButtons, constraints);

		this.setVisible(true);

	}

	public ViewImagesBigger(List<String> imagesInPredicctions, Map<String, JButton> newImagesSelected, TabPanel tp) {

		this.newDetectedImages = newImagesSelected;
		this.listImages = new ArrayList<ImageIcon>();

		String n = FileFuntions.namewithoutExtension(imagesInPredicctions.get(0)).replace("_pred", "");
		this.image = ShowTiff.showTiffToImageIcon(newImagesSelected.get(n).getName());

		this.indexImagenList = 0;
		this.clickImageIndex = 0;
		this.indexImageView = "ImageViewer ";
		this.tp = tp;
		dir = this.tp.getDir();

		for (String string : imagesInPredicctions) {
			ImageIcon i = ShowTiff.showTiffToImageIcon(string);
			i.setDescription(string);
			listImages.add(i);
		}

		// Se aniade la imagen
		labelImage = new JLabel();
		labelImage.setIcon(listImages.get(0));
		labelImage.setVisible(true);

		// se aniaden los botones para poder pasar las imagenes
		JButton backBu = new JButton();
		JButton forwardBu = new JButton();
		JButton cancelBu = new JButton();

		cancelBu.setText("Exit");
		backBu.setText("<");
		forwardBu.setText(">");
		if (imagesInPredicctions.size() == 1) {
			backBu.setEnabled(false);
			forwardBu.setEnabled(false);

		}

		// contenedor de botones y puesta en orden de estos

		panelButtons = new JPanel();

		panelButtons.add(backBu);
		panelButtons.add(forwardBu);
		panelButtons.add(cancelBu);

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 1;
		constraints.weighty = 1;

		constraints.gridx = 0;
		constraints.gridy = 0;

		createComparer(constraints);
		addlistenerButton(backBu, forwardBu, true);
		addListenerCancelBu(cancelBu);

		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 1;
		this.add(panelButtons, constraints);

		this.setVisible(true);

	}

	

	public JLabel getOriginalImaLb() {
		return originalImaLb;
	}

	public void setOriginalImaLb(JLabel originalImaLb) {
		this.originalImaLb = originalImaLb;
	}

	public JLabel getLabelImage() {
		return labelImage;
	}

	public void setLabelImage(JLabel labelImage) {
		this.labelImage = labelImage;
	}

	public void closeTab(ActionEvent evt) {
		JButton bu = (JButton) evt.getSource();
		if (bu.getParent() != null) {
			tp.remove(tp.indexOfTabComponent(bu.getParent()));
		}
	}

	private void createComparer(GridBagConstraints constraints) {

		JSplitPane splitPa = new JSplitPane();
		splitPa.setOrientation(javax.swing.JSplitPane.HORIZONTAL_SPLIT);

		originalImaLb = new JLabel();

		if (al != null) {
			ImageIcon ico = ShowTiff.showTiffToImageIcon(al.getImage().getAbsolutePath());
			originalImaLb.setIcon(ico);
		} else {

			originalImaLb.setIcon(image);
			JButton selectButton = new JButton("Select");
			selectButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (newDetectedImages != null) {

						File f = new File(newDetectedImages
								.get(FileFuntions.namewithoutExtension(listImages.get(indexImagenList).getDescription())
										.replace("_pred", ""))
								.getName());

						FileFuntions.saveSelectedImage(f, dir);

						listImages.remove(indexImagenList);
						indexImagenList = 0;
						
						if (listImages.size() == 0) {

							
							((ImageTreePanel)tp.getParent()).repaintTabPanel(false);
							
							
						} else {

							if (listImages.size() == 1) {

								
							JButton	back=(JButton) panelButtons.getComponent(0);
							JButton	forward=(JButton) panelButtons.getComponent(1);
							
							back.setEnabled(false);
							forward.setEnabled(false);
								
							}
							String n = FileFuntions.namewithoutExtension(
									(listImages.get(indexImagenList).getDescription()).replace("_pred", ""));
							image = ShowTiff.showTiffToImageIcon(newDetectedImages.get(n).getName());
							originalImaLb.setIcon(image);
							labelImage.setIcon(listImages.get(indexImagenList));
						}

					} else {
						mouseSelectAction(originalImaLb);
					}

				}
			});

			panelButtons.add(selectButton);

		}

		originalImaLb.setVisible(true);

		splitPa.setLeftComponent(new JScrollPane(originalImaLb));
		splitPa.setRightComponent(new JScrollPane(labelImage));

		splitPa.setVisible(true);

		this.add(splitPa, constraints);
		splitPa.setDividerLocation(500);
		this.repaint();

	}

	public void mouseSelectAction(JLabel originalImaLb) {
		JOptionPane.showMessageDialog(tp.getJFrameGeneral(), "Changing the selected image");

		tp.changeSelectedImage(listImages.get(indexImagenList).getDescription(),
				listImages.get(clickImageIndex).getDescription());

		originalImaLb.setIcon(labelImage.getIcon());
		this.repaint();

		JOptionPane.showMessageDialog(tp.getJFrameGeneral(), "Image changed");

	}

	private void addXTotab() {

		String nombreImagen = (new File(listImages.get(indexImagenList).getDescription())).getName();
		String title = indexImageView + nombreImagen;
		this.tp.add(title, this);
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

		originalImaLb.setIcon(ShowTiff.showTiffToImageIcon(this.newDetectedImages.get(FileFuntions
				.namewithoutExtension(listImages.get(indexImagenList).getDescription()).replace("_pred", ""))
				.getName()));
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
				int op=JOptionPane.showConfirmDialog(tp.getJFrameGeneral(), "Do you like to finish saving/selecting new data?");
				if(op==0) {
					((ImageTreePanel)tp.getParent()).repaintTabPanel(false);
				}
				
			}
		});
		
	}
	private void moreActionChangeIndexIma() {
		labelImage.setIcon(listImages.get(indexImagenList));
		image = labelImage.getIcon();

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
