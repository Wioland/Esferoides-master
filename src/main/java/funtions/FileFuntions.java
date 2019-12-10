package funtions;

import java.awt.Container;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.tree.TreePath;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import esferoides.EsferoideJ_;
import ij.ImageJ;
import interfaces.AlgorithmView;
import interfaces.ShowImages;
import interfaces.TabPanel;
import interfaces.ViewImagesBigger;
import task.ExcelTask;
import task.ImagesTask;

public class FileFuntions {

	private static Map<String, Long> directoryLastChange;

	public static void chargePlugins() {
		Class<?> clazz = EsferoideJ_.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(),
				url.length() - clazz.getName().length() - ".class".length());
		System.setProperty("plugins.dir", pluginsDir);

		ImageJ imageJFrame = new ImageJ();
		imageJFrame.setVisible(false);

	}

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

	public static File getTemporalFolderFromOriginalPath(String OriginalPath) {
		File folder = new File(OriginalPath);
		folder = new File(OriginalPath.replace(folder.getName(), "") + "temporal");
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
		if (resp == 0) {

			JOptionPane.showMessageDialog(null, "Saving the images");

			List<String> temporalFiles = new ArrayList<String>();
			List<String> originalFiles = new ArrayList<String>();
			File saveDir = new File(saveDirPath);

			String originalPath = RoiFuntions.getoriginalFilePathFromTempralTiff(selectedFile.getAbsolutePath());
			File fOld = new File(originalPath);
			String originalName = fOld.getName();
			String extension = FileFuntions.extensionwithoutName(originalPath);
			String pattern = originalName.replace(extension, ".*\\.*");

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
						extension = extensionwithoutName(s);
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
									JOptionPane.showMessageDialog(null,
											"And error ocurred and the files couldn´t be saved", "Error saving",
											JOptionPane.ERROR_MESSAGE);
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

						int rowIndex = ExcelActions.findRow(sheetResult, originalName.replace("." + extension, ""));
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

	public static boolean isExtension(List<String> result, String extension) {
		boolean isTif = true;
		for (String name : result) {
			if (!name.endsWith(extension)) {
				isTif = false;
				break;
			}
		}
		return isTif;
	}

	public static String namewithoutExtension(String filePath) {
		File faux = new File(filePath);
		String fileName = faux.getName();
		String nameNoextension = fileName.split("\\.")[0];
		return nameNoextension;
	}

	public static String extensionwithoutName(String filePath) {
		File f = new File(filePath);
		return f.getName().split("\\.")[1];
	}

	public static void addModificationDirectory(String directory) {
		if (directoryLastChange == null) {
			directoryLastChange = new HashMap<String, Long>();
		}
		File faux = new File(directory + "predictions");
		directoryLastChange.put(directory + "predictions", faux.lastModified());

	}

	public static boolean directoryHasChange(String directory) {
		File direFile = new File(directory + "predictions");
		return direFile.lastModified() != directoryLastChange.get(directory + "predictions");
	}

	public static void isDirectoryContentModify(String directory, TabPanel tp) {
		if (directoryHasChange(directory)) {
			JOptionPane.showMessageDialog(null, "The content of the directoy has change. Painting again the images",
					"Warning", JOptionPane.WARNING_MESSAGE);
			addModificationDirectory(directory);

			// Se pintan de nuevo las imagenes del tab
			JSplitPane sp = (JSplitPane) tp.getComponent(0);
			JScrollPane s = (JScrollPane) sp.getBottomComponent();
			JViewport jv = (JViewport) s.getComponent(0);
			ShowImages images = (ShowImages) jv.getComponent(0);

			List<String> actualImages = new ArrayList<String>();
			Utils.search(".*\\.tiff", new File(directory), actualImages);
			Collections.sort(actualImages);

			checkStillExist(images, actualImages); // se comprueban si las imagens de los botones siguen existiendo

			if (actualImages.size() != 0) { // si sigue habiendo achivos en los actuales, es decir que no estabn en los
											// botones se pasa a añadirlos
				for (String name : actualImages) {
					// convertir a formato que se pueda ver
					ImageIcon image = ShowTiff.showTiffToImageIcon(name);
					image.setDescription(name);

					// aniadir a button
					// Obtiene un icono en escala con las dimensiones especificadas
					ImageIcon iconoEscala = new ImageIcon(
							image.getImage().getScaledInstance(700, 700, java.awt.Image.SCALE_DEFAULT));
					JButton imageView = new JButton(iconoEscala);
					imageView.setIcon(iconoEscala);
					imageView.setName(name);

					images.getImageIcon().add(image);

					imageView.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {

							TabPanel tap = null;

							String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
							if (tap != null && tap.indexOfTab(nombreTab) == -1) {
								ViewImagesBigger viewImageBig = new ViewImagesBigger(image, images.getImageIcon(), tap);
							}

						}
					});

					images.getListImagesPrev().put(name, imageView);

					File faux = new File(name);
					images.getLastModifyImage().put(name, faux.lastModified());

					images.add(imageView);
				}
			}
			images.repaint();
		}
	}

	public static void checkStillExist(ShowImages images, List<String> actualImages) {
		 Iterator<String> imageModify =   images.getLastModifyImage().keySet().iterator();
		while (imageModify.hasNext()) {
			
			String imaPath=imageModify.next();
			File faux = new File(imaPath);

			if (faux.exists()) { // si sigue existiendo se mire si ha sido modificada
				if (faux.lastModified() != images.getLastModifyImage().get(imaPath)) {

					JOptionPane.showMessageDialog(null, "The image " + faux.getName() + " has change", "Warning",
							JOptionPane.WARNING_MESSAGE);

					JButton imageButton = images.getListImagesPrev().get(imaPath);
					ImageIcon ima = new ImageIcon(imaPath);
					imageButton.setIcon(ima);
					imageButton.repaint();

					images.getListImagesPrev().put(imaPath, imageButton);
					images.getLastModifyImage().put(imaPath, faux.lastModified());
					
				}
				actualImages.remove(imaPath);
			} else {// si no existe borrarla del map de imagenes

				JButton deleteImage = images.getListImagesPrev().get(imaPath);
				images.remove(deleteImage);

				imageModify.remove();
				imageModify.remove();

			}

		}
	}

	public static void imagescheckWithTime(TabPanel tp, int secons) {
		ImagesTask imatask = new ImagesTask(tp);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(imatask, 0, 1000 * secons);
	}
}
