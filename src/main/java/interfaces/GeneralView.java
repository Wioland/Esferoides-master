
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
		String fileName= "results.xls";
		String path=this.directory + "\\" + fileName;
		excel= new File(path);
		

		// Parametros ventana
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Results");

		// Creamos el conjunto de pestañas
		JTabbedPane tabPanel = new JTabbedPane();

		// Creamos los paneles
		JPanel panelExcel = new JPanel();
		JPanel panelImageTree = new JPanel();

		// Componentes de los paneles
		excelPanelContent(panelExcel,excel);
		imageTreePanelContent(panelImageTree);

		// Añadimos un nombre de la pestaña y el panel
		tabPanel.addTab("Excel", panelExcel);
		tabPanel.addTab("Images", panelImageTree);

		getContentPane().add(tabPanel);
		
		setVisible(true);
	}

	private void imageTreePanelContent(JPanel panelImageTree) {
		ImageTreePanel imageTree= new ImageTreePanel(directory);
		panelImageTree.add(imageTree);
		panelImageTree.setVisible(true);

	}

	private void excelPanelContent(JPanel panelExcel, File excel) {
		ExcelTableCreator excelPanel= new ExcelTableCreator(excel);
		panelExcel.add(excelPanel);
		panelExcel.setVisible(true);

	}

	
	
	
	
	
	
	
	public static void main(String[] args) {

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");

		GeneralView ventana = new GeneralView(dc.getDirectory());

	}
}
