package funtions;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ij.measure.ResultsTable;

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
			if(dir.endsWith("temporal\\")) {
				filename=this.dir + sheet.getRow(1).getCell(0).getStringCellValue()+"results.xls";
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
		
		for (int i = 1; i <sheet.getRow(rowIndex).getLastCellNum(); i++) {
			Cell cell2Update = sheet.getRow(rowIndex).getCell(i);
			//cell2Update.setCellValue(newRow.getCell(i).getStringCellValue());
			cell2Update.setCellValue(49);
		}	
	}
	

}
