
package interfaces;

import java.io.File;

import javax.swing.*;

import ij.io.DirectoryChooser;

public class GeneralView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String directory;
	private File excel;

	public GeneralView(String directory) {

		this.directory = directory;
		String fileName = "results.xls";
		String path = this.directory + "\\" + fileName;
		excel = new File(path);

		// Parametros ventana
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Results");

		// Creamos el conjunto de pestañas
		JTabbedPane tabPanel = new JTabbedPane();

		// Creamos los paneles, creamos los componentes dentro de estos y aniadimos el
		// nombre a la pestania

		// los del excel
		JPanel panelExcel = new JPanel();

		if (excel.exists()) {
			excelPanelContent(panelExcel, excel);
		}else {
			JLabel noFileLb= new JLabel();
			noFileLb.setText("There is no such file in this folder");
			panelExcel.add(noFileLb);
		}
		tabPanel.addTab("Excel", panelExcel);

		// los de las imagenes y el arbol
		JPanel panelImageTree = new JPanel();
		imageTreePanelContent(panelImageTree);
		tabPanel.addTab("Images", panelImageTree);

		// Añadimosel panel

		getContentPane().add(tabPanel);
		setVisible(true);
	}

	/*
	 * Funcion que crea y aniade la vista de imagenes y el arbol de directorios
	 */
	private void imageTreePanelContent(JPanel panelImageTree) {
		ImageTreePanel imageTree = new ImageTreePanel(directory);
		panelImageTree.add(imageTree);
		panelImageTree.setVisible(true);

	}

	/*
	 * Funcion que aniade la vista del excel
	 */
	private void excelPanelContent(JPanel panelExcel, File excel) {
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);
		panelExcel.add(excelPanel);
		panelExcel.setVisible(true);

	}

	// PRUEBAS

	public static void main(String[] args) {

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");

		GeneralView ventana = new GeneralView(dc.getDirectory());

	}
}
