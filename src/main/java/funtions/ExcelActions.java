package funtions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import ij.measure.ResultsTable;
import interfaces.ExcelTableCreator;
import interfaces.TabPanel;
import task.ExcelTask;

public class ExcelActions {

	private ResultsTable rt;
	private String dir;

	public ExcelActions(ResultsTable rt, String dir) {
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
		String[] rowi;
		HSSFRow row;
		for (int i = 0; i < rows; i++) {
			row = sheet.createRow((short) i + 1);
			rowi = this.rt.getRowAsString(i).split("\\t");
			for (int j = 1; j <= headings.length; j++) {

				row.createCell((short) j - 1).setCellValue(rowi[j]);
			}
		}

		FileOutputStream fileOut;
		try {
			String filename = this.dir + "results.xls";

			// in case the excel is from the temporal images we change the name
			// in order to
			// identify the excel with the image
			if (dir.endsWith("temporal" + File.separator)) {
				String name = sheet.getRow(1).getCell(0).getStringCellValue();

				filename = this.dir + name + "_results.xls";

			}

			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while creating the excel associated with the image");
		}

	}

	/**
	 * Method that find the row of an object in an excel file based on the name
	 * it has on the first column
	 * 
	 * @param sheet
	 *            sheet of the excel where you wanted to find the object
	 * @param cellContent
	 *            id of the object in the excel
	 * @return the row number of the object or -1 if the object isn't in the
	 *         excel sheet
	 */

