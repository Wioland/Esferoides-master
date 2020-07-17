package interfaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelTableCreator extends JTable {

	private static final long serialVersionUID = 1L;

	private DefaultTableModel tableModel;

	public ExcelTableCreator(File excel) {

		readXLSX(excel);

	}

	/**
	 * Creates a table mode with the data of an excel file
	 * 
	 * @param file excel file to read
	 */
	private void readXLSX(File file) {
		tableModel = new DefaultTableModel();
		try {
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = wb.getSheetAt(0);// first sheet
			Row row;
			Cell cell;

			// obtains the number of columns with data
			int maxCol = 0;
			for (int a = 0; a <= sheet.getLastRowNum(); a++) {
				if (sheet.getRow(a) != null) {
					if (sheet.getRow(a).getLastCellNum() > maxCol) {
						maxCol = sheet.getRow(a).getLastCellNum();
					}
				}
			}

			if (maxCol > 0) {

				// row by row
				Iterator<Row> rowIterator = sheet.iterator();
				row = rowIterator.next();
				// Adds the headings
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					cell = cellIterator.next();
					tableModel.addColumn(cell.getStringCellValue());
				}
				while (rowIterator.hasNext()) {

					int index = 0;
					row = rowIterator.next();

					Object[] obj = new Object[row.getLastCellNum()];
					cellIterator = row.cellIterator();

					while (cellIterator.hasNext()) {
						cell = cellIterator.next();
						// data of the blank cells
						while (index < cell.getColumnIndex()) {
							obj[index] = "";
							index += 1;
						}
						// gets the data from the cell
						switch (cell.getCellType()) {
						case BOOLEAN:
							obj[index] = cell.getBooleanCellValue();
							break;
						case NUMERIC:
							obj[index] = cell.getNumericCellValue();
							break;
						case STRING:
							obj[index] = cell.getStringCellValue();
							break;
						case BLANK:
							obj[index] = " ";
							break;
						case FORMULA:
							obj[index] = cell.getCellFormula();
							break;
						default:
							obj[index] = "";
							break;
						}
						index += 1;
					}
					tableModel.addRow(obj);
				}
				setModel(tableModel);
			} else {
				JOptionPane.showMessageDialog(null, "Nothing to import", "Error", JOptionPane.ERROR_MESSAGE);
			}
			wb.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error while reading the excel", "Error saving",
					JOptionPane.ERROR_MESSAGE);
		}

	}

}
