package funtions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ij.measure.ResultsTable;
import interfaces.ExcelTableCreator;
import interfaces.TabPanel;
import task.ExcelTask;

public class ExcelActions {

	private ResultsTable rt;
	private String dir;

	public ExcelActions(ResultsTable rt, String dir) {
		super();
		this.rt = rt;
		this.dir = dir;
	}

	public void convertToExcel() {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Results");
//		HSSFCellStyle style = workbook.createCellStyle();
//		style.setFillBackgroundColor(IndexedColors.ROYAL_BLUE.getIndex());
//		CellStyle style2 = workbook.createCellStyle();
//		style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex()); 

		HSSFRow rowhead = sheet.createRow((short) 0);

		String[] headings = this.rt.getHeadings();
		for (int i = 0; i < headings.length; i++) {
			rowhead.createCell((short) i).setCellValue(headings[i]);
		}

		int rows = this.rt.getCounter();

		HSSFRow row;
		for (int i = 0; i < rows; i++) {
			row = sheet.createRow((short) i + 1);
			String[] rowi = this.rt.getRowAsString(i).split("\\t");
			for (int j = 1; j <= headings.length; j++) {

				row.createCell((short) j - 1).setCellValue(rowi[j]);
			}
		}

		FileOutputStream fileOut;
		try {
			String filename = this.dir + "results.xls";
			if (dir.endsWith("temporal" + File.separator)) {
				filename = this.dir + sheet.getRow(1).getCell(0).getStringCellValue() + "_results.xls";
			}
			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int findRow(HSSFSheet sheet, String cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
						return row.getRowNum();
					}
				}
			}
		}
		return 0;
	}

	public static void changeRow(int rowIndex, HSSFSheet sheet, Row newRow) {

		for (int i = 1; i < sheet.getRow(rowIndex).getLastCellNum(); i++) {
			Cell cell2Update = sheet.getRow(rowIndex).getCell(i);
			cell2Update.setCellValue(newRow.getCell(i).getStringCellValue());

		}
	}

	public static void checkExcelTab(TabPanel tp, String dir, int indexTab) {

		/*
		 * si ya habia un excel mostrandose comprueba si este se ha modificado o si ese
		 * ha desaparecido si no habia excel comprueba si ahora hay excel, se borrar su
		 * componente label y se cambia por el excel ademas se le cambia el nombre a la
		 * pestaña
		 */

		int indexComponent = tp.getSelectedIndex();
		if (indexTab != -1) {
			indexComponent = indexTab;
		}

		File excel = tp.getIndexTabExcel().get(indexComponent);
		List<String> result = new ArrayList<String>();

		File folder = new File(dir);

		Utils.searchDirectory(".*\\.xls", folder, result);
		Collections.sort(result);

		if (excel != null) { // si tiene excel ese tab
			if (excel.exists()) { // si sigue existiendo
				if (!tp.getExcelModificationIndexTab().get(indexComponent).equals(excel.lastModified())) { // si se
					JOptionPane.showMessageDialog(null, "The excel was modified. Updating this tab"); // ha modificado

					JScrollPane sp = (JScrollPane) tp.getSelectedComponent();
					JViewport jP = (JViewport) sp.getComponent(0);
					ExcelTableCreator eTC = (ExcelTableCreator) jP.getComponent(0);
					jP.remove(eTC);
					eTC = new ExcelTableCreator(excel);
					jP.add(eTC);
					jP.repaint();

					tp.getExcelModificationIndexTab().put(indexComponent, excel.lastModified());
				}
			} else {

				JOptionPane.showMessageDialog(null, "The excel that this tab was showing was deleted");
				JScrollPane sp = (JScrollPane) tp.getSelectedComponent();
				JViewport jP = (JViewport) sp.getComponent(0);
				tp.noFileText("Excel", jP);
				tp.setTitleAt(tp.getSelectedIndex(), "Excel ");

				tp.getIndexTabExcel().remove(indexComponent);
				tp.getExcelModificationIndexTab().remove(indexComponent);

				if (tp.getIndexTabExcel().size() != 0) { // se borran las pestañas noFileExcel salvo una
					tp.remove(tp.getSelectedComponent());
					Iterator<Integer> iter = tp.getIndexTabExcel().keySet().iterator();
					while (iter.hasNext() || iter.next() != indexComponent) {

						Integer posTab = (Integer) iter.next();

						if (posTab > indexComponent) {
							tp.getIndexTabExcel().put(posTab - 1, tp.getIndexTabExcel().get(posTab));
							iter.remove();
							tp.getIndexTabExcel().remove(posTab);
						}

					}
				}
			}

		} else { // se comprueba si la carpeta tiene ahora mismo un excel

			if (result.size() != 0) { // si lo tiene

				tp.getExcelModificationIndexTab().remove(indexComponent);
				JOptionPane.showMessageDialog(null, "Detected an excel file");
				for (String string : result) {
					if (string.equals(result.get(0))) { // el primer excel se pone en el noFileExcel
						JScrollPane sp = (JScrollPane) tp.getSelectedComponent();
						JViewport jP = (JViewport) sp.getComponent(0);
						jP.remove(jP.getComponent(0));
						File ex = new File(string);
						ExcelTableCreator eTC = new ExcelTableCreator(ex);
						jP.add(eTC);
						jP.repaint();

						String name = ex.getName();
						tp.getExcelModificationIndexTab().put(tp.getSelectedIndex(), ex.lastModified());
						tp.getIndexTabExcel().put(tp.getSelectedIndex(), ex);
						tp.setTitleAt(tp.getSelectedIndex(), "Excel " + name);

					} else {
						addExcelPanel(new File(string), tp); // para el resto se crean nuevas pestañas
					}
				}

			}

		}

		// se comprueba si se han añadido nuevos excels a la carpeta
		if (result.size() != tp.getIndexTabExcel().size()) {
			for (String string : result) {
				if (!tp.getIndexTabExcel().containsValue(new File(string))) {
					addExcelPanel(new File(string), tp);
				}

			}
		}

	}

	public static void addExcelPanel(File excel, TabPanel tp) {

		String name = excel.getName();
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);
		JScrollPane s = new JScrollPane(excelPanel);

		s.setName("Excel " + name);
		tp.add("Excel " + name, s);

		tp.getExcelModificationIndexTab().put(tp.indexOfTab("Excel " + name), excel.lastModified());
		tp.getIndexTabExcel().put(tp.indexOfTab("Excel " + name), excel);
		tp.setSelectedIndex(tp.indexOfTab("Excel " + name));

		int index = tp.indexOfComponent(s);

		excelcheckWithTime(tp, tp.getDir(), index, 60);

	}

	public static void excelcheckWithTime(TabPanel tp, String directory, int indexTab, int secons) {
		ExcelTask exTask = new ExcelTask(tp, directory, indexTab);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(exTask, 0, 1000 * secons);
	}
}
