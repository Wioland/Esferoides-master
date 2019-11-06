package interfaces;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
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

import funtions.CreateListImageAlgori;

public class AlgorithmView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<File> images;

	public AlgorithmView(File image) {
		// Parametros ventana

		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Algorithm view selecter");

		images = new ArrayList<File>();
		String algoname = "";

		 // crear las imagenes con todos los algoritmos
		CreateListImageAlgori cRi= new CreateListImageAlgori(image);
		images=cRi.createImagesAlgorithms();
		
		

		JPanel panelImage = new JPanel();
		JPanel panelButtons = new JPanel();
		panelImage.setAutoscrolls(true);
		JScrollPane s = new JScrollPane(panelButtons);

		for (File ima : images) {
			JLabel imageView = new JLabel();
			JLabel imageAlgori = new JLabel();

			algoname = ima.getName();// esto seguramente modificar tener en cuenta que al guardar las imagenes hay
										// que poer el nombre + nel algoritmo utilizado en ese momento de ahi pillar
										// para aqui

			imageAlgori.setText("Used algorithm " + algoname);
			imageView.setIcon(new ImageIcon(ima.getAbsolutePath()));
			panelImage.add(imageView);
			panelImage.add(imageAlgori);

			imageView.setAlignmentX(CENTER_ALIGNMENT);
			imageView.setAlignmentY(CENTER_ALIGNMENT);

		}

		JButton saveImageBt = new JButton();
		JButton modifySelectionBu = new JButton();
		JButton viewBiggerBt = new JButton();

		saveImageBt.setText("Save selected image");
		modifySelectionBu.setText("Modify selected image");
		viewBiggerBt.setText("View bigger selected image");

		addButtonListener(saveImageBt, modifySelectionBu, viewBiggerBt);

		panelButtons.add(saveImageBt);
		panelButtons.add(modifySelectionBu);
		panelButtons.add(viewBiggerBt);

		JSplitPane jSp = new JSplitPane();

		jSp.setOrientation(SwingConstants.VERTICAL);
		jSp.setLeftComponent(panelImage);
		jSp.setRightComponent(s);

		// aniadimos las componentes al jframe
		jSp.setVisible(true);
		this.add(jSp);
		this.setVisible(true);

	}



	private void addButtonListener(JButton saveImageBt, JButton modifiSelectionBu, JButton viewBiggerBt) {

		saveImageBt.addActionListener(new ActionListener() {
			// si se genera el click guarda la imagen seleccionada
			public void actionPerformed(ActionEvent e) {
				SaveImageAndDelete();
			}

		});

		modifiSelectionBu.addActionListener(new ActionListener() {
			// si se genera el click se lleva a otra pestaña para modificar la seleccion
			public void actionPerformed(ActionEvent e) {
				modifySeclection(); // dejar que se habra con imagej
			}

		});

		viewBiggerBt.addActionListener(new ActionListener() {
			// si se genera el click se lleva a otra pestaña para ver la imagen mas grande
			public void actionPerformed(ActionEvent e) {
			//	ViewImagesBigger viewImageBig = new ViewImagesBigger(label.getIcon(), listImages); // igualmenjor que se
																									// habra con imagej

			}

		});

	}

	private void SaveImageAndDelete() {
		// TODO Auto-generated method stub

	}

	private void modifySeclection() {
		// TODO Auto-generated method stub

	}

}
