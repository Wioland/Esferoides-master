package interfaces;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
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

	public AlgorithmView(File image) {
		// Parametros ventana

		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Algorithm view selecter");

		images = new ArrayList<File>();
		imageIcoList = new ArrayList<ImageIcon>();
		String algoname = "";

		// crear las imagenes con todos los algoritmos
		CreateListImageAlgori cRi = new CreateListImageAlgori(image);
		images = cRi.createImagesAlgorithms();

		JPanel panelImage = new JPanel();
		JPanel panelButtons = new JPanel();
		panelImage.setAutoscrolls(true);
		JScrollPane s = new JScrollPane(panelButtons);

		for (File ima : images) {
			JButton imageView = new JButton();
			JLabel imageAlgori = new JLabel();

			imageIcoList.add(new ImageIcon(ima.getAbsolutePath()));

			algoname = ima.getName();// esto seguramente modificar tener en cuenta que al guardar las imagenes hay
										// que poer el nombre + nel algoritmo utilizado en ese momento de ahi pillar
										// para aqui

			imageAlgori.setText("Used algorithm " + algoname);
			imageView.setIcon(new ImageIcon(ima.getAbsolutePath()));

			imageView.addMouseListener(new MouseAdapter() {

				public void actionPerformed(MouseEvent me) {

					if (!me.isConsumed()) {
						switch (me.getClickCount()) {
						case 1:
							selectedBu = (JButton) me.getSource();
							break;
						case 2:
							me.consume();
							ViewImagesBigger vi = new ViewImagesBigger(((JButton) me.getSource()).getIcon(),
									imageIcoList);

							break;

						default:
							break;
						}

					}

				}
			});

			panelImage.add(imageView);
			panelImage.add(imageAlgori);

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
		jSp.setLeftComponent(panelImage);
		jSp.setRightComponent(s);

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

					SaveImageAndDelete();
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

		modifiSelectionBu.addActionListener(new ActionListener() {
			// si se genera el click se lleva a otra pestaña para modificar la seleccion
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					modifySeclection(selectedBu); // dejar que se habra con imagej
					// pasarle la imagen original coger y mirar el index de este elemento en su
					// lista y coger el indez de la otra lista
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

				
			}

		});

	

	}

	private void SaveImageAndDelete() {
		// TODO Auto-generated method stub

	}

	private void modifySeclection(String filename, String fileRoi) {

		ij.WindowManager.closeAllWindows();
		Opener op = new Opener();
		op.open(filename);

		RoiManager roi = new RoiManager();
		roi.runCommand("Open", fileRoi);

	}

}
