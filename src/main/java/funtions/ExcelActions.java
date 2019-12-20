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

	/**
	 * Converts the resulsTable to an excel file
	 */
	public void convertToExcel() {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Results");
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

			// in case the excel is from the temporal images we change the name in order to
			// identify the excel with the image
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

	/**
	 * Method that find the row of an object in an excel file based on the name it
	 * has on the first column
	 * 
	 * @param sheet       sheet of the excel where you wanted to find the object
	 * @param cellContent id of the object in the excel
	 * @return the row number of the object or -1 if the object isn't in the excel
	 *         sheet
	 */

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
		return -1;
	}

	/**
	 * Changes the content of the row all Except from the first column with contains
	 * the id
	 * 
	 * @param rowIndex the excel row we are going to change
	 * @param sheet    the sheet of the excel in witch we are going to change a row
	 * @param newRow   a row with the new content
	 */
	public static void changeRow(int rowIndex, HSSFSheet sheet, Row newRow) {

		for (int i = 1; i < sheet.getRow(rowIndex).getLastCellNum(); i++) {
			Cell cell2Update = sheet.getRow(rowIndex).getCell(i);
			cell2Update.setCellValue(newRow.getCell(i).getStringCellValue());

		}
	}

	/**
	 * If an excel is been shown in the tabPanel, it checks if the excel has been
	 * modified or deleted. If there weren't excels checks if now there are and add
	 * them.
	 * 
	 * @param tp  The tabPanel that contains the excels tabs
	 * @param dir the directory where the excel is
	 */
	public static void checkAllExcelTab(TabPanel tp, String dir) {

		List<Integer> lAux = new ArrayList<Integer>();
		for (Integer tbIndex : tp.getIndexTabExcel().keySet()) {
			lAux.add(tbIndex);
		}

		// for all the excel tabs checks id they have changed
		for (Integer tbIndex : lAux) {
			checkExcelTab(tp, dir, tbIndex);
		}

		List<String> result = new ArrayList<String>();
		File folder = new File(dir);

		Utils.searchDirectory(".*\\.xls", folder, result);
		Collections.sort(result);

		// checks if the are new excels
		addedExcelToTheTab(result, tp);
	}

	/**
	 * Checks is the excel of it tab have change or if an
	 * 
	 * @param tp    TabPanel that contains the excel tab
	 * @param dir   The excel directory
	 * @param index The tab index to check
	 */
	public static void checkExcelTab(TabPanel tp, String dir, int index) {

		if (tp.getIndexTabExcel().containsKey(index)) { // if this index is an excel tab

			File excel = tp.getIndexTabExcel().get(index);
			List<String> result = new ArrayList<String>();
			File folder = new File(dir);

			Utils.searchDirectory(".*\\.xls", folder, result);
			Collections.sort(result);

			if (excel != null) { // if it tab has an excel it tab isn't a noFileTab
				if (excel.exists()) { // if the excel file still exist in the folder

					Long modTab = tp.getExcelModificationIndexTab().get(index);
					Long excelMod = excel.lastModified();

					if (!modTab.equals(excelMod)) { // if it hasn't been modified

						JOptionPane.showMessageDialog(null,
								"The excel " + excel.getName() + " was modified. Updating it´s tab");

						JScrollPane sp = (JScrollPane) tp.getComponent(index);
						JViewport jP = (JViewport) sp.getComponent(0);
						ExcelTableCreator eTC = (ExcelTableCreator) jP.getComponent(0);
						jP.remove(eTC);
						eTC = new ExcelTableCreator(excel);
						jP.add(eTC);
						jP.repaint();

						tp.getExcelModificationIndexTab().put(index, excel.lastModified());
					}
				} else { // if the excel no longer exist

					deleteExcelTab(excel, index, tp);
					excel = tp.getIndexTabExcel().get(index);

					boolean exist = excel.exists();
					boolean existindex = tp.getIndexTabExcel().containsKey(index);

					while (!exist && existindex) { // checks if the next index has an existing excel
						deleteExcelTab(excel, index, tp);
						excel = tp.getIndexTabExcel().get(index);
						exist = excel.exists();
						existindex = tp.getIndexTabExcel().containsKey(index);
					}
				}

			} else { // if the tab doesn't have an excel checks if there are new excels

				addedExcelToTheTab(result, tp);

			}
		}

	}

	/**
	 * If the excel has been deleted we delete the tab in the tabPane if the tab is
	 * the last having an excel in the tabPanel we doesn't delete it and we
	 * transform it in a noFile tab
	 * 
	 * @param excel the excel file
	 * @param index the index of the tab in the tabPane
	 * @param tp    The tabPanel
	 */
	public static void deleteExcelTab(File excel, int index, TabPanel tp) {

		JOptionPane.showMessageDialog(null, "The excel " + excel.getName() + " was deleted");

		JScrollPane sp = (JScrollPane) tp.getComponentAt(index);
		JViewport jP = (JViewport) sp.getComponent(0);

		if (tp.getIndexTabExcel().size() == 1) { // if it is the las excel tab we transform it to a noFileTab

			tp.noFileText("Excel", jP);
			tp.setTitleAt(1, "Excel ");

			tp.getIndexTabExcel().remove(1);
			tp.getExcelModificationIndexTab().remove(1);

		} else {
			tp.remove(index); // we remove it
			Set<Integer> list = tp.getIndexTabExcel().keySet();
			List<Integer> auxList = new ArrayList<Integer>();
			for (Integer integer : list) { // we look for the excels tabs that follow it
				if (integer > index) {
					auxList.add(integer);

				}
			}

			// we change the index of the after excel tabs in -1
			for (Integer integer : auxList) {
				tp.getIndexTabExcel().put(integer - 1, tp.getIndexTabExcel().get(integer));
				tp.getExcelModificationIndexTab().put(integer - 1, tp.getExcelModificationIndexTab().get(integer));

				tp.getIndexTabExcel().remove(integer);
				tp.getExcelModificationIndexTab().remove(integer);
			}

		}

	}

	/**
	 * add an new excel to the tabPanel When a new excel appear in the directory
	 * 
	 * @param result list of excel paths
	 * @param tp     The tabPanel
	 */
	public static void addedExcelToTheTab(List<String> result, TabPanel tp) {

//		int resultSiz = result.size();
//		int tpSize = tp.getIndexTabExcel().size();
		if (result.size() > tp.getIndexTabExcel().size()) { // if the folder

			JOptionPane.showMessageDialog(null, "Detected an excel file");
			boolean firstCheck = false;
			if (!tp.getComponent(1).getClass().equals(JScrollPane.class)) {
				firstCheck = true;
			}

			for (String path : result) {
				if (!tp.getIndexTabExcel().containsValue(new File(path))) { // if there is a nofileTab the first excel
																			// goes there
																			// Changing the textPane with the
																			// ScrollPane with the excel
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
						addExcelPanel(new File(path), tp); // For the rest of excels we create a new tab
					}
				}

			}

		}
	}

	/**
	 * Add a new excelTab to the tabPanel
	 * 
	 * @param excel The excel file to add
	 * @param tp    The Tabpanel
	 */
	public static void addExcelPanel(File excel, TabPanel tp) {

		String name = excel.getName();
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);
		JScrollPane s = new JScrollPane(excelPanel);

		if (!excel.getAbsoluteFile().equals(tp.getDir())) { // if the excel is in a subfolder the name contains the
															// different path + excel name

			String folder = excel.getAbsolutePath().replace(tp.getDir(), "");
			name = folder;
		}

		s.setName("Excel " + name);
		tp.add("Excel " + name, s);

		// we add to the maps
		tp.getExcelModificationIndexTab().put(tp.indexOfTab("Excel " + name), excel.lastModified()); // the index and
																										// the last
																										// modification
																										// of the file
		tp.getIndexTabExcel().put(tp.indexOfTab("Excel " + name), excel); // the index and the excel File
		tp.setSelectedIndex(tp.indexOfTab("Excel " + name)); // we make it the selected tab

	}

	/**
	 * Methods for checking the excel tabs in the seconds given in order to know if
	 * the excels had change or new ones have appear
	 * 
	 * @param tp        The tabPanel
	 * @param directory The current directory to check
	 * @param secons    The seconds between calls
	 */
	public static void excelcheckWithTime(TabPanel tp, String directory, int secons) {
		ExcelTask exTask = new ExcelTask(tp, directory);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(exTask, 0, 1000 * secons);
	}
}
