package interfaces;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import funtions.Utils;

public class TabPanel extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Long> excelModificationIndexTab;
	private Map<Integer, File> IndexTabExcel;
	private String dir;
	private boolean nd2Ima;

	public TabPanel(String directory) {

		// Buscamos los excels que haya en la carpeta o en sus hijos
		List<String> result = new ArrayList<String>();
		File folder = new File(directory);
		File excel;
		excelModificationIndexTab = new HashMap<Integer, Long>();
		IndexTabExcel = new HashMap<Integer, File>();
		this.dir = directory;
		nd2Ima = false;

		Utils.search(".*\\.xls", folder, result);
		Collections.sort(result);

		// Creamos los paneles, creamos los componentes dentro de estos y aniadimos el
		// nombre a la pestania

		// los de las imagenes

		ShowImages images = new ShowImages(directory, this);
		
		if (images.countComponents() == 0) {
			noFileText("Images");
			// Comprobar si en la carpeta hay imagenes nd2
			List<String> listImages = new ArrayList<String>();
			Utils.search(".*\\.nd2", folder, listImages);
			if (listImages != null) {
				nd2Ima = true;
			}
		} else {
			JSplitPane splitPane= new JSplitPane(HORIZONTAL);
			LensMEnuButtons lens= new LensMEnuButtons(images.getListImagesPrev());
			JScrollPane s= new JScrollPane(images);
			
			splitPane.setTopComponent(lens);
			splitPane.setBottomComponent(s);
			
			addTab("Images", splitPane);
		}

		// los del excel

		if (result.size() == 0) {
			noFileText("Excel");
		} else {

			for (String path : result) {
				// System.out.println(path);

				excel = new File(path);
				if (excel.exists()) {
					addExcelPanel(excel);

				} else {
					noFileText("Excel");
				}

			}

		}
	}

	public boolean isNd2Ima() {
		return nd2Ima;
	}

	public void setNd2Ima(boolean nd2Ima) {
		this.nd2Ima = nd2Ima;
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
		j.setName(tabName);

		if (tabName.equals("Excel")) { // igual hay que cambiarlo por el nombre
			insertTab(tabName, null, j, null, 1);
			excelModificationIndexTab.put(this.indexOfTab(tabName), 0L);
			addListenersPanelExcel(j);
		} else {
			addTab(tabName, j);
		}

	}

	private void addExcelPanel(File excel) {
		JPanel panelExcel = new JPanel();
		String name = excel.getName();
		// System.out.println(name);
		excelPanelContent(panelExcel, excel);
		panelExcel.setName("Excel " + name);

		JScrollPane s= new JScrollPane(panelExcel);
		
		insertTab("Excel " + name, null, s, null, 1);
		excelModificationIndexTab.put(this.indexOfTab("Excel " + name), excel.lastModified());
		IndexTabExcel.put(this.indexOfTab("Excel " + name), excel);
		addListenersPanelExcel(panelExcel);

	}

	private void checkExcelTab(MouseEvent e) {

		/*
		 * si ya habia un excel mostrandose comprueba si este se ha modificado o si ese
		 * ha desaparecido si no habia excel comprueba si ahora hay excel, se borrar su
		 * componente label y se cambia por el excel ademas se le cambia el nombre a la
		 * pestaña
		 */

		int indexComponent = ((TabPanel) e.getComponent().getParent().getParent().getParent()).indexOfTab(e.getComponent().getName());
		File excel = IndexTabExcel.get(indexComponent);

		if (excel != null) { // si tiene excel
			if (excel.exists()) { // si sigue existiendo
				if (!excelModificationIndexTab.get(indexComponent).equals(excel.lastModified())) { // si se
					JOptionPane.showMessageDialog(null, "The excel was modified. Updating this tab"); // ha modificado
					JPanel jP = (JPanel) e.getComponent();
					ExcelTableCreator eTC = (ExcelTableCreator) jP.getComponent(0);
					jP.remove(eTC);
					eTC = new ExcelTableCreator(excel);
					jP.add(eTC);
					jP.repaint();
					excelModificationIndexTab.put(indexComponent, excel.lastModified());
				}
			} else {
				remove(e.getComponent());
				JOptionPane.showMessageDialog(null, "The excel that this tab was showing was deleted");
				noFileText("Excel");
				IndexTabExcel.remove(indexComponent);
				excelModificationIndexTab.remove(indexComponent);
			}

		} else { // se comprueba si la carpeta tiene ahora mismo un excel
			File folder = new File(dir);
			List<String> result = new ArrayList<String>();

			Utils.search(".*\\.xls", folder, result);
			Collections.sort(result);
			if (result.size() != 0) { // si lo tiene
				remove(e.getComponent());
				excelModificationIndexTab.remove(indexComponent);
				JOptionPane.showMessageDialog(null, "Detected an excel file");
				for (String string : result) {
					addExcelPanel(new File(string));
				}

			}

		}

	}

	private void addListenersPanelExcel(Component component) {

		component.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				checkExcelTab(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

}
