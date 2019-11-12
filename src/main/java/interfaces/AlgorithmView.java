package interfaces;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import funtions.CreateListImageAlgori;
import funtions.ShowTiff;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;

public class AlgorithmView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<File> images;
	private JButton selectedBu;
	private List<ImageIcon> imageIcoList;
	private String directory;
	private CreateListImageAlgori cLa;

	public AlgorithmView(File image, String dir) {
		// Parametros ventana

		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Algorithm view selecter");

		cLa = new CreateListImageAlgori(image);

		
		imageIcoList = new ArrayList<ImageIcon>();
		String algoname = "";
		this.directory = dir;

		// crear las imagenes con todos los algoritmos
		images = cLa.createImagesAlgorithms();

		JPanel panelImage = new JPanel(new GridLayout(0, 4));
		JPanel panelButtons = new JPanel(new GridLayout(0, 1));
		panelImage.setAutoscrolls(true);
		JScrollPane s = new JScrollPane(panelImage);
		

		for (File ima : images) {
			JButton imageView = new JButton();
			JLabel imageAlgori = new JLabel();
			JPanel butLab = new JPanel();

			imageIcoList.add(ShowTiff.showTiffToImageIcon(ima.getAbsolutePath()));

			algoname = getAlgorithmName(ima);

			imageAlgori.setText("Used algorithm " + algoname);
			imageView.setIcon(new ImageIcon(ima.getAbsolutePath()));
			imageView.setName(ima.getAbsolutePath());

			imageView.addMouseListener(new MouseAdapter() {

				public void actionPerformed(MouseEvent me) {

					if (!me.isConsumed()) {
						switch (me.getClickCount()) {
						case 1:
							selectedBu = (JButton) me.getSource();
							selectedBu.setName(((JButton) me.getSource()).getName());
							break;
						case 2:
							me.consume();
							ViewImagesBigger vi = new ViewImagesBigger(((JButton) me.getSource()).getIcon(),
									imageIcoList,directory);

							break;

						default:
							break;
						}

					}

				}
			});

			butLab.add(imageView);
			butLab.add(imageAlgori);
			panelImage.add(butLab);

			imageView.setAlignmentX(CENTER_ALIGNMENT);
			imageView.setAlignmentY(CENTER_ALIGNMENT);

		}

		JButton saveImageBt = new JButton();
		JButton modifySelectionBu = new JButton();

		saveImageBt.setText("Save selected image");
		modifySelectionBu.setText("Modify selected image");

		addButtonListener(saveImageBt, modifySelectionBu, panelImage);

		panelButtons.add(saveImageBt);
		panelButtons.add(modifySelectionBu);

		JSplitPane jSp = new JSplitPane();

		jSp.setOrientation(SwingConstants.VERTICAL);
		jSp.setLeftComponent(s);
		jSp.setRightComponent(panelButtons);
		jSp.setDividerLocation(1100 + jSp.getInsets().left);

		// aniadimos las componentes al jframe
		jSp.setVisible(true);
		this.add(jSp);
		this.setVisible(true);

	}

	private void addButtonListener(JButton saveImageBt, JButton modifiSelectionBu, JPanel pIma) {

		saveImageBt.addActionListener(new ActionListener() {
			// si se genera el click guarda la imagen seleccionada
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					SaveImageAndDelete(selectedBu.getName() ); 
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

		modifiSelectionBu.addActionListener(new ActionListener() {
			// si se genera el click se lleva a otra pestaña para modificar la seleccion
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

				 modifySeclection(selectedBu.getName());
		
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

	}

	private String getAlgorithmName(File ima) {

		String[] splitNameIma = ima.getName().split("_");
		String algoritmNameString = splitNameIma[splitNameIma.length - 1].replace(".tiff", "");
		return algoritmNameString;
	}

	private void SaveImageAndDelete(String filePath) {
		File ima = new File(filePath);
		String algoritmNameString = getAlgorithmName(ima);

		cLa.saveSelectedImage(ima, this.directory, algoritmNameString);
	}

	private void modifySeclection(String filename) {
		String fileRoi = filename.replace("tiff", "zip");

		ij.WindowManager.closeAllWindows();
		Opener op = new Opener();
		op.open(filename);

		RoiManager roi = new RoiManager();
		roi.runCommand("Open", fileRoi);

	}

}
