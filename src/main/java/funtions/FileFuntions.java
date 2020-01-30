package funtions;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import ij.IJ;
import ij.ImageJ;
import interfaces.ShowImages;
import interfaces.TabPanel;
import interfaces.ViewImagesBigger;
import task.ImagesTask;

public class FileFuntions {

	private static Map<String, Long> directoryLastChange;
	// private static List<String> pluginNames;

	/**
	 * assign the plugin folder for imageJ and creates a instance of imageJ
	 */
	public static void chargePlugins() {
		PropertiesFileFuntions prop = new PropertiesFileFuntions();
		prop.cheeckJarDirectoryChange();
		System.setProperty("plugins.dir", prop.getProp().getProperty("jarDirectory"));

		 new ImageJ(2);// NO_SHOW MODE
		// imageJFrame.setVisible(false);

		IJ.setForegroundColor(255, 0, 0);

		// setPluginNames(new ArrayList<>());

		// addMenuItem(imageJFrame.getMenuBar().getMenu(5));

	}

//	
//	public static void addMenuItem(Menu menu) {
//		for (int i = 0; i < menu.countItems(); i++) {
//			String claString=menu.getItem(i).getClass().getName();
//			
//			if(claString=="java.awt.Menu") {
//				addMenuItem((Menu)menu.getItem(i));
//			}else {
//				getPluginNames().add(menu.getLabel()+" > "+menu.getItem(i).getLabel());
//			}
//			
//			
//		}
//		
//	}

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
			String oldNameNoExt = namewithoutExtension(selectedFile.getAbsolutePath());
			oldNameNoExt += ".*\\.*";
			oldNameNoExt = oldNameNoExt.replace("_pred", "");

			Utils.search(oldNameNoExt, oldFolder, temporalFiles);
			Utils.search(pattern, saveDir, originalFiles);

			File f;
			File orFile;
			Path from;
			Path to;
			HSSFWorkbook modifyExcel;

