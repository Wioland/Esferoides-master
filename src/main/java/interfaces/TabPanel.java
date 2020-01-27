package interfaces;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import esferoides.Methods;
import funtions.ExcelActions;
import funtions.FileFuntions;
import funtions.Utils;

public class TabPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private Map<Integer, Long> excelModificationIndexTab;
	private Map<Integer, File> IndexTabExcel;
	private String dir;
	private boolean originalIma;

	public TabPanel(String directory, boolean selectAlgo) {

		if (selectAlgo) {
			selectAlgorithmImagesTab(directory);
		} else {
			alreadyImageTiffFolderTab(directory);
		}

	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isOriginalIma() {
		return originalIma;
	}

	public void setOriginalIma(boolean originalIma) {
		this.originalIma = originalIma;
	}

	public Map<Integer, Long> getExcelModificationIndexTab() {
		return excelModificationIndexTab;
	}

	public void setExcelModificationIndexTab(Map<Integer, Long> excelModificationIndexTab) {
		this.excelModificationIndexTab = excelModificationIndexTab;
	}

	public Map<Integer, File> getIndexTabExcel() {
		return IndexTabExcel;
	}

	public void setIndexTabExcel(Map<Integer, File> indexTabExcel) {
		IndexTabExcel = indexTabExcel;
	}

	public void alreadyImageTiffFolderTab(String directory) {
		// Buscamos los excels que haya en la carpeta o en sus hijos
		List<String> result = new ArrayList<String>();
		File folder = new File(directory);
		File excel;
		excelModificationIndexTab = new HashMap<Integer, Long>();
		IndexTabExcel = new HashMap<Integer, File>();
		this.dir = directory;
		originalIma = false;

		Utils.searchDirectory(".*\\.xls", folder, result);
		Collections.sort(result);

		// Creamos los paneles, creamos los componentes dentro de estos y aniadimos el
		// nombre a la pestania

		// los de las imagenes

		ShowImages images = new ShowImages(directory, this);

		if (images.getComponents().length == 0) {
			noFileText("Images", null);
			// Comprobar si en la carpeta hay imagenes nd2
			List<String> listImages = new ArrayList<String>();
			Utils.searchDirectory(".*\\.nd2", folder, listImages);
			if (listImages.size() != 0) {
				originalIma = true;
			} else {
				Utils.searchDirectory(".*\\.tif", folder, listImages);
				if (listImages.size() != 0) {
					originalIma = true;
				}
			}
		} else {
			JPanel splitPane = new JPanel(new GridBagLayout());
			LensMEnuButtons lens = new LensMEnuButtons(images.getListImagesPrev());
			JScrollPane s = new JScrollPane(images);
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;

			constraints.gridx = 0;
			constraints.gridy = 0;

			splitPane.add(lens, constraints);

			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.gridx = 0;
			constraints.gridy = 1;
			splitPane.add(s, constraints);

			addTab("Images", splitPane);

		}

		// los del excel

		if (result.size() == 0) {
			noFileText("Excel", null);
		} else {

			for (String path : result) {
				// System.out.println(path);

				excel = new File(path);
				if (excel.exists()) {
					ExcelActions.addExcelPanel(excel, this);

				} else {
					noFileText("Excel", null);
				}

			}

		}

		this.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				TabPanel tab = (TabPanel) e.getSource();

				if (tab.getTitleAt(tab.getSelectedIndex()).contains("Excel")) {
					ExcelActions.checkExcelTab(tab, dir, tab.getSelectedIndex());
				} else {
					if (tab.getTitleAt(tab.getSelectedIndex()).contains("Images")) {
						FileFuntions.isDirectoryContentModify(dir + "predictions", tab);
					}
				}

			}
		});

		FileFuntions.addModificationDirectory(dir + "predictions");
		FileFuntions.imagescheckWithTime(this, 60);

		ExcelActions.excelcheckWithTime(this, dir, 60);

	}

	public void selectAlgorithmImagesTab(String directory) {

		List<String> result = new ArrayList<String>();
		List<String> listExtensions = JMenuPropertiesFile.getExtensions();
		int i = 0;

		// Miramos que tipo de formato contiene la carpeta seleccionada para ello
		// buscamos los tipos de formato que admitimos dentro de esa carpeta hasta que
		// result no sea vacio o no queden tipos de formato con los que comparar

		while (result.isEmpty() && i < listExtensions.size()) {
			Utils.searchDirectory(".*\\." + listExtensions.get(i), new File(directory), result);
			i++;
		}

		if (result.isEmpty()) { // mostramos k en esa carpeta no hay imagenes requeridas y volverr a llamar al
								// mail para seleccionar otra carpeta y realizar las acciones que queramos con
								// ella

			alreadyImageTiffFolderTab(directory);

		} else { // si hay imagens procedemos a realizar la decteccion de los esferoides en ellas
			// Realizamos los metodos de procesado
		new Methods(directory, result);

			// para cada imagen original creamos un showimages mirando cuales de las nuevas
			// imagenes
			// creadas contienen el nombre de la imagen original sin el sufijo
		
			ShowAllAlgorithmImages imaCreated;
			JPanel imagesPanel = new JPanel(new GridLayout(0, 3));

			for (String pathOriginalImage : result) {
				imaCreated = new ShowAllAlgorithmImages(pathOriginalImage, this);
				imagesPanel.add(imaCreated);
			}

			addTab("Images", imagesPanel);
		}

	}

	public void noFileText(String tabName, JViewport jp) {

		JTextArea j = new JTextArea();
		j.setText("There is no such file in this folder");
		j.setEnabled(false);
		//j.enable(false);
		j.setName(tabName);

		JScrollPane s = new JScrollPane(j);
		if (jp == null) {
			if (tabName.equals("Excel")) { // igual hay que cambiarlo por el nombre
				insertTab(tabName, null, s, null, 1);
				excelModificationIndexTab.put(this.indexOfTab(tabName), 0L);

				// int index= indexOfComponent(s);

				// ExcelActions.excelcheckWithTime(this, dir, index, 60);
			} else {
				addTab(tabName, s);
			}
		} else {
			jp.remove(jp.getComponent(0));
			jp.add(j);
			jp.repaint();
		}

	}

}
