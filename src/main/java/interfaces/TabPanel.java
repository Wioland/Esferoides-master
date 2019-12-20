package interfaces;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import funtions.ExcelActions;
import funtions.FileFuntions;
import funtions.Utils;

public class TabPanel extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Long> excelModificationIndexTab;
	private Map<Integer, File> IndexTabExcel;
	private String dir;
	private boolean originalIma;

	public TabPanel(String directory) {

		// Buscamos los excels que haya en la carpeta o en sus hijos
		List<String> result = new ArrayList<String>();
		File folder = new File(directory);
		File excel;
		excelModificationIndexTab = new HashMap<Integer, Long>();
		IndexTabExcel = new HashMap<Integer, File>();
		this.dir = directory;
		originalIma = false;

		Utils.search(".*\\.xls", folder, result);
		Collections.sort(result);

		// Creamos los paneles, creamos los componentes dentro de estos y aniadimos el
		// nombre a la pestania

		// los de las imagenes

		ShowImages images = new ShowImages(directory, this);

		if (images.countComponents() == 0) {
			noFileText("Images", null);
			// Comprobar si en la carpeta hay imagenes nd2
			List<String> listImages = new ArrayList<String>();
			Utils.search(".*\\.nd2", folder, listImages);
			if (listImages.size() != 0) {
				originalIma = true;
			} else {
				Utils.search(".*\\.tif", folder, listImages);
				if (listImages.size() != 0) {
					originalIma = true;
				}
			}
		} else {
			JPanel splitPane = new JPanel(new GridBagLayout()) ;
			LensMEnuButtons lens = new LensMEnuButtons(images.getListImagesPrev());
			JScrollPane s = new JScrollPane(images);
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;

			constraints.gridx = 0;
			constraints.gridy = 0;
			
			splitPane.add(lens,constraints);
			
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.gridx = 0;
			constraints.gridy = 1;
			splitPane.add(s,constraints);

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
					ExcelActions.addExcelPanel(excel,this);

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
					ExcelActions.checkExcelTab(tab, dir,tab.getSelectedIndex()) ;
				}else {
					if (tab.getTitleAt(tab.getSelectedIndex()).contains("Images")) {
						FileFuntions.isDirectoryContentModify(dir, tab);
					}
				}

			}
		});
		
		
		FileFuntions.addModificationDirectory(dir);
		FileFuntions.imagescheckWithTime(this, 60);
		
		ExcelActions.excelcheckWithTime(this, dir, 60);

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

	public void noFileText(String tabName, JViewport jp) {

		JTextArea j = new JTextArea();
		j.setText("There is no such file in this folder");
		j.enable(false);
		j.setName(tabName);

		JScrollPane s = new JScrollPane(j);
		if (jp == null) {
			if (tabName.equals("Excel")) { // igual hay que cambiarlo por el nombre
				insertTab(tabName, null, s, null, 1);
				excelModificationIndexTab.put(this.indexOfTab(tabName), 0L);
				
				//int index= indexOfComponent(s);
				
				//ExcelActions.excelcheckWithTime(this, dir, index, 60); 
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
