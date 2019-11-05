
package interfaces;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;

import esferoides.Utils;
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

		List<String> result = new ArrayList<String>();
		File folder = new File(directory);
		Utils.search(".*\\.xls", folder, result);
		Collections.sort(result);

		// System.out.println(result.get(0));
		// String fileName = "results.xls";
		// String path = this.directory + "\\" + fileName;

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

		for (String path : result) {
			// System.out.println(path);
			JPanel panelExcel = new JPanel();
			excel = new File(path);
			if (excel.exists()) {
				excelPanelContent(panelExcel, excel);
				panelExcel.setBackground(Color.blue);
			} else {
				JLabel noFileLb = new JLabel();
				noFileLb.setText("There is no such file in this folder");
				panelExcel.add(noFileLb);
			}

			String[] j = path.split("\\\\");
			String name = j[j.length - 2] + "\\" + j[j.length - 1];

			// System.out.println(name);
			tabPanel.addTab("Excel " + name, panelExcel);

		}

		// los de las imagenes y el arbol
		JPanel panelImageTree = new JPanel();
		imageTreePanelContent(tabPanel);

		// Añadimosel panel

		getContentPane().add(tabPanel);
		setVisible(true);
	}

	/*
	 * Funcion que crea y aniade la vista de imagenes y el arbol de directorios
	 */
	private void imageTreePanelContent(JTabbedPane panelImageTree) {
		ImageTreePanel imageTree = new ImageTreePanel(directory);

		panelImageTree.addTab("Images", imageTree);
		// .add(imageTree);
		// panelImageTree.setVisible(true);

	}

	/*
	 * Funcion que aniade la vista del excel
	 */
	private void excelPanelContent(JPanel panelExcel, File excel) {
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);

		JButton buttonSave = new JButton("Save");

		panelExcel.add(excelPanel);
		panelExcel.add(buttonSave);
		panelExcel.setVisible(true);

	}

	// PRUEBAS

	public static void main(String[] args) {

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");

		GeneralView ventana = new GeneralView(dc.getDirectory());

	}
}
