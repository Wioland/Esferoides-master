package interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import funtions.Utils;

public class TabPanel extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TabPanel(String directory) {

		// Buscamos los excels que haya en la carpeta o en sus hijos
		List<String> result = new ArrayList<String>();
		File folder = new File(directory);
		File excel;

		Utils.search(".*\\.xls", folder, result);
		Collections.sort(result);

		// Creamos los paneles, creamos los componentes dentro de estos y aniadimos el
		// nombre a la pestania

		// los del excel

		if (result.size() == 0) {
			noFileText("Excel");
		} else {

			for (String path : result) {
				// System.out.println(path);
				JPanel panelExcel = new JPanel();
				excel = new File(path);
				if (excel.exists()) {
					excelPanelContent(panelExcel, excel);

				} else {
					JLabel noFileLb = new JLabel();
					noFileLb.setText("There is no such file in this folder");
					panelExcel.add(noFileLb);
				}

//				String[] j = path.split("\\\\");
//				String name = j[j.length - 2] + "\\" + j[j.length - 1];

				// System.out.println(name);
//				addTab("Excel " + name, panelExcel);

			}

		}

		// los de las imagenes

		ShowImages images = new ShowImages(directory);
		if (images.countComponents() == 0) {
			noFileText("Images");
		} else {
			addTab("Images", images);
		}


	}

	/*
	 * Funcion que aniade la vista del excel
	 */
	private void excelPanelContent(JPanel panelExcel, File excel) {
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);

		panelExcel.add(excelPanel);
		panelExcel.setVisible(true);

	}

	private void noFileText(String tabName) {
		JTextArea j = new JTextArea();
		j.setText("There is no such file in this folder");
		j.enable(false);
		addTab(tabName, j);

	}

}