	public static int findRow(HSSFSheet sheet, String cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == CellType.STRING) {
					if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
						return row.getRowNum();
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Changes the content of the row all Except from the first column with
	 * contains the id
	 * 
	 * @param rowIndex
	 *            the excel row we are going to change
	 * @param sheet
	 *            the sheet of the excel in witch we are going to change a row
	 * @param newRow
	 *            a row with the new content
	 */
	public static void changeRow(int rowIndex, HSSFSheet sheet, Row newRow) {

		for (int i = 1; i < sheet.getRow(rowIndex).getLastCellNum(); i++) {
			Cell cell2Update = sheet.getRow(rowIndex).getCell(i);
			cell2Update.setCellValue(newRow.getCell(i).getStringCellValue());

		}
	}

	/**
	 * If an excel is been shown in the tabPanel, it checks if the excel has
	 * been modified or deleted. If there weren't excels checks if now there are
	 * and add them.
	 * 
	 * @param tp
	 *            The tabPanel that contains the excels tabs
	 * @param dir
	 *            the directory where the excel is
	 */
	public static void checkAllExcelTab(TabPanel tp, String dir) {
tp.setSelectedIndex(0);
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
	 * @param tp
	 *            TabPanel that contains the excel tab
	 * @param dir
	 *            The excel directory
	 * @param index
	 *            The tab index to check
	 */
	public static void checkExcelTab(TabPanel tp, String dir, int index) {

		if (tp.getIndexTabExcel().containsKey(index)) { // if this index is an
														// excel tab

			File excel = tp.getIndexTabExcel().get(index);
			List<String> result = new ArrayList<String>();
			File folder = new File(dir);

			Utils.searchDirectory(".*\\.xls", folder, result);
			Collections.sort(result);

			if (excel != null) { // if it tab has an excel it tab isn't a
									// noFileTab
				if (excel.exists()) { // if the excel file still exist in the
										// folder

					Long modTab = tp.getExcelModificationIndexTab().get(index);
					Long excelMod = excel.lastModified();

					if (!modTab.equals(excelMod)) { // if it hasn't been
													// modified

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
					checkExcelTab(tp, dir, index);

				}

			} else { // if the tab doesn't have an excel checks if there are new
						// excels

				addedExcelToTheTab(result, tp);

			}
		}

	}

	/**
	 * If the excel has been deleted we delete the tab in the tabPane if the tab
	 * is the last having an excel in the tabPanel we doesn't delete it and we
	 * transform it in a noFile tab
	 * 
	 * @param excel
	 *            the excel file
	 * @param index
	 *            the index of the tab in the tabPane
	 * @param tp
	 *            The tabPanel
	 */
	public static void deleteExcelTab(File excel, int index, TabPanel tp) {

		
		if (index <= tp.countComponents()&& index>0) {
			JOptionPane.showMessageDialog(null, "The excel " + excel.getName() + " was deleted");
			JScrollPane sp = (JScrollPane) tp.getComponentAt(index);
			JViewport jP = (JViewport) sp.getComponent(0);

			if (tp.getIndexTabExcel().size() == 1) { // if it is the last excel
														// tab we transform it
														// to a noFileTab

				tp.noFileText("Excel", jP);
				tp.setTitleAt(1, "Excel ");

				tp.getIndexTabExcel().remove(1);
				tp.getExcelModificationIndexTab().remove(1);

			} else {
				tp.remove(index); // we remove it
				int indexTabExcelSize=tp.getIndexTabExcel().size();
				
				tp.getIndexTabExcel().remove(index);
				
				if(index!=(indexTabExcelSize+1)){
					int i=0;
					for (i=index;i<(indexTabExcelSize+1);i++ ) {
						tp.getIndexTabExcel().put(i, tp.getIndexTabExcel().get(i+1));
					}
					tp.getIndexTabExcel().remove(tp.getIndexTabExcel().size());
				}
				

			}
		}

	}

	/**
	 * add an new excel to the tabPanel When a new excel appear in the directory
	 * 
	 * @param result
	 *            list of excel paths
	 * @param tp
	 *            The tabPanel
	 */
	public static void addedExcelToTheTab(List<String> result, TabPanel tp) {

		// int resultSiz = result.size();
		// int tpSize = tp.getIndexTabExcel().size();
		if (result.size() > tp.getIndexTabExcel().size()) { // if the folder

			JOptionPane.showMessageDialog(null, "Detected an excel file");
			boolean firstCheck = false;
			if (!tp.getIndexTabExcel().isEmpty()) {
				firstCheck = true;
			}
			String name;
			ExcelTableCreator eTC;
			File ex;

			for (String path : result) {
				// if there is a nofileTab the first excel goes there Changing
				// the textPane with
				// the ScrollPane with the excel
				if (!tp.getIndexTabExcel().containsValue(new File(path))) {
					if (!firstCheck) {
						JScrollPane sp = (JScrollPane) tp.getComponent(1);
						JViewport jP = (JViewport) sp.getComponent(0);
						jP.remove(jP.getComponent(0));

						ex = new File(path);
						eTC = new ExcelTableCreator(ex);

						jP.add(eTC);
						jP.repaint();

						name = ex.getName();
						tp.getExcelModificationIndexTab().put(1, ex.lastModified());
						tp.getIndexTabExcel().put(1, ex);
						tp.setTitleAt(1, "Excel " + name);

						firstCheck = true;

					} else {
						addExcelPanel(new File(path), tp); // For the rest of
															// excels we create
															// a new tab
					}
				}

			}

		}
	}

	/**
	 * Add a new excelTab to the tabPanel
	 * 
	 * @param excel
	 *            The excel file to add
	 * @param tp
	 *            The Tabpanel
	 */
	public static void addExcelPanel(File excel, TabPanel tp) {

		String name = excel.getName();
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);
		JScrollPane s = new JScrollPane(excelPanel);
		// if the excel is in a subfolder the name contains the different path +
		// excel
		// name
		if (!excel.getAbsolutePath().equals(tp.getDir())) {

			String folder = excel.getAbsolutePath().replace(tp.getDir(), "");
			name = folder;
		}

		s.setName("Excel " + name);
		tp.add("Excel " + name, s);

		// we add to the maps the index and the last modification of the file
		tp.getExcelModificationIndexTab().put(tp.indexOfTab("Excel " + name), excel.lastModified());
		tp.getIndexTabExcel().put(tp.indexOfTab("Excel " + name), excel); // the
																			// index
																			// and
																			// the
																			// excel
																			// File
		tp.setSelectedIndex(tp.indexOfTab("Excel " + name)); // we make it the
																// selected tab

	}

	/**
	 * Methods for checking the excel tabs in the seconds given in order to know
	 * if the excels had change or new ones have appear
	 * 
	 * @param tp
	 *            The tabPanel
	 * @param directory
	 *            The current directory to check
	 * @param secons
	 *            The seconds between calls
	 */
	public static Timer excelcheckWithTime(TabPanel tp, String directory, int secons) {
		ExcelTask exTask = new ExcelTask(tp, directory);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(exTask, 0, 1000 * secons);
		return temporizador;
	}

	/**
	 * Deletes all the excels in the directory given
	 * 
	 * @param directory
	 *            folder in with we are going to delete the excel files
	 */
	public static void deleteAllExcels(File directory) {

		String pattern = ".*\\_results.xls";
		List<String> result = new ArrayList<String>();
		Utils.searchDirectory(pattern, directory, result);

		File aux = null;
		for (String string : result) {
			aux = new File(string);
			aux.delete();
		}

	}

	/**
	 * Merges different excels in the results.xls excel, if it doesn't exist the
	 * excel given transforms in to it
	 * 
	 * @param excel
	 *            the excel to merge
	 * @param originalName
	 *            original name of the image associated with the data
	 * @param directory
	 *            the directory in with the merge takes place
	 */
	public static void mergeExcels(File excel, String originalName, File directory) {

		List<String> result = new ArrayList<String>();

		Utils.searchDirectory("results.xls", directory, result);

		FileInputStream inputStream;
		HSSFWorkbook wb;
		try {

			if (result.isEmpty()) {

				// changes the name of the first row
				inputStream = new FileInputStream(excel);
				wb = new HSSFWorkbook(inputStream);
				modifyCell(wb, 0, 1, 0, originalName);

				inputStream.close();

				FileOutputStream outputFile = new FileOutputStream(excel);

				wb.write(outputFile);

				outputFile.close();

				excel.renameTo(new File(excel.getAbsolutePath().replace(excel.getName(), "results.xls")));
			} else {
				// adds a row but with the name changed
				inputStream = new FileInputStream(excel);
				wb = new HSSFWorkbook(inputStream);
				modifyCell(wb, 0, 1, 0, originalName);

				FileOutputStream outputFile = new FileOutputStream(excel);
				wb.write(outputFile);
				outputFile.close();

				HSSFRow newRow = getRow(excel, 0, 1);
				inputStream.close();
				inputStream = new FileInputStream(new File(result.get(0)));
				wb = new HSSFWorkbook(inputStream);

				addRow(wb, 0, newRow);

				inputStream.close();
				outputFile = new FileOutputStream(new File(result.get(0)));
				wb.write(outputFile);

				outputFile.close();

				excel.delete();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Gets a row from an excel file
	 * 
	 * @param excel
	 *            excel file
	 * @param indexSheet
	 *            index of the sheet to take the row
	 * @param indexRow
	 *            index of the row you wants to get
	 * @return the row of the excel in the position given
	 */
	private static HSSFRow getRow(File excel, int indexSheet, int indexRow) {
		HSSFWorkbook oldExcel;
		HSSFRow newRow = null;
		try {
			oldExcel = new HSSFWorkbook(new FileInputStream(excel));
			newRow = oldExcel.getSheetAt(indexSheet).getRow(indexRow);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newRow;
	}

	/**
	 * Modify the value of an excel cell
	 * 
	 * @param workbook
	 *            the workbook of the excel
	 * @param indexSheet
	 *            index of the sheet
	 * @param indexRow
	 *            index of the row
	 * @param indexCell
	 *            index of the cell
	 * @param newValue
	 *            new value of the cell
	 */
	public static void modifyCell(HSSFWorkbook workbook, int indexSheet, int indexRow, int indexCell, String newValue) {
		HSSFSheet sheet = workbook.getSheetAt(indexSheet);
		Cell cell2Update = sheet.getRow(indexRow).getCell(indexCell);
		cell2Update.setCellValue(newValue);
	}

	/**
	 * 
	 * Adds a new row to the excel given
	 * 
	 * @param workbook
	 *            workbook of the excel
	 * @param indexSheet
	 *            index of the sheet
	 * @param newRow
	 *            row to insert
	 */
	public static void addRow(HSSFWorkbook workbook, int indexSheet, HSSFRow newRow) {
		HSSFSheet sheet = workbook.getSheetAt(indexSheet);
		int rowCount = sheet.getLastRowNum();
		if (rowCount != 0 || sheet.getRow(0) != null) {
			rowCount++;
		}

		sheet.createRow((short) (rowCount));
		// changeRow(rowCount+1, sheet, newRow);

		for (int i = 0; i < newRow.getLastCellNum(); i++) {
			sheet.getRow(rowCount).createCell(i);
			Cell cell2Update = sheet.getRow(rowCount).getCell(i);

			if (newRow.getCell(i) != null) {
				switch (newRow.getCell(i).getCellType()) {
				case BOOLEAN:
					cell2Update.setCellValue(newRow.getCell(i).getBooleanCellValue());
					break;
				case NUMERIC:
					cell2Update.setCellValue(newRow.getCell(i).getNumericCellValue());
					break;
				case STRING:
					cell2Update.setCellValue(newRow.getCell(i).getStringCellValue());
					break;
				case BLANK:
					cell2Update.setCellValue(newRow.getCell(i).getStringCellValue());
					break;
				case ERROR:
					cell2Update.setCellValue(newRow.getCell(i).getErrorCellValue());
					break;
				case FORMULA:
					break;
				case _NONE:
					break;
				default:
					break;
				}
			}

		}

	}

	/**
	 * Separates each row of the current excel in diferrent excels
	 * 
	 * @param pathFile
	 *            path of the excel to unmerge
	 * @param originalNewSelected
	 *            has the current label of the row and the old/new
	 */
	public static void unMergeExcel(String pathFile, Map<String, JButton> originalNewSelected) {
		if (pathFile.endsWith(File.separator)) {
			pathFile += "results.xls";
		} else {
			pathFile += File.separator + "results.xls";
		}

		File excel = new File(pathFile);

		if (excel.exists()) {

			FileInputStream inputStream;
			HSSFWorkbook wb;
			try {

				// changed the name of the first row
				inputStream = new FileInputStream(excel);
				wb = new HSSFWorkbook(inputStream);
				HSSFSheet sheet = wb.getSheetAt(0);

				String name = "";
				HSSFRow row;
				for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {

					if (i != 0) {
						row = sheet.getRow(i);
						name = row.getCell(0).getStringCellValue();
						name = originalNewSelected.get(name).getName();
						createExcelFromOtherExcelRow(name, sheet.getRow(0), row);
					}

				}

				inputStream.close();

				excel.delete();

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	/**
	 * Creates a new excel from a row
	 * 
	 * @param pathFile
	 *            the path to save the file. That is the path of the image
	 *            associated with the data, so we have to change the extension
	 *            and name in the funtion
	 * @param headings
	 *            the row that contains the headings of the new data
	 * @param data
	 *            the row with the data
	 */
	private static void createExcelFromOtherExcelRow(String pathFile, HSSFRow headings, HSSFRow data) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		workbook.createSheet("Results");

		FileOutputStream fileOut;
		try {

			String filename = pathFile.replace(".tiff", "_results.xls");
			addRow(workbook, 0, headings);

			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();

			data.getCell(0).setCellValue(FileFuntions.namewithoutExtension(pathFile).replace("_pred", ""));
			addRow(workbook, 0, data);

			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while creating the excel associated with the image");
		}

	}
}
