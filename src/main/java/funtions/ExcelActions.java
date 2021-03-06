package funtions;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import ij.io.DirectoryChooser;
import ij.measure.ResultsTable;
import interfaces.ExcelTableCreator;
import interfaces.TabPanel;
import task.ExcelTask;

/**
 * Functions for working with Excel files
 * 
 * @author Yolanda
 *
 */
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
			// in order to identify the excel with the image
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
			JOptionPane.showMessageDialog(Utils.mainFrame,
					"An error occurred while creating the excel associated with the image");
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
		if (tp.getIndexTabExcel() != null) {
			for (Integer tbIndex : tp.getIndexTabExcel().keySet()) {
				lAux.add(tbIndex);
			}

			// for all the excel tabs checks id they have changed
			for (Integer tbIndex : lAux) {
				checkExcelTab(tp, dir, tbIndex);
			}

			List<String> result = new ArrayList<String>();
			File folder = new File(dir);

			Utils.search(".*\\.xls", folder, result, 0);
			Collections.sort(result);

			// checks if the are new excels
			addedExcelToTheTab(result, tp);
		}

	}

	/**
	 * Checks if the excel of it tab have change, if it has been deleted or a new
	 * Excel has appeared
	 * 
	 * @param tp    TabPanel that contains the excel tab
	 * @param dir   The excel directory
	 * @param index The tab index to check
	 */
	public static void checkExcelTab(TabPanel tp, String dir, int index) {

		if (tp.getIndexTabExcel().containsKey(index)) { // if this index is an
														// excel tab

			File excel = tp.getIndexTabExcel().get(index);
			List<String> result = new ArrayList<String>();
			File folder = new File(dir);

			Utils.search(".*\\.xls", folder, result, 0);
			Collections.sort(result);

			if (excel != null) { // if it tab has an excel it tab isn't a
									// noFileTab
				if (excel.exists()) { // if the excel file still exist in the
										// folder

					Long modTab = tp.getExcelModificationIndexTab().get(index);
					Long excelMod = excel.lastModified();

					if (!modTab.equals(excelMod)) { // if it hasn't been
													// modified

						JOptionPane.showMessageDialog(Utils.mainFrame,
								"The excel " + excel.getName() + " was modified. Updating it´s tab");

						JPanel jpa = (JPanel) tp.getComponentAt(index);
						JScrollPane sp = (JScrollPane) jpa.getComponent(1);
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
	 * If the excel has been deleted we delete the tab in the tabPane if the tab is
	 * the last having an excel in the tabPanel we doesn't delete it and we
	 * transform it in a noFile tab
	 * 
	 * @param excel the excel file
	 * @param index the index of the tab in the tabPane
	 * @param tp    The tabPanel
	 */
	public static synchronized void deleteExcelTab(File excel, int index, TabPanel tp) {

//		tp.countComponents()
		if (index <= tp.getComponents().length && index > 0) {
			JOptionPane.showMessageDialog(Utils.mainFrame, "The excel " + excel.getName() + " was deleted");
			JPanel jpa = (JPanel) tp.getComponentAt(index);
			JScrollPane sp = (JScrollPane) jpa.getComponent(1);
			JViewport jP = (JViewport) sp.getComponent(0);

			// if it is the last excel tab we transform it to a noFileTab
			if (tp.getIndexTabExcel().size() == 1) {
				jpa.remove(0);
				tp.noFileText("Excel", jP);
				tp.setTitleAt(1, "Excel ");

				tp.getIndexTabExcel().remove(1);
				tp.getExcelModificationIndexTab().remove(1);

			} else {
				tp.remove(index); // we remove it
				int indexTabExcelSize = tp.getIndexTabExcel().size();

				tp.getIndexTabExcel().remove(index);

				if (index != (indexTabExcelSize + 1)) {
					int i = 0;
					for (i = index; i < indexTabExcelSize; i++) {
						tp.getIndexTabExcel().put(i, tp.getIndexTabExcel().get(i + 1));
					}
					tp.getIndexTabExcel().remove(tp.getIndexTabExcel().size());
				}

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

		if (result.size() > tp.getIndexTabExcel().size()) { // if the folder

			JOptionPane.showMessageDialog(Utils.mainFrame, "Detected an excel file");
			boolean firstCheck = false;
			if (!tp.getIndexTabExcel().isEmpty()) {
				firstCheck = true;
			}

			for (String path : result) {
				// if there is a nofileTab the first excel goes there Changing
				// the textPane with
				// the ScrollPane with the excel
				if (!tp.getIndexTabExcel().containsValue(new File(path))) {
					if (!firstCheck) {
						tp.remove(1);
						addExcelPanel(new File(path), tp, 1);
						firstCheck = true;

					} else {
						// For the rest of excels we create a new tab
						addExcelPanel(new File(path), tp,-1);
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
	 * @param index index in the tabpanel if -1 the last index of the tabpanel
	 */
	public static void addExcelPanel(File excel, TabPanel tp, int index) {

		String name = excel.getName();
		ExcelTableCreator excelPanel = new ExcelTableCreator(excel);
		JScrollPane s = new JScrollPane(excelPanel);
		JPanel panel = new JPanel(new GridBagLayout());
		JButton buSaveExcel = new JButton("EXPORT EXCEL FILE");
		buSaveExcel.setMaximumSize(new Dimension(200, 200));
		buSaveExcel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DirectoryChooser dc = new DirectoryChooser("Select the folder to save the excel");
				String dir = dc.getDirectory();

				if (dir != null) {

					if (!dir.endsWith(File.separator)) {
						dir += File.separator;
					}
					dir += excel.getName();

					FileFuntions.copiFile(excel.getAbsolutePath(), dir);

					JOptionPane.showMessageDialog(Utils.mainFrame, "File excel saved in the selected File. "
							+ "\n Warning: the current File is only a copy of the original. The original file is the one that will ve updated with the changes.");
				}

			}
		});

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0;
		constraints.weighty = 0;

		constraints.gridx = 0;
		constraints.gridy = 0;

		panel.add(buSaveExcel, constraints);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;

		panel.add(s, constraints);

		// if the excel is in a subfolder the name contains the different path + excel
		// name
		if (!excel.getAbsolutePath().equals(tp.getDir())) {

			String folder = excel.getAbsolutePath().replace(tp.getDir(), "");
			name = folder;
		}

		s.setName("Excel " + name);
		if (index == -1) {
			tp.add("Excel " + name, panel);
		} else {
			tp.insertTab("Excel " + name, null, panel, null, index);
		}

		// we add to the maps the index and the last modification of the file
		tp.getExcelModificationIndexTab().put(tp.indexOfTab("Excel " + name), excel.lastModified());
		// the index and the excel File
		tp.getIndexTabExcel().put(tp.indexOfTab("Excel " + name), excel);
		// we make it the selected tab
		tp.setSelectedIndex(tp.indexOfTab("Excel " + name));

	}

	/**
	 * Methods for checking the excel tabs in the seconds given in order to know if
	 * the Excels had changed or new ones have appeared
	 * 
	 * @param tp        The tabPanel
	 * @param directory The current directory to check
	 * @param secons    The seconds between calls
	 * @return Timer
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
	 * @param directory folder in with we are going to delete the excel files
	 */
	public static void deleteAllExcels(File directory) {

		String pattern = ".*\\_results.xls";
		List<String> result = new ArrayList<String>();
		Utils.search(pattern, directory, result, 1);

		File aux = null;
		for (String string : result) {
			aux = new File(string);
			aux.delete();
		}

	}

	/**
	 * Merges the excels of a directory and its subdirectories down to a certain
	 * depth and leaves the result excel in the main directory
	 * 
	 * @param directory main directory
	 * @param deep      deep of merge
	 */
	public static void mergeExcelsDirectoryAndSubdir(File directory, int deep) {

		List<String> result = new ArrayList<String>();

		Utils.search("results.xls", directory, result, deep);
		if (!result.isEmpty()) {
			FileInputStream inputStream;
			HSSFWorkbook wb;
			try {

				// The first one is the new excel
				File excel = new File(result.get(0));
				String path = directory.getAbsolutePath();
				if (path.endsWith(File.separator)) {
					path += File.separator;
				}
				path += "results.xls";

				excel.renameTo(new File(path));
				excel = new File(path);

				FileOutputStream outputFile = new FileOutputStream(excel);
				inputStream = new FileInputStream(excel);
				wb = new HSSFWorkbook(inputStream);

				File excelGetRow = null;
				HSSFRow newRow = null;

				for (int i = 1; i < result.size(); i++) {
					excelGetRow = new File(result.get(i));

					int num = getNumberLastRow(excelGetRow, 0);

					for (int j = 1; j < num + 1; j++) {
						newRow = getRow(excel, 0, j);
						addRow(wb, 0, newRow);
						wb.write(outputFile);
					}
					excelGetRow.delete();
				}

				inputStream.close();

				outputFile.close();
				wb.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Gets the last row of the sheet of an excel file
	 * 
	 * @param excel Excel file
	 * @param sheet position of the sheet on the excel
	 * @return the position of the last row in the excel
	 */
	private static int getNumberLastRow(File excel, int sheet) {
		HSSFWorkbook oldExcel;
		int n = 0;
		try {
			oldExcel = new HSSFWorkbook(new FileInputStream(excel));
			n = oldExcel.getSheetAt(sheet).getLastRowNum();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return n;
	}

	/**
	 * Merges different excels in the results.xls excel, if it doesn't exist the
	 * excel given transforms in to it
	 * 
	 * @param excel        the excel to merge
	 * @param originalName original name of the image associated with the data
	 * @param directory    the directory in with the merge takes place
	 */
	public static void mergeExcels(File excel, String originalName, File directory) {

		List<String> result = new ArrayList<String>();

		Utils.search("results.xls", directory, result, 1);

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
	 * @param excel      excel file
	 * @param indexSheet index of the sheet to take the row
	 * @param indexRow   index of the row you wants to get
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
	 * @param workbook   the workbook of the excel
	 * @param indexSheet index of the sheet
	 * @param indexRow   index of the row
	 * @param indexCell  index of the cell
	 * @param newValue   new value of the cell
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
	 * @param workbook   workbook of the excel
	 * @param indexSheet index of the sheet
	 * @param newRow     row to insert
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
	 * @param pathFile            path of the excel to unmerge
	 * @param originalNewSelected has the current label of the row and the old/new
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
	 * @param pathFile the path to save the file. That is the path of the image
	 *                 associated with the data, so we have to change the extension
	 *                 and name in the funtion
	 * @param headings the row that contains the headings of the new data
	 * @param data     the row with the data
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
			JOptionPane.showMessageDialog(Utils.mainFrame,
					"An error occurred while creating the excel associated with the image");
		}

	}

	/**
	 * Saves an excel file in the given directory with the rows given
	 * 
	 * @param goodRows rows to add to the excel
	 * @param folder   place where the excel is going to be save
	 */
	public static void saveExcel(ArrayList<Integer> goodRows, File folder) {
		ResultsTable rt = ResultsTable.getResultsTable();
		int rows = rt.getCounter();
		for (int i = rows; i > 0; i--) {
			if (!(goodRows.contains(i - 1))) {
				rt.deleteRow(i - 1);
			} else {
				String[] s = rt.getRowAsString(i - 1).split(",");
				if (s.length == 1) {
					s = rt.getRowAsString(i - 1).split("\t");
				}

				if (s[1].equals("")) {
					rt.deleteRow(i - 1);
				}
			}

		}

		ExcelActions ete = new ExcelActions(rt, folder.getAbsolutePath() + File.separator);
		ete.convertToExcel();

		rt.reset();
	}

	/**
	 * Deletes a row of the excel given by id.
	 * 
	 * @param excelFile    Excel file
	 * @param originalName first column data of the excel that identify the row
	 */
	public static void deleteRow(File excelFile, String originalName) {
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(excelFile));
			HSSFSheet sheet = workbook.getSheet("Results");
			int rowIndex = findRow(sheet, originalName);
			removeRow(sheet, rowIndex);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Deletes a row of the excel given by position.
	 * 
	 * @param sheet    excel sheet
	 * @param rowIndex position of the row in the sheet
	 */
	public static void removeRow(HSSFSheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			HSSFRow removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}

	/**
	 * Changes a row of the given excel from the result Table of imageJ
	 * 
	 * @param goodRows     rows to add
	 * @param sheet        sheet to modify
	 * @param originalName id of the row
	 * @param excelFile    excel file
	 * @param workbook     workbook of the excel
	 */
	public static void getchangeExcelRowFromResultTable(ArrayList<Integer> goodRows, HSSFSheet sheet,
			String originalName, File excelFile, HSSFWorkbook workbook) {

		String[] s = null;
		ResultsTable rt = ResultsTable.getResultsTable();
		int rows = rt.getCounter();
		for (int i = rows; i > 0; i--) {
			if (!(goodRows.contains(i - 1))) {
				rt.deleteRow(i - 1);
			} else {
				s = rt.getRowAsString(i - 1).split(",");
				if (s.length == 1) {
					s = rt.getRowAsString(i - 1).split("\t");
				}

				if (s[1].equals("")) {
					rt.deleteRow(i - 1);
				}

			}

		}
		int intRow = ExcelActions.findRow(sheet, originalName);
		if (intRow == -1) { // If the row does not exist we create it
			HSSFRow newRow = sheet.createRow((short) sheet.getLastRowNum() + 1);
			for (int i = 1; i < s.length; i++) {
				newRow.createCell((short) i).setCellValue(s[i]);
			}
		} else { // if it exist we modify it
			for (int i = 1; i < sheet.getRow(intRow).getLastCellNum(); i++) {
				Cell cell2Update = sheet.getRow(intRow).getCell(i);
				cell2Update.setCellValue(s[i + 1]);
			}

		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(excelFile);
			workbook.write(out);

			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
