package interfaces;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

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

	public ViewImagesBigger(Icon image, List<ImageIcon> listImages, String directory, boolean onlreadyAlgo,
			TabPanel tp) {

		setMinimumSize(new Dimension(1000, 800));

		JSplitPane jSp = new JSplitPane();
		// JScrollPane s = new JScrollPane(jSp);
		this.listImages = listImages;
		this.image = image;
		this.indexImagenList = listImages.indexOf(image);
		dir = directory;
		this.tp = tp;

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

		// contenedor de botones y puesta en orden de estos
		JPanel panelButtons = new JPanel();

		panelButtons.setLayout(new GridLayout(0, 4));
		panelButtons.add(backBu);
		panelButtons.add(forwardBu);

		if (!onlreadyAlgo) {
			tryAlgoriBu.setText("Try other algorithm");

			addlistenerButton(backBu, forwardBu, tryAlgoriBu);
			panelButtons.add(tryAlgoriBu);
			tp.add(listImages.get(indexImagenList).getDescription(), this);
			tp.setSelectedIndex(tp.indexOfTab(listImages.get(indexImagenList).getDescription()));

		} else {
			addlistenerButton(backBu, forwardBu);
		}

		jSp.setOrientation(SwingConstants.HORIZONTAL);
		jSp.setTopComponent(labelImage);
		jSp.setBottomComponent(panelButtons);
		labelImage.setHorizontalAlignment(JLabel.CENTER);
		labelImage.setVerticalAlignment(JLabel.CENTER);
		jSp.setDividerLocation(900 + jSp.getInsets().top);

		// aniadimos las componentes al jframe
		jSp.setVisible(true);

		add(jSp);
		this.setVisible(true);

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

					tp.setTitleAt(tp.indexOfTab(listImages.get(prevImaIndex).getDescription()),
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
					tp.setTitleAt(tp.indexOfTab(listImages.get(prevImaIndex).getDescription()),
							listImages.get(indexImagenList).getDescription());
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
