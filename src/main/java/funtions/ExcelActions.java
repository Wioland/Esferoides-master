package funtions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Timer;

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
				String name = sheet.getRow(1).getCell(0).getStringCellValue();
				filename = this.dir + name + "_results.xls";
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

	public static void checkAllExcelTab(TabPanel tp, String dir) {

		/*
		 * si ya habia un excel mostrandose comprueba si este se ha modificado o si ese
		 * ha desaparecido si no habia excel comprueba si ahora hay excel, se borrar su
		 * componente label y se cambia por el excel ademas se le cambia el nombre a la
		 * pestaña
		 */

		List<Integer> lAux = new ArrayList<Integer>();
		for (Integer tbIndex : tp.getIndexTabExcel().keySet()) {
			lAux.add(tbIndex);
		}

		for (Integer tbIndex : lAux) {
			checkExcelTab(tp, dir, tbIndex);
		}
		
		List<String> result = new ArrayList<String>();

		File folder = new File(dir);

		Utils.searchDirectory(".*\\.xls", folder, result);
		Collections.sort(result);
		
		
		addedExcelToTheTab(result, tp);
	}

	public static void checkExcelTab(TabPanel tp, String dir, int index) {

		if (tp.getIndexTabExcel().containsKey(index)) {
			File excel = tp.getIndexTabExcel().get(index);

			List<String> result = new ArrayList<String>();

			File folder = new File(dir);

			Utils.searchDirectory(".*\\.xls", folder, result);
			Collections.sort(result);

			if (excel != null) { // si tiene excel ese tab
				if (excel.exists()) { // si sigue existiendo

					Long modTab = tp.getExcelModificationIndexTab().get(index);
					Long excelMod = excel.lastModified();

					if (!modTab.equals(excelMod)) { // si se ha
													// modificado

						JOptionPane.showMessageDialog(null, "The excel "+excel.getName() +" was modified. Updating it´s tab");

						JScrollPane sp = (JScrollPane) tp.getComponent(index);
						JViewport jP = (JViewport) sp.getComponent(0);
						ExcelTableCreator eTC = (ExcelTableCreator) jP.getComponent(0);
						jP.remove(eTC);
						eTC = new ExcelTableCreator(excel);
						jP.add(eTC);
						jP.repaint();

						tp.getExcelModificationIndexTab().put(index, excel.lastModified());
					}
				} else { // si ya no existe

					JOptionPane.showMessageDialog(null, "The excel " + excel.getName() + " was deleted");

					JScrollPane sp = (JScrollPane) tp.getComponentAt(index);
					JViewport jP = (JViewport) sp.getComponent(0);

					if (tp.getIndexTabExcel().size() == 1) { // si solo queda un tab de excel entonces que se quede ese
																// tab como no file
						tp.noFileText("Excel", jP);
						tp.setTitleAt(1, "Excel ");

						tp.getIndexTabExcel().remove(1);
						tp.getExcelModificationIndexTab().remove(1);

					} else {
						tp.remove(index);
						Set<Integer> list = tp.getIndexTabExcel().keySet();
						List<Integer> auxList = new ArrayList<Integer>();
						for (Integer integer : list) {
							if (integer > index) {
								auxList.add(integer);

							}
						}

						for (Integer integer : auxList) {
							tp.getIndexTabExcel().put(integer - 1, tp.getIndexTabExcel().get(integer));
							tp.getExcelModificationIndexTab().put(integer - 1,
									tp.getExcelModificationIndexTab().get(integer));

							tp.getIndexTabExcel().remove(integer);
							tp.getExcelModificationIndexTab().remove(integer);
						}

					}

				}

			} else { // si ese tab no tiene un excel

				addedExcelToTheTab(result, tp);

			}
		}

	}

	public static void addedExcelToTheTab(List<String> result, TabPanel tp) {

//		int resultSiz = result.size();
//		int tpSize = tp.getIndexTabExcel().size();
		if (result.size() > tp.getIndexTabExcel().size()) { // si lo tiene y no esta pintado

			JOptionPane.showMessageDialog(null, "Detected an excel file");
			boolean firstCheck = false;
			if (!tp.getComponent(1).getClass().equals(JScrollPane.class)) {
				firstCheck = true;
			}

			for (String path : result) {
				if (!tp.getIndexTabExcel().containsValue(new File(path))) {
					if (!firstCheck) {
						JScrollPane sp = (JScrollPane) tp.getComponent(1);
						JViewport jP = (JViewport) sp.getComponent(0);
						jP.remove(jP.getComponent(0));

						File ex = new File(path);
						ExcelTableCreator eTC = new ExcelTableCreator(ex);

						jP.add(eTC);
						jP.repaint();

						String name = ex.getName();
						tp.getExcelModificationIndexTab().put(tp.getSelectedIndex(), ex.lastModified());
						tp.getIndexTabExcel().put(tp.getSelectedIndex(), ex);
						tp.setTitleAt(tp.getSelectedIndex(), "Excel " + name);

						firstCheck = true;
					} else {
						addExcelPanel(new File(path), tp); // para el resto se crean nuevas pestañas
					}
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

		//int index = tp.indexOfComponent(s);

		// excelcheckWithTime(tp, tp.getDir(), index, 60);

	}

	public static void excelcheckWithTime(TabPanel tp, String directory, int secons) {
		ExcelTask exTask = new ExcelTask(tp, directory);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(exTask, 0, 1000 * secons);
	}
}
