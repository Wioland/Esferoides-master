package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import funtions.FileFuntions;
import funtions.ShowTiff;

public class ViewImagesBigger extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ImageIcon> listImages;
	private JLabel labelImage;
	private int indexImagenList = 0;
	private Icon image;
	private String dir;
	private TabPanel tp;
	private String indexImageView;
	private AlgorithmView al;

	public ViewImagesBigger(Icon image, List<ImageIcon> listImages, Component tp) {

		// setMinimumSize(new Dimension(1000, 800));

		JSplitPane jSp = new JSplitPane();
		// JScrollPane s = new JScrollPane(jSp);

		this.listImages = listImages;
		this.image = image;
		this.indexImagenList = listImages.indexOf(image);
		indexImageView = "ImageViewer ";

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
		JPanel panelButtons = new JPanel();

//		panelButtons.setMaximumSize(new Dimension(200, 200));
//		backBu.setMaximumSize(new Dimension(200, 200));
//		forwardBu.setMaximumSize(new Dimension(200, 200));

		panelButtons.add(backBu);
		panelButtons.add(forwardBu);
		
		jSp.setOrientation(SwingConstants.HORIZONTAL);
		JScrollPane scrollIma = new JScrollPane(labelImage);
		
		jSp.setTopComponent(scrollIma);
		jSp.setBottomComponent(panelButtons);

		jSp.setDividerLocation(800);
		
	
		

		if (this.tp != null) {
			tryAlgoriBu.setText("Try other algorithm");

			addlistenerButton(backBu, forwardBu, tryAlgoriBu);
//			tryAlgoriBu.setMaximumSize(new Dimension(200, 200));
			panelButtons.add(tryAlgoriBu);
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

		} else {
			JSplitPane splitPa = new JSplitPane();
			splitPa.setOrientation(javax.swing.JSplitPane.HORIZONTAL_SPLIT);

			JLabel originalImaLb = new JLabel();
			ImageIcon ico = ShowTiff.showTiffToImageIcon(al.getImage().getAbsolutePath());
			originalImaLb.setIcon(ico);
			originalImaLb.setVisible(true);

			splitPa.setLeftComponent(originalImaLb);
			splitPa.setRightComponent(labelImage);
			JScrollPane scroll= new JScrollPane(splitPa);
			jSp.setTopComponent(scroll);
		
			addlistenerButton(backBu, forwardBu);
		}

		jSp.setVisible(true);
		add(jSp);
		this.setVisible(true);

	}

	public void closeTab(ActionEvent evt) {
		JButton bu = (JButton) evt.getSource();
		if (bu.getParent() != null) {
			tp.remove(tp.indexOfTabComponent(bu.getParent()));
		}
	}

	private void addlistenerButton(JButton backBu, JButton forwardBu) {
		// TODO Auto-generated method stub
		backBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int prevImaIndex = indexImagenList;
				indexImagenList--;
				if (indexImagenList < 0) {
					indexImagenList = listImages.size() - 1;
				}

				labelImage.setIcon(listImages.get(indexImagenList));
				image = labelImage.getIcon();
				if (tp != null) {
					String nombreImagen = (new File(listImages.get(prevImaIndex).getDescription())).getName();
					tp.setTitleAt(tp.indexOfTab(indexImageView + nombreImagen),
							listImages.get(indexImagenList).getDescription());
				}

			}
		});

		forwardBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int prevImaIndex = indexImagenList;
				indexImagenList++;
				if (indexImagenList > listImages.size() - 1) {
					indexImagenList = 0;
				}

				labelImage.setIcon(listImages.get(indexImagenList));
				image = labelImage.getIcon();
				if (tp != null) {
					String nombreImagen = (new File(listImages.get(prevImaIndex).getDescription())).getName();
					int indexTab = tp.indexOfTab(indexImageView + nombreImagen);
					String title = indexImageView
							+ (new File(listImages.get(indexImagenList).getDescription()).getName());
					tp.setTitleAt(indexTab, title);
					// lblTitle.settext(title);
					tp.repaint();
				}

			}
		});
	}

	private void addlistenerButton(JButton backBu, JButton forwardBu, JButton tryAlgoriBu) {

		addlistenerButton(backBu, forwardBu);

		tryAlgoriBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				ImageIcon i = listImages.get(listImages.indexOf(image));
				File f = new File(i.getDescription());
				
				

				AlgorithmView alg = new AlgorithmView(f, dir);

			}
		});

	}

}