			for (String s : temporalFiles) {
				f = new File(s);
				if (!f.getName().endsWith("xls")) {

					/*
					 * If the new file was succesfully move to the predicctions folder if the
					 * original file exist we delete it and change the name of the new file
					 * otherwise we only rename
					 */
					for (String oriFilePath : originalFiles) {
						extension = extensionwithoutName(s);
						if (oriFilePath.endsWith(extension)) {
							orFile = new File(oriFilePath);
							if (orFile.exists()) {
								// f.renameTo(new File(f.getAbsolutePath().replace(f.getName(),
								// orFile.getName())));
								from = f.toPath();
								to = orFile.toPath();
								try {
									// orFile.delete();
									Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									JOptionPane.showMessageDialog(null,
											"And error ocurred and the files couldn´t be saved", "Error saving",
											JOptionPane.ERROR_MESSAGE);
								}

							}

						}

					}
				} else {

					// we change the excel row to the new one
					try {

						modifyExcel = new HSSFWorkbook(
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
	 * @param folder The folder to delete
	 */
	public static void deleteFolder(File folder) {

		if (folder.exists()) { // if exist
			File[] files = folder.listFiles();
			for (File file : files) { // if it isn't empty we delete it's files
				if (file.isDirectory()) { // if it's a directory we call this method else we delete it
					deleteFolder(file);
				} else {
					file.delete();
				}

			}
			folder.delete(); // we delete it
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
		if (f.getName().contains(".")) {
			return f.getName().split("\\.")[1];
		} else {
			return "";
		}

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
		File faux = new File(directory);
		directoryLastChange.put(directory, faux.lastModified());

	}

	/**
	 * check if a directory has been modify
	 * 
	 * @param directory The path of the directory
	 * @return true if the directory has been modify
	 */
	public static boolean directoryHasChange(String directory) {
		File direFile = new File(directory);
		return direFile.lastModified() != directoryLastChange.get(directory);
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

			checkStillExist(images, actualImages, tp); // check if the images of the buttons still exist

			if (actualImages.size() != 0) { // if we have new file we add them

				ImageIcon iconoEscala;
				JButton imageView;
				File faux;

				for (String name : actualImages) {
					// convert the format to show the image
					ImageIcon image = ShowTiff.showTiffToImageIcon(name);
					image.setDescription(name);

					// add the button
					// we create an icon with the specific measures
					iconoEscala = new ImageIcon(
							image.getImage().getScaledInstance(700, 700, java.awt.Image.SCALE_DEFAULT));
					imageView = new JButton(iconoEscala);
					imageView.setIcon(iconoEscala);
					imageView.setName(name);
					images.getImageIcon().add(image);
					imageView.repaint();

					imageView.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {

							String nombreTab = "ImageViewer " + (new File(image.getDescription()).getName());
							if (tp != null) {
								if (tp.indexOfTab(nombreTab) == -1) {
									 new ViewImagesBigger(image, images.getImageIcon(),
											tp,false);
								}

							}

						}
					});

					images.getListImagesPrev().put(name, imageView);

					faux = new File(name);
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
	public static void checkStillExist(ShowImages images, List<String> actualImages, TabPanel tp) {
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
					ima.setDescription(imaPath);
					imageButton.setIcon(ima);
					imageButton.repaint();

					images.getListImagesPrev().put(imaPath, imageButton);
					images.getLastModifyImage().put(imaPath, faux.lastModified());

				}
			} else {// if it doesn´t exist we delete it from the map

				JButton deleteImage = images.getListImagesPrev().get(imaPath);
				images.remove(deleteImage);

				imageModify.remove();

			}
			actualImages.remove(imaPath);

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

	/**
	 * Function that checks if there is tiff and roi files in the folder but not in
	 * the predictions folder In case of finding Tiff and roi files in the folder it
	 * ask you if you want to move the files to a prediction folder
	 * 
	 * @param folder the folder to check if have tiff and roi files
	 * @return List<String> the list of tiff files in the folder given
	 */
	public static List<String> checkTiffNotPredictionsFolder(File folder) {

		List<String> listImages = new ArrayList<String>();
		File predictionsDir = new File(folder.getAbsolutePath() + File.separator + "predictions");

		if (!folder.getAbsolutePath().endsWith("predictions")) {

			if (predictionsDir.exists()) {
				if (predictionsDir.list().length != 0) {
					Utils.searchDirectory(".*\\.tiff", predictionsDir, listImages);
					// check if there is more outside the predictions folder to ask to move there
					moveTifffromParentToPredictions(folder, listImages, predictionsDir);
				} else {
					moveTifffromParentToPredictions(folder, listImages, predictionsDir);
				}

			} else {
				if (folder.getAbsolutePath().endsWith("temporal")) {
					Utils.searchDirectory(".*\\.tiff", folder, listImages);
				}

			}
		} else {
			Utils.searchDirectory(".*\\.tiff", folder, listImages);

		}

		return listImages;
	}

	/**
	 * Moves the tiff and roi files from the folder to the predictionsDir
	 * 
	 * @param folder         the folder to check if have tiff and roi files
	 * @param listImages     the list of tiff files
	 * @param predictionsDir the folder predictions
	 */
	public static void moveTifffromParentToPredictions(File folder, List<String> listImages, File predictionsDir) {

		listImages.clear();

		Utils.searchDirectory(".*\\.tiff", folder, listImages);
		Utils.searchDirectory(".*\\.zip", folder, listImages);
		if (!listImages.isEmpty()) {
			JOptionPane.showMessageDialog(null, "There are tiff files in this folder, but tey aren´t in a predictions"
					+ " folder. Moving Tiff and Zip files to predictions folder");

			if (!predictionsDir.exists()) {
				predictionsDir.mkdir();
			}

			File file;
			String newPAth;
			Path from;
			Path to;

			for (String s : listImages) {
				file = new File(s);
				newPAth = s.replace(file.getName(), "predictions" + File.separator + file.getName());
				from = Paths.get(s);
				to = Paths.get(newPAth);

				try {
					Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "An error occurred while saving the images in the predicctions folder", "Error saving",
							JOptionPane.ERROR_MESSAGE);
					
				}
			}
			listImages.clear();
			Utils.searchDirectory(".*\\.tiff", predictionsDir, listImages);
		} else {
			Utils.searchDirectory(".*\\.tiff", predictionsDir, listImages);
		}

	}
	
	
	
	public static <K, V> K getKey(Map<K, V> map, V value) {
		for (K key : map.keySet()) {
			if (value.equals(map.get(key))) {
				return key;
			}
		}
		return null;
	}
	public static  String getKeyFRomButtonDescription(Map<String, JButton> map,String value) {
		JButton valueButton=null;
		
		for (JButton bu: map.values()) {
			if(bu.getName()==value) {
				valueButton=bu;
				break;
			}
		}
		
		return getKey(map, valueButton);
	}



	public static void removeAllToOriginalFolder(String dirPredictions, File tempoFolder) {
		// TODO Auto-generated method stub
		
	}

	

	public static void changeToriginalName(File tempoFolder, Map<String, JButton> originalNewSelected) {
		// TODO Auto-generated method stub
		
	}
	
   
	

//	public static List<String> getPluginNames() {
//		return pluginNames;
//	}
//
//	public static void setPluginNames(List<String> pluginNames) {
//		FileFuntions.pluginNames = pluginNames;
//	}
}
