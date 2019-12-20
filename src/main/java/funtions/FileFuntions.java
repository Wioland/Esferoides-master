package funtions;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.tree.TreePath;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import esferoides.EsferoideJ_;
import ij.ImageJ;
import interfaces.ShowImages;
import interfaces.TabPanel;
import interfaces.ViewImagesBigger;
import task.ImagesTask;

public class FileFuntions {

	private static Map<String, Long> directoryLastChange;

	/**
	 * assign the plugin folder for imageJ and creates a instance of imageJ
	 */
	public static void chargePlugins() {
		Class<?> clazz = EsferoideJ_.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(),
				url.length() - clazz.getName().length() - ".class".length());
		System.setProperty("plugins.dir", pluginsDir);

		ImageJ imageJFrame = new ImageJ();
		// imageJFrame.setVisible(false);

	}

	/**
	 * Returns the Path of the selected file in the tree
	 * 
	 * @param tp The treePath created of the current directory
	 * @return The path of the selected file in the tree
	 */
	public static String getPathSelectedTreeFile(TreePath tp) {
		String path = "";

		for (int i = 0; i < tp.getPathCount(); i++) { // we generate the path form the String{} given
			path += tp.getPath()[i].toString();
			if (i > 0 && i != (tp.getPathCount() - 1)) {

				path += File.separator;

			}
		}

		return path;

	}

	/**
	 * Get the path of the temporal folder from a file path
	 * 
	 * @param originalPath A file path
	 * @return the path of the temporal folder
	 */
	public static File getTemporalFolderFromOriginalPath(String originalPath) {
		File folder = new File(originalPath);
		folder = new File(originalPath.replace(folder.getName(), "") + "temporal");
		return folder;
	}

	/**
	 * Save the files from the temporal folder to the prediction folder exchanging
	 * the original files in the prediction folder with the temporal ones We save
	 * the tiff and roi/zip and change the corresponding row on the result excel
	 * 
	 * @param selectedFile The temporal file to save
	 * @param saveDirPath
	 */
	public static void saveSelectedImage(File selectedFile, String saveDirPath) {

		// We look for the tiff and zip files with the same as the selected file
		// We take the files and take out he algorithm name
		// We exchange the files in the saveDir

		int resp = JOptionPane.showConfirmDialog(null,
				"This action will delete the current images in predition folder. Are you sure you want to proceed to save?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (resp == 0) { // if yes

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
					 * If the new file was succesfully move to the predicctions folder if the
					 * original file exist we delete it and change the name of the new file
					 * otherwise we onle rename
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

					// we change the excel row to the new one
					try {

						HSSFWorkbook modifyExcel = new HSSFWorkbook(
								new FileInputStream(new File(originalPath.replace(originalName, "") + "results.xls")));
						HSSFWorkbook newdataExcel = new HSSFWorkbook(new FileInputStream(new File(s)));

						HSSFSheet sheetResult = modifyExcel.getSheet("Results");
						HSSFRow newRow = newdataExcel.getSheet("Results").getRow(1);
						extension = extensionwithoutName(originalName);
						String auxOriginal = originalName.replace("." + extension, "");

						int rowIndex = ExcelActions.findRow(sheetResult, auxOriginal);
						if (rowIndex != -1) {
							ExcelActions.changeRow(rowIndex, sheetResult, newRow);
							FileOutputStream out = new FileOutputStream(
									new File(originalPath.replace(originalName, "") + "results.xls"));
							modifyExcel.write(out);

							out.close();
						}

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

	/**
	 * For delete a folder If we close the app or the algorithm view window we
	 * delete the temporal folder
	 * 
	 * @param temporalFolder The folder to delete
	 */
	public static void deleteTemporalFolder(File temporalFolder) {

		if (temporalFolder.exists()) { // if exist
			File[] files = temporalFolder.listFiles();
			for (File file : files) { // if it isn't empty we delete it's files
				if (file.isDirectory()) { // if it's a directory we call this method else we delete it
					deleteTemporalFolder(file);
				} else {
					file.delete();
				}

			}
			temporalFolder.delete(); // we delete it
		}

	}

	/**
	 * 
	 * check if all the files have the same extension
	 * 
	 * @param result    list of path
	 * @param extension extension to check
	 * @return true if all files have the same extension false otherwise
	 */
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

	/**
	 * From a file path we get the name of the file without the file extension
	 * 
	 * @param filePath the path of the file
	 * @return the name of the file with out the extension
	 */
	public static String namewithoutExtension(String filePath) {
		File faux = new File(filePath);
		String fileName = faux.getName();
		String nameNoextension = fileName.split("\\.")[0];
		return nameNoextension;
	}

	/**
	 * We get the extension of a file
	 * 
	 * @param filePath the path of the file we want to know the extension
	 * @return the extension of the file
	 */
	public static String extensionwithoutName(String filePath) {
		File f = new File(filePath);
		return f.getName().split("\\.")[1];
	}

	/**
	 * Creates the directoryLastChange if it wasn't all ready created and add the
	 * last modification of the directory
	 * 
	 * @param directory The path of the directory
	 */
	public static void addModificationDirectory(String directory) {
		if (directoryLastChange == null) {
			directoryLastChange = new HashMap<String, Long>();
		}
		File faux = new File(directory + "predictions");
		directoryLastChange.put(directory + "predictions", faux.lastModified());

	}

	/**
	 * check if a directory has been modify
	 * 
	 * @param directory The path of the directory
	 * @return true if the directory has been modify
	 */
	public static boolean directoryHasChange(String directory) {
		File direFile = new File(directory + "predictions");
		return direFile.lastModified() != directoryLastChange.get(directory + "predictions");
	}

	/**
	 * check if the directory has change If true, we change the content of the
	 * tabpanel adding or deleting images
	 * 
	 * @param directory the path of the directory
	 * @param tp        The tabPanel that shows the content of the directory
	 */
	public static void isDirectoryContentModify(String directory, TabPanel tp) {
		if (directoryHasChange(directory)) {
			JOptionPane.showMessageDialog(null, "The content of the directoy has change. Painting again the images",
					"Warning", JOptionPane.WARNING_MESSAGE);
			addModificationDirectory(directory);

			// we paint again the images
			JPanel sp = (JPanel) tp.getComponent(0);
			JScrollPane s = (JScrollPane) sp.getComponent(1);
			JViewport jv = (JViewport) s.getComponent(0);
			ShowImages images = (ShowImages) jv.getComponent(0);

			List<String> actualImages = new ArrayList<String>();
			Utils.search(".*\\.tiff", new File(directory), actualImages);
			Collections.sort(actualImages);

			checkStillExist(images, actualImages); // check if the images of the buttons still exist

			if (actualImages.size() != 0) { // if we have new file we add them
				for (String name : actualImages) {
					// convert the format to show the image
					ImageIcon image = ShowTiff.showTiffToImageIcon(name);
					image.setDescription(name);

					// add the button
					// we create an icon with the specific measures
					ImageIcon iconoEscala = new ImageIcon(
							image.getImage().getScaledInstance(700, 700, java.awt.Image.SCALE_DEFAULT));
					JButton imageView = new JButton(iconoEscala);
					imageView.setIcon(iconoEscala);
					imageView.setName(name);

					images.getImageIcon().add(image);

					imageView.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {

							String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
							if (tp != null) {
								if (tp.indexOfTab(nombreTab) == -1) {
									ViewImagesBigger viewImageBig = new ViewImagesBigger(image, images.getImageIcon(),
											tp);
								}

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

	/**
	 * Check if the images shown still exist, the had been modify And change the
	 * images that the showImages is showing
	 * 
	 * @param images       ShowImages with the images shown
	 * @param actualImages List of the images of the directory
	 */
	public static void checkStillExist(ShowImages images, List<String> actualImages) {
		Iterator<String> imageModify = images.getLastModifyImage().keySet().iterator();
		while (imageModify.hasNext()) {

			String imaPath = imageModify.next();
			File faux = new File(imaPath);

			if (faux.exists()) { // if it still exist we look if it has been modify
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
			} else {// if it doesn´t exist we delete it from the map

				JButton deleteImage = images.getListImagesPrev().get(imaPath);
				images.remove(deleteImage);

				imageModify.remove();

			}

		}
	}

	/**
	 * We check if the images on the directory has been modified, deleted or new
	 * ones has been added in the seconds given
	 * 
	 * @param tp     The tabpanel that contains the tab with the images
	 * @param secons the seconds between calls
	 */
	public static void imagescheckWithTime(TabPanel tp, int secons) {
		ImagesTask imatask = new ImagesTask(tp);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(imatask, 0, 1000 * secons);
	}
}
