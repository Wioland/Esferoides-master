package interfaces;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
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
import funtions.ShowTiff;

public class AlgorithmView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> images;
	private static JButton selectedBu;
	private static List<ImageIcon> imageIcoList;
	private static String directory;
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
				if (getOpenWindows() != null) { // quedan las de visualizacion de las imagenes en grande
					if (getOpenWindows().size() > 0) {
						for (ViewImagesBigger wind : getOpenWindows()) {
							// wind.dispose();
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

		setOpenWindows(new ArrayList<ViewImagesBigger>());

		OurProgressBar pb = new OurProgressBar(this);

		cLa = new CreateListImageAlgori(image);

		setImageIcoList(new ArrayList<ImageIcon>());
		this.setDirectory(dir);

		// crear las imagenes con todos los algoritmos
		cLa.createImagesAlgorithms();

		JPanel panelButtons = new JPanel(new GridLayout(0, 1));
		
		ShowImages panelImage= new ShowImages(dir+"temporal", this);
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



	private void addButtonListener(JButton saveImageBt, JButton modifiSelectionBu, JPanel pIma) {

		saveImageBt.addActionListener(new ActionListener() {
			// si se genera el click guarda la imagen seleccionada
			public void actionPerformed(ActionEvent e) {
				if (getSelectedBu() != null) {

					SaveImageAndDelete(getSelectedBu().getName());
				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

		modifiSelectionBu.addActionListener(new ActionListener() {
			// si se genera el click se lleva a otra pestaña para modificar la seleccion
			public void actionPerformed(ActionEvent e) {
				if (getSelectedBu() != null) {
					JButton h = getSelectedBu();
		
					modifySeclection(getSelectedBu().getName());

				} else {
					JOptionPane.showMessageDialog(pIma, "Not image selected", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			}

		});

	}

	private void SaveImageAndDelete(String filePath) {
		File ima = new File(filePath);
		FileFuntions.saveSelectedImage(ima, this.getDirectory());
		this.dispose();
		FileFuntions.deleteTemporalFolder(new File(this.getDirectory() + "temporal"));
	}

	private void modifySeclection(String filename) {
		String fileRoi = filename.replace("_pred.tiff", ".zip");

		String nd2Path = RoiFuntions.getNd2FilePathFromTempralTiff(filename);

		ij.WindowManager.closeAllWindows();

		RoiFuntions.showNd2FilePlusRoi(nd2Path, fileRoi);

	}



	public static JButton getSelectedBu() {
		return selectedBu;
	}



	public static void setSelectedBu(JButton selectedBu) {
		AlgorithmView.selectedBu = selectedBu;
	}



	public static String getDirectory() {
		return directory;
	}



	public static void setDirectory(String directory) {
		AlgorithmView.directory = directory;
	}



	public static List<ImageIcon> getImageIcoList() {
		return imageIcoList;
	}



	public static void setImageIcoList(List<ImageIcon> imageIcoList) {
		AlgorithmView.imageIcoList = imageIcoList;
	}



	public static List<ViewImagesBigger> getOpenWindows() {
		return openWindows;
	}



	public static void setOpenWindows(List<ViewImagesBigger> openWindows) {
		AlgorithmView.openWindows = openWindows;
	}

}
