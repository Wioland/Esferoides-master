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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private File excel;
	private DefaultTableModel tableModel;

	public ExcelTableCreator(File excel) {
		this.excel = excel;
		readXLSX(excel);
	}

	private void readXLSX(File file) {
		tableModel = new DefaultTableModel();
		try {
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = wb.getSheetAt(0);// primera hoja
			Row row;
			Cell cell;

			// obtiene cantidad total de columnas con contenido
			int maxCol = 0;
			for (int a = 0; a <= sheet.getLastRowNum(); a++) {
				if (sheet.getRow(a) != null) {
					if (sheet.getRow(a).getLastCellNum() > maxCol) {
						maxCol = sheet.getRow(a).getLastCellNum();
					}
				}
			}
			if (maxCol > 0) {
				// Añade encabezado a la tabla
				for (int i = 1; i <= maxCol; i++) {
					tableModel.addColumn("Col." + i);
				}
				// recorre fila por fila
				Iterator<Row> rowIterator = sheet.iterator();
				while (rowIterator.hasNext()) {

					int index = 0;
					row = rowIterator.next();

					Object[] obj = new Object[row.getLastCellNum()];
					Iterator<Cell> cellIterator = row.cellIterator();

					while (cellIterator.hasNext()) {
						cell = cellIterator.next();
						// contenido para celdas vacias
						while (index < cell.getColumnIndex()) {
							obj[index] = "";
							index += 1;
						}
						// extrae contenido de archivo excel
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_BOOLEAN:
							obj[index] = cell.getBooleanCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							obj[index] = cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_STRING:
							obj[index] = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_BLANK:
							obj[index] = " ";
							break;
						case Cell.CELL_TYPE_FORMULA:
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
		} catch (IOException ex) {
			System.err.println("" + ex.getMessage());
		}
	}

	
	
	
}
