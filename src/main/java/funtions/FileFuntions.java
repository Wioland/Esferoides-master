package funtions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

public class FileFuntions {

	public static String getPathSelectedTreeFile(TreePath tp) {
		String path = "";

		for (int i = 0; i < tp.getPathCount(); i++) {
			path += tp.getPath()[i].toString();
			if (i > 0 && i != (tp.getPathCount() - 1)) {

				path += File.separator;

			}
		}

		return path;

	}

	public static File getTemporalFolderFromNd2Path(String nd2Path) {
		File folder = new File(nd2Path);
		folder = new File(nd2Path.replace(folder.getName(), "") + "temporal");
		return folder;
	}

	// guardar la imagen del algoritmo no solo el tiff tambien el zip
	// generados
	public static void saveSelectedImage(File selectedFile, String saveDirPath) {
		// se bucan los archivos tif y zip con el mismo nombre dentro de la carpeta
		// temporal

		// se coge el nombre del archivo creado y se le quita el nombre del algoritmo
		// utilizado

		// Se sobre escriben los archivos de la carpeta general con el mismo nombre

		int resp = JOptionPane.showConfirmDialog(null,
				"This action will delete the current images in predition folder. Are you sure you want to proceed to save?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if(resp==0) {
			
			JOptionPane.showMessageDialog(null, "Saving the images");
			
			List<String> temporalFiles = new ArrayList<String>();
			List<String> originalFiles = new ArrayList<String>();
			File saveDir = new File(saveDirPath);

			String originalPath = RoiFuntions.getNd2FilePathFromTempralTiff(selectedFile.getAbsolutePath());
			File fOld = new File(originalPath);
			String originalName = fOld.getName();
			String pattern = originalName.replace(".nd2", ".*\\.*");

			File oldFolder = new File(selectedFile.getAbsolutePath().replace(selectedFile.getName(), ""));

			Utils.search(pattern, oldFolder, temporalFiles);
			Utils.search(pattern, saveDir, originalFiles);

			for (String s : temporalFiles) {
				File f = new File(s);
				if (!f.getName().endsWith("xls")) {

					/*
					 * si se a movido el archivo nuevo a la carpeta predicctions se comprueba que el
					 * archivo original existe y en ese caso se borra y se renombra el nuevo, si no
					 * solo se renombra el nuevo
					 */
					for (String oriFilePath : originalFiles) {
						String extension = f.getName().split("\\.")[1];
						if (oriFilePath.endsWith(extension)) {
							File orFile = new File(oriFilePath);
							if (orFile.exists()) {
								// f.renameTo(new File(f.getAbsolutePath().replace(f.getName(),
								// orFile.getName())));
								Path from = f.toPath();
								Path to = orFile.toPath();
								try {
									Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									JOptionPane.showMessageDialog(null, "And error ocurred and the files couldn´t be saved",
											"Error saving", JOptionPane.ERROR_MESSAGE);
								}

							}

							break;
						}

					}
				} else {

					// cambiar la fila del excel por la nueva
					try {

						HSSFWorkbook modifyExcel = new HSSFWorkbook(
								new FileInputStream(new File(originalPath.replace(originalName, "") + "results.xls")));
						HSSFWorkbook newdataExcel = new HSSFWorkbook(new FileInputStream(new File(s)));

						HSSFSheet sheetResult = modifyExcel.getSheet("Results");
						HSSFRow newRow = newdataExcel.getSheet("Results").getRow(1);

						int rowIndex = ExcelActions.findRow(sheetResult, originalName.replace(".nd2", ""));
						ExcelActions.changeRow(rowIndex, sheetResult, newRow);
						FileOutputStream out = new FileOutputStream(
								new File(originalPath.replace(originalName, "") + "results.xls"));
						modifyExcel.write(out);

						out.close();

					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error changing the excel row", "Error saving",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			JOptionPane.showMessageDialog(null, "Save files in prediccion folder succed");
		}

		
	}

	// si se sale de la app o para borrar la carpeta tras seleccionar una imagen
	public static void deleteTemporalFolder(File temporalFolder) {

		if (temporalFolder.exists()) {
			File[] files = temporalFolder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteTemporalFolder(file);
				} else {
					file.delete();
				}

			}
			temporalFolder.delete();
		}

	}
}
