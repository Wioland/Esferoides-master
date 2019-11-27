package interfaces;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import funtions.CreateListImageAlgori;
import funtions.FileFuntions;
import funtions.RoiFuntions;

public class AlgorithmView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton selectedBu;
	private List<ImageIcon> imageIcoList;
	private String directory;
	private CreateListImageAlgori cLa;
	private static List<ViewImagesBigger> openWindows;

	public AlgorithmView(File image, String dir) {
		// Parametros ventana

		setExtendedState(MAXIMIZED_BOTH);
		setTitle("Algorithm view selecter");
		this.setVisible(true);
		setMinimumSize(new Dimension(1000, 300));

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				File folder = CreateListImageAlgori.getTemporalFolder();
				if (folder != null) {
					folder.delete();
				}

				// Cerrar el resto de ventanas que se hayan abierto a partir de esta
				ij.WindowManager.closeAllWindows(); // esto cierra todas las ventanas abiertas con imagej solamente
				if (openWindows != null) { // quedan las de visualizacion de las imagenes en grande
					if (openWindows.size() > 0) {
						for (ViewImagesBigger wind : openWindows) {
							 //wind.disable();
						}
					}
				}

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});

		openWindows = new ArrayList<ViewImagesBigger>();

		OurProgressBar pb = new OurProgressBar(this);

		cLa = new CreateListImageAlgori(image);

		imageIcoList = new ArrayList<ImageIcon>();
		this.directory = dir;

		// crear las imagenes con todos los algoritmos
		cLa.createImagesAlgorithms();

		JPanel panelButtons = new JPanel(new GridLayout(0, 1));

		ShowImages panelImage = new ShowImages(dir + "temporal", this);
		panelImage.setAutoscrolls(true);

		JButton saveImageBt = new JButton();
		JButton modifySelectionBu = new JButton();

		saveImageBt.setText("Save selected image");
		modifySelectionBu.setText("Modify selected image");

		addButtonListener(saveImageBt, modifySelectionBu, panelImage);

		panelButtons.add(saveImageBt);
		panelButtons.add(modifySelectionBu);
		JScrollPane s = new JScrollPane(panelImage);
		JSplitPane jSp = new JSplitPane();

		jSp.setOrientation(SwingConstants.VERTICAL);
		jSp.setLeftComponent(s);
		jSp.setRightComponent(panelButtons);
		// jSp.setDividerLocation(1100 + jSp.getInsets().left);

		// aniadimos las componentes al jframe
		pb.setVisible(false);
		pb.dispose();
		jSp.setVisible(true);
		getContentPane().add(jSp);

		pack();

	}

	public void mouseClick(MouseEvent me, ImageIcon imageIcon) {
		if (!me.isConsumed()) {
			switch (me.getClickCount()) {
			case 1:
				selectedBu = (JButton) me.getSource();
				selectedBu.setName(((JButton) me.getSource()).getName());
				break;
			case 2:
				me.consume();
				ViewImagesBigger vi = new ViewImagesBigger(imageIcon, imageIcoList, directory, true, null);
				openWindows.add(vi);
				break;

			default:
				break;
			}

		}

	}

	private void addButtonListener(JButton saveImageBt, JButton modifiSelectionBu, JPanel pIma) {

		saveImageBt.addActionListener(new ActionListener() {
			// si se genera el click guarda la imagen seleccionada
			public void actionPerformed(ActionEvent e) {
				if (selectedBu != null) {

					SaveImageAndDelete(selectedBu.getName());
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

	private void SaveImageAndDelete(String filePath) {
		File ima = new File(filePath);
		FileFuntions.saveSelectedImage(ima, this.directory + "predictions");
		FileFuntions.deleteTemporalFolder(new File(this.directory + "temporal"));
		this.dispose();
	}

	private void modifySeclection(String filename) {
		String fileRoi = filename.replace("_pred.tiff", ".zip");

		String nd2Path = RoiFuntions.getNd2FilePathFromTempralTiff(filename);

		ij.WindowManager.closeAllWindows();

		RoiFuntions.showNd2FilePlusRoi(nd2Path, fileRoi);

	}

}
