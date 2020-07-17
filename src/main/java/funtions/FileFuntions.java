package funtions;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import esferoides.Methods;
import ij.IJ;
import ij.ImageJ;
import ij.io.DirectoryChooser;
import interfaces.AlgorithmView;
import interfaces.GeneralView;
import interfaces.JPanelComparer;
import interfaces.OurProgressBar;
import interfaces.ShowImages;
import interfaces.TabPanel;
import interfaces.ViewImagesBigger;
import task.ImagesTask;

public class FileFuntions {

	private static Map<String, Long> directoryLastChange;

	public static Map<String, Long> getDirectoryLastChange() {
		return directoryLastChange;
	}

	public static void setDirectoryLastChange(Map<String, Long> directoryLastChange) {
		FileFuntions.directoryLastChange = directoryLastChange;
	}

	/**
	 * assign the plugin folder for imageJ and creates a instance of imageJ
	 */
	public static void chargePlugins() {
		new ImageJ(2);// NO_SHOW MODE

		IJ.setForegroundColor(255, 255, 0); // Yellow
//		IJ.setForegroundColor(255, 0, 0); //Red
	}

	/**
	 * Returns the Path of the selected file in the tree
	 * 
	 * @param tp The treePath created of the current directory
	 * @return The path of the selected file in the tree
	 */
	public static String getPathSelectedTreeFile(TreePath tp) {
		String path = "";

		for (int i = 0; i < tp.getPathCount(); i++) { // we generate the path
														// form the String{}
														// given
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

	public static void saveImageNoBeforeProcess(File f, String originalDir, String originalName) {
		List<String> temporalFiles = new ArrayList<String>();
		File oldFolder = new File(f.getAbsolutePath().replace(f.getName(), ""));
		String newDir = f.getAbsolutePath().replace("temporal" + File.separator + f.getName(),
				"predictions" + File.separator);

		originalName += ".*\\.*";

		Utils.search(originalName, oldFolder, temporalFiles, 1);

		String to = null;
		String extension = null;

		for (String s : temporalFiles) {
			f = new File(s);
			if (!f.getName().endsWith("xls")) {

				/*
				 * If the new file was successfully move to the predictions folder if the
				 * original file exist we delete it and change the name of the new file
				 * otherwise we only rename
				 */

				extension = FileFuntions.extensionwithoutName(s);
				to = newDir + originalName;
				if (extension.contentEquals("tiff")) {
					to += "_pred";
				}
				to += "." + extension;
				f.renameTo(new File(to));

			} else {

				ExcelActions.mergeExcels(f, originalName, new File(originalDir));

			}
		}

	}

	/**
	 * * Save the files from the temporal folder to the prediction folder exchanging
	 * the original files in the prediction folder with the temporal ones We save
	 * the tiff and roi/zip and change the corresponding row on the result excel
	 * 
	 * @param selectedFile The temporal file to save
	 * @param saveDirPath  path of the save directory
	 * @return true if the file is save false otherwise
	 */
	public static boolean saveSelectedImage(File selectedFile, String saveDirPath) {

		// We look for the tiff and zip files with the same as the selected file
		// We take the files and take out he algorithm name
		// We exchange the files in the saveDir
		boolean b = false;
		int resp = JOptionPane.showConfirmDialog(null,
				"This action will delete the current image in predition folder. Are you sure you want to proceed to save?",
				"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (resp == 0) { // if yes
			b = true;
			JOptionPane.showMessageDialog(null, "Saving the images");

			saveSelectedImageNoQuestion(selectedFile, saveDirPath);

			JOptionPane.showMessageDialog(null, "Save files in prediccion folder succed");
		}
		return b;

	}

	public static void saveSelectedImageNoQuestion(File selectedFile, String saveDirPath) {

		// We look for the tiff and zip files with the same as the selected file
		// We take the files and take out he algorithm name
		// We exchange the files in the saveDir

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

		Utils.search(oldNameNoExt, oldFolder, temporalFiles, 1);
		Utils.search(pattern, saveDir, originalFiles, 1);

		File f;
		File orFile;
		Path from;
		Path to;
		HSSFWorkbook modifyExcel;

		for (String s : temporalFiles) {
			f = new File(s);
			if (!f.getName().endsWith("xls")) {

				/*
				 * If the new file was successfully move to the predictions folder if the
				 * original file exist we delete it and change the name of the new file
				 * otherwise we only rename
				 */
				for (String oriFilePath : originalFiles) {
					extension = extensionwithoutName(s);
					if (oriFilePath.toUpperCase().endsWith(extension.toUpperCase())) {
						orFile = new File(oriFilePath);
						if (orFile.exists()) {

							from = f.toPath();
							to = orFile.toPath();
							try {

								Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, "And error ocurred and the files couldn´t be saved",
										"Error saving", JOptionPane.ERROR_MESSAGE);
							}

						}

					}

				}
			} else {

				// we change the excel row to the new one
				try {
					String path = originalPath.replace(originalName, "");
					File excelResults = new File(path + "results.xls");
					if (!excelResults.exists()) {
						path = path.substring(0, path.lastIndexOf(File.separator));
						path = path.substring(0, path.lastIndexOf(File.separator) + 1);

						excelResults = new File(path + "results.xls");
					}

					modifyExcel = new HSSFWorkbook(new FileInputStream(excelResults));
					HSSFWorkbook newdataExcel = new HSSFWorkbook(new FileInputStream(new File(s)));

					HSSFSheet sheetResult = modifyExcel.getSheet("Results");
					HSSFRow newRow = newdataExcel.getSheet("Results").getRow(1);
					extension = extensionwithoutName(originalName);
					String auxOriginal = originalName.replace("." + extension, "");

					int rowIndex = ExcelActions.findRow(sheetResult, auxOriginal);
					if (rowIndex != -1) {
						ExcelActions.changeRow(rowIndex, sheetResult, newRow);
//							FileOutputStream out = new FileOutputStream(
//									new File(originalPath.replace(originalName, "") + "results.xls"));
						FileOutputStream out = new FileOutputStream(excelResults);
						modifyExcel.write(out);

						out.close();
					}
					newdataExcel.close();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error changing the excel row", "Error saving",
							JOptionPane.ERROR_MESSAGE);
				}
			}
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
				if (file.isDirectory()) { // if it's a directory we call this
											// method else we delete it
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
			if (!name.toUpperCase().endsWith(extension.toUpperCase())) {
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

		List<String> listResult = new ArrayList<String>();
		Utils.searchFoldersName(faux, "predictions", listResult, 2);

		for (String dir : listResult) {
			faux = new File(dir);
			directoryLastChange.put(dir, faux.lastModified());
		}

	}

	private static boolean checkAllDirectChangeMapDire() {
		boolean b = false;
		for (String dire : directoryLastChange.keySet()) {
			if (directoryHasChange(dire)) {
				b = true;
				break;
			}
		}
		return b;
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
		if (tp != null && tp.getComponents().length != 0) {
			if (checkAllDirectChangeMapDire()) {
				JOptionPane.showMessageDialog(null, "The content of the directoy has change. Painting again the images",
						"Warning", JOptionPane.WARNING_MESSAGE);

				addModificationDirectory(directory);

				List<String> actualImages = new ArrayList<String>();
				Utils.search(".*\\.tiff", new File(directory), actualImages, 2);
				Collections.sort(actualImages);

				if(tp.indexOfTab("Images") != -1) {
					Utils.mainFrame.getImageTree().repainTabNoTimers(false);
				}
				else {
					// Repaint the images in the viewer
					List<ImageIcon> listImages = transformListToImageicon(actualImages);
					tp.getViewImagen().setListImages(listImages);
					tp.getViewImagen().moreActionChangeIndexIma();

					// repaint the images in the scroll view
					if (tp.indexOfTab("Images Scroll") != -1) {
						repaintImagesScrollView(actualImages, tp);
					}
				}
				
					
				
//				if (tp.getComponent(0).getClass() != JPanel.class) {
//
//					if (actualImages.size() != 0) {
//
//						// Creates de viewer
//						Utils.mainFrame.getImageTree().repaintTabPanel(false);
//					}
//
//				} else {
//
//					// Repaint the images in the viewer
//					List<ImageIcon> listImages = transformListToImageicon(actualImages);
//					tp.getViewImagen().setListImages(listImages);
//
//					// repaint the images in the scroll view
//					if (tp.indexOfTab("Images Scroll") != -1) {
//						repaintImagesScrollView(actualImages, tp);
//					}
//
//				}

			}

		}

	}

	private static void repaintImagesScrollView(List<String> imagesTiff, TabPanel tp) {

		// we paint again the images
		JPanel sp = (JPanel) tp.getComponent(0);
		JScrollPane s = (JScrollPane) sp.getComponent(1);
		JViewport jv = (JViewport) s.getComponent(0);
		ShowImages images = (ShowImages) jv.getComponent(0);

		// check if the images of the buttons still exist
		checkStillExist(images, imagesTiff, tp);

		if (imagesTiff.size() != 0) { // if we have new file we add them

			ImageIcon iconoEscala;
			JButton imageView;
			File faux;
			int height = tp.getLens().actualImageHeight();

			for (String name : imagesTiff) {
				// convert the format to show the image
				ImageIcon image = ShowTiff.showTiffToImageIcon(name);
				image.setDescription(name);

				// add the button
				// we create an icon with the specific measures
				iconoEscala = new ImageIcon(
						image.getImage().getScaledInstance(height, height, java.awt.Image.SCALE_DEFAULT));
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
								new ViewImagesBigger(image, images.getImageIcon(), tp, false);
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

		// If we dont have images we put the no file message
		if (images.getListImages().isEmpty()) {
			JTextArea j = new JTextArea();
			j.setText("There is no such file in this folder");
			j.setEnabled(false);
			j.setName("Image");

			JScrollPane scroll = new JScrollPane(j);
			tp.setComponentAt(0, scroll);
			tp.repaint();
		}

	}

	/**
	 * Check if the images shown still exist, the had been modify And change the
	 * images that the showImages is showing. Close the ViewImagesBigger.
	 * 
	 * @param images       ShowImages with the images shown
	 * @param actualImages List of the images of the directory
	 * @param tp           The current tabPanel
	 */
	public static void checkStillExist(ShowImages images, List<String> actualImages, TabPanel tp) {
		Iterator<String> imageModify = images.getLastModifyImage().keySet().iterator();
		while (imageModify.hasNext()) {

			String imaPath = imageModify.next();
			File faux = new File(imaPath);
			int height = tp.getLens().actualImageHeight();

			if (faux.exists()) { // if it still exist we look if it has been
									// modify
				if (faux.lastModified() != images.getLastModifyImage().get(imaPath)) {

					JOptionPane.showMessageDialog(null, "The image " + faux.getName() + " has change", "Warning",
							JOptionPane.WARNING_MESSAGE);

					JButton imageButton = images.getListImagesPrev().get(imaPath);

					ImageIcon ima = ShowTiff.showTiffToImageIcon(imaPath);
					ima.setDescription(imaPath);

					images.getImageIcon().set(images.getListImages().indexOf(imaPath), ima);

					ima = new ImageIcon(ima.getImage().getScaledInstance(height, height, java.awt.Image.SCALE_DEFAULT));

					ima.setDescription(imaPath);
					imageButton.setIcon(ima);
					imageButton.repaint();

					images.getListImagesPrev().put(imaPath, imageButton);
					images.getLastModifyImage().put(imaPath, faux.lastModified());

					// if there is tabs with viewImagesBigger we close them and
					// get the new list of
					// tiff images
					Component[] com = tp.getComponents();
					for (Component component : com) {
						if (component.getClass().equals(JPanelComparer.class)) {
							tp.remove(component);
						}
					}

				}
			} else {// if it doesn't exist we delete it from the map

				JButton deleteImage = images.getListImagesPrev().get(imaPath);
				images.remove(deleteImage);

				images.getImageIcon().remove(images.getListImages().indexOf(imaPath));
				images.getListImages().remove(imaPath);
				images.getListImagesPrev().remove(imaPath);

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
	public static Timer imagescheckWithTime(TabPanel tp, int secons) {
		ImagesTask imatask = new ImagesTask(tp);
		Timer temporizador = new Timer();

		temporizador.scheduleAtFixedRate(imatask, 0, 1000 * secons);

		return temporizador;
	}

	/**
	 * Function that checks if there is tiff and roi files in the folder but not in
	 * the predictions folder In case of finding Tiff and roi files in the folder it
	 * ask you if you want to move the files to a prediction folder
	 * 
	 * @param folder        the folder to check if have tiff and roi files
	 * @param nameFileNoExt name of the file without extension of the file we wants
	 *                      to find it's tiff in the predictions folder if "" all
	 *                      the files
	 * @return List of strings with the paths of tiff files in the folder given
	 */
	public static List<String> checkTiffNotPredictionsFolder(File folder, String nameFileNoExt) {

		List<String> listImages = new ArrayList<String>();
		File predictionsDir = new File(folder.getAbsolutePath() + File.separator + "predictions");

		if (!folder.getAbsolutePath().endsWith("predictions")) {

			if (predictionsDir.exists()) {
				if (predictionsDir.list().length != 0) {
					Utils.searchDirectory(".*\\.tiff", predictionsDir, listImages);
					// check if there is more outside the predictions folder to
					// ask to move there
					moveTifffromParentToPredictions(folder, listImages, predictionsDir);
				} else {
					moveTifffromParentToPredictions(folder, listImages, predictionsDir);
				}

			} else {
				if (folder.getAbsolutePath().endsWith("temporal")) {

					if (nameFileNoExt == "") {
						Utils.searchDirectory(".*\\.tiff", folder, listImages);
					} else {
						nameFileNoExt = nameFileNoExt.replace("_pred", "");
						Utils.searchDirectory(nameFileNoExt + ".*\\.tiff", folder, listImages);
					}

				} else {
					moveTifffromParentToPredictions(folder, listImages, predictionsDir);
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

		if (!listImages.isEmpty()) {

			Utils.searchDirectory(".*\\.zip", folder, listImages);

			JOptionPane.showMessageDialog(null, "There are tiff files in this folder, but they aren´t in a predictions"
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

					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"An error occurred while saving the images in the predicctions folder", "Error saving",
							JOptionPane.ERROR_MESSAGE);
					removeAllToOriginalFolder(newPAth, new File(to.toString()));
				}
			}
			listImages.clear();
			Utils.searchDirectory(".*\\.tiff", predictionsDir, listImages);
		} else {
			Utils.searchDirectory(".*\\.tiff", predictionsDir, listImages);
		}

	}

	/**
	 * From a map return the key giving the value, there is no repeated values
	 * 
	 * @param <K>   Type of the key
	 * @param <V>   Type of the value
	 * @param map   Map to search the key
	 * @param value the value we wants to know the key
	 * @return the key of the current value
	 */
	public static <K, V> K getKey(Map<K, V> map, V value) {
		for (K key : map.keySet()) {
			if (value.equals(map.get(key))) {
				return key;
			}
		}
		return null;
	}

	/**
	 * 
	 * GEts the key of a map, giving the description of a button, repeted values in
	 * the map
	 * 
	 * @param map   map to search the key
	 * @param value description of the button
	 * @return the key of a button
	 */
	public static String getKeyFRomButtonDescription(Map<String, JButton> map, String value) {
		JButton valueButton = null;

		for (JButton bu : map.values()) {
			if (bu.getName() == value) {
				valueButton = bu;
				break;
			}
		}

		return getKey(map, valueButton);
	}

	/**
	 * Moves all the files from the dirpredictions to the temporal folder
	 * 
	 * @param dirPredictions current folder
	 * @param tempoFolder    new folder
	 */
	public static void removeAllToOriginalFolder(String dirPredictions, File tempoFolder) {

		File dirPre = new File(dirPredictions);
		if (dirPre.exists() && dirPre.listFiles().length != 0) {
			File[] files = dirPre.listFiles();
			for (File file : files) {
				file.renameTo(new File(file.getAbsolutePath().replace(dirPredictions, tempoFolder.getAbsolutePath())));
			}
		}
	}

	/**
	 * Moves the files to the predictions folder, changes the name of the selected
	 * files (Jbutton) to the original ones checks if there is already a file with
	 * that name in that case delete it and move the other from the temporal folder.
	 * 
	 * @param dirPredictions      path of the images predicted
	 * @param originalNewSelected map of string-JButton that contains the name of
	 *                            the image- button selected with the image selected
	 *                            to save
	 */
	public static void changeToriginalNameAndFolder(String dirPredictions, Map<String, JButton> originalNewSelected) {

		File dirPre = new File(dirPredictions);

		if (dirPre.exists() && dirPre.listFiles().length != 0) {
			File[] files = dirPre.listFiles();
			String nameNoEx = "";
			String originalDirectoryPath = "";
			File originalFolder = null;

			for (File file : files) {
				nameNoEx = FileFuntions.namewithoutExtension(file.getAbsolutePath());
				if (nameNoEx.endsWith("_pred")) {
					nameNoEx = nameNoEx.replace("_pred", "");
				}
				originalDirectoryPath = originalNewSelected.get(nameNoEx).getName();
				originalFolder = new File(originalDirectoryPath);
				originalFolder = new File(originalFolder.getAbsolutePath().replace(originalFolder.getName(), ""));

				if (!originalFolder.exists()) {
					originalFolder.mkdir();
				}

				if (FileFuntions.extensionwithoutName(file.getAbsolutePath()).equals("zip")) {
					file.renameTo(new File(originalNewSelected.get(nameNoEx).getName().replace("_pred.tiff", ".zip")));
				} else {
					file.renameTo(new File(originalNewSelected.get(nameNoEx).getName()));
				}

			}

			if (dirPre.listFiles().length == 0) {
				dirPre.delete();

			}

			ExcelActions.unMergeExcel(dirPredictions.replace("predictions", ""), originalNewSelected);
		}
	}

	/**
	 * 
	 * Searchs if in a folder there is original files (nd2 or tif)
	 * 
	 * @param folder folder to search original files
	 * @return true if there is original images (nd2 or tif) in the folder
	 */
	public static boolean isOriginalImage(File folder) {
		boolean originalIma = false;
		// check if the folder contains nd2 images
		List<String> listImages = new ArrayList<String>();
		List<String> listExtensions = FileFuntions.getExtensions();

		for (String ext : listExtensions) {
			if (!ext.equals("tiff") || !ext.equals("jpeg")) {

				Utils.search(".*\\." + ext, folder, listImages, 1);

				if (ext.equals("jpg")) {
					ext = "jpeg";
					Utils.search(".*\\." + ext, folder, listImages, 1);
				}

				if (listImages.size() != 0) {
					originalIma = true;
					break;
				}
			}
		}

//		Utils.searchDirectory(".*\\."+ext, folder, listImages);
//		if (listImages.size() != 0) {
//			originalIma = true;
//		} else {
//			Utils.searchDirectory(".*\\.tif", folder, listImages);
//			if (listImages.size() != 0) {
//				originalIma = true;
//			}
//		}
		return originalIma;
	}

	/**
	 * If a new version of the jar is find and the user wants to update, calls the
	 * updateJar and kills the execution of this jar
	 * 
	 * @param callFromMain true if the method is call in the main method false if it
	 *                     is call in the JMEnu
	 */
	public static void createUpdater(Boolean callFromMain) {
		boolean newVersion = false;
		PropertiesFileFuntions prop = new PropertiesFileFuntions();

		String pathJArUpdater = getCurrentPAth() + File.separator + "updater" + File.separator
				+ "jarUpdater-1.0-SNAPSHOT-jar-with-dependencies.jar";

		String urlVersion = prop.getProp().getProperty("urlVersionFile");
		String currentVersion = prop.getProp().getProperty("version");

		// We check if there is a new version of the app
		newVersion = checkNewVersionJAr(urlVersion, currentVersion);

		if (newVersion) {// if there is a new version we ask to update or
							// not

			JCheckBox rememberChk = new JCheckBox("Don't show this message again.");
//			 Button b=new Button("Details >>>");
			String msg = "A new update has been detected. \n Do you want to dowload it?";
			Object[] msgContent = { msg, rememberChk };

//
//			 Object[] msgContent = { msg, rememberChk ,b};
//			 b.addActionListener(new ActionListener() {
//			
//			 @Override
//			 public void actionPerformed(ActionEvent e) {
//			
//			VersionInfo veinf= new VersionInfo(null);
//			veinf.inicialice(currentVersion, readversion(urlVersion));
//			 }
//			 });

			int op = 0;

			if (callFromMain) {
				op = JOptionPane.showConfirmDialog(null, msgContent, "Alert!", JOptionPane.YES_NO_OPTION);
				boolean remember = rememberChk.isSelected();
				if (remember) {
					if (op != -1) {
						URL urlUpdater = getProgramProps();
						if (urlUpdater != null) {
							PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);
							propUpdater.getProp().setProperty("showUpdater", String.valueOf(!remember));

							try {

								propUpdater.getProp().store(new FileOutputStream(urlUpdater.getPath()),
										"Show the updater");
								JOptionPane.showMessageDialog(null,
										"You can update the program any time in: \n Help -> Update");

							} catch (IOException e) {

								e.printStackTrace();
								JOptionPane.showMessageDialog(null,
										"Error trying to update. \n Check if you have the update folder with its files whit this jar");
							}

						}
					}
				}

			} else {

				op = JOptionPane.showConfirmDialog(null, msg, "Alert!", JOptionPane.YES_NO_OPTION);
			}

			if (op == 0) {
				// if yes we call the updater jar
				try {
					Runtime.getRuntime().exec("java -jar " + "\"" + pathJArUpdater + "\"");
//					Process process = Runtime.getRuntime().exec("java -jar " + "\"" + pathJArUpdater + "\"");
//					InputStream inputstream = process.getInputStream();
//					BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream);

					System.exit(0);// stop the execution of this jar
				} catch (IOException e) {

					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Error trying to update. \n Check if you have the update folder with its files whit this jar");

				}

			}

		} else {
			if (!callFromMain) {
				JOptionPane.showMessageDialog(Utils.mainFrame, "You already have the newest version");
			}
		}

	}

	/**
	 * Checks if there is a new version of this jar
	 * 
	 * @param urlVerion         the url with the newest version of this jar in the
	 *                          web
	 * @param currentJarVersion the version of this jar
	 * @return true if there is a new version
	 */
	private static boolean checkNewVersionJAr(String urlVerion, String currentJarVersion) {
		boolean newJAr = false;
		String newJArversion = "";
		double cVersion = 0;
		double nVersion = 0;

		newJArversion = (readversion(urlVerion)).replace("-SNAPSHOT", ""); // gets the version in the web
		currentJarVersion = currentJarVersion.replace("-SNAPSHOT", "");

		String[] newVerSplit = newJArversion.split("\\.");
		String[] cuVerSplit = currentJarVersion.split("\\.");

		if (newVerSplit.length > 2) {
			newJArversion = newVerSplit[0] + "." + newVerSplit[1];
			for (int i = 2; i < newVerSplit.length; i++) {
				newJArversion += newVerSplit[i];
			}

		}

		if (cuVerSplit.length > 2) {
			currentJarVersion = cuVerSplit[0] + "." + cuVerSplit[1];
			for (int i = 2; i < cuVerSplit.length; i++) {
				currentJarVersion += cuVerSplit[i];
			}
		}
		cVersion = Double.parseDouble(currentJarVersion);
		nVersion = Double.parseDouble(newJArversion);

		System.out.println("Current version  " + cVersion);
		System.out.println("New version  " + nVersion);
		System.out.println("MAyor version  " + (nVersion > cVersion));

		// compare the versions in case they are different and the version from
		// the url is not "" there is a new version
		if (!newJArversion.equals(currentJarVersion) && !newJArversion.equals("") && (nVersion > cVersion)) {
			newJAr = true;
		}

		return newJAr;
	}

	/**
	 * Reads the version of the jar in the url given. This url is a txt only with
	 * the version
	 * 
	 * @param urlVerion url where the file with the newest version is
	 * @return the current version in the web
	 */
	public static String readversion(String urlVerion) {
		String line = "";
		// create the url
		URL url;
		try {
			url = new URL(urlVerion);
			// open the url stream, wrap it an a few "readers"
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			// read the file
			String linea;
			while ((linea = reader.readLine()) != null) {
				line = linea;// gets the line with the version
			}

			// close our reader
			reader.close();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return line;

	}

	public static List<String> readFile(String urlFile) {
		List<String> line = new ArrayList<String>();
		// create the url
		URL url;
		try {
			url = new URL(urlFile);
			// open the url stream, wrap it an a few "readers"
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

			// read the file
			String linea;
			while ((linea = reader.readLine()) != null) {
				line.add(linea);
			}

			// close our reader
			reader.close();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return line;

	}

	/**
	 * Gets the current path where this jar is
	 * 
	 * @return the current path of this jar
	 */
	public static String getCurrentPAth() {
		String location = "";
		CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
		File jarFile;
		try {
			jarFile = new File(codeSource.getLocation().toURI().getPath());
			location = jarFile.getParentFile().getPath();
		} catch (URISyntaxException e1) {

			e1.printStackTrace();
		}
		return location;
	}

	/**
	 * url of the program properties for the general program or null in case of
	 * error
	 * 
	 * @return url of the program properties for the general program or null in case
	 *         of error
	 */
	public static URL getProgramProps() {
		try {
			return new URL("file:///" + FileFuntions.getCurrentPAth() + File.separator + "updater" + File.separator
					+ "program.properties");
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if the update pop out will be shown
	 */
	public static void CheckIfUpdate() {
		URL urlUpdater = getProgramProps();
		if (urlUpdater != null) {
			PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);
			Boolean showUpdater = Boolean.valueOf(propUpdater.getProp().getProperty("showUpdater"));
			if (showUpdater) {

				createUpdater(true); // see if there is a new version of the
										// app
			}
		}

	}

	public static void closeAlgorithmViewWindows() {
		// Closed all the windows that aren't the main frame
		Window[] s = Window.getWindows();
		for (Window window : s) {
			if (window.getClass().equals(AlgorithmView.class)) {
				AlgorithmView al = (AlgorithmView) window;

				if (al.getT().isAlive()) {
					al.getT().interrupt();
				}

				window.dispose();
			}
		}

	}

	public static boolean windowAlgoWiewOfFile(File f) {
		boolean alreadyAlView = false;
		System.gc();
		Window[] s = Window.getWindows();
		for (Window window : s) {
			if (!window.isVisible()) {
				window.dispose();
			} else {
				if (window.getClass().equals(AlgorithmView.class)) {
					AlgorithmView al = (AlgorithmView) window;

					if (al.getImage().equals(f)) {
						alreadyAlView = true;
						al.toFront();
						break;
					}

				}
			}
		}
		return alreadyAlView;
	}

	public static void changeDirectory(String path, boolean tree) {

		closeAlgorithmViewWindows();

		// Close the imageJ windows
		if (IJ.isWindows()) {
			IJ.run("Close All");
			if (IJ.isResultsWindow()) {
				IJ.selectWindow("Results");
				IJ.run("Close");
				IJ.selectWindow("ROI Manager");
				IJ.run("Close");
			}

		}
		if (tree) {
			Utils.callProgram(path);
		} else {
			Utils.mainFrame.paintMainFRame(path);
		}

//		JOptionPane.showMessageDialog(Utils.mainFrame, "Directory changed to " + path);

	}

	/**
	 * Action to perform in order to change the current directory
	 */
	public static void changeDirectory() {
		GeneralView mainFrame = Utils.mainFrame;

//		String text = "The current directory is: \n" + mainFrame.getDir() + "\n Do you what to change it?";
//		int op = JOptionPane.showConfirmDialog(mainFrame, text, "Change directory", JOptionPane.YES_NO_OPTION);
//
//		if (op == 0) {

		DirectoryChooser dc = new DirectoryChooser("Select new directory");

		if (dc.getDirectory() != null) {
			// Desactivate the menu options until the panel is paint
			FileFuntions.changeDirectory(dc.getDirectory(), false);

		} else {

			JOptionPane.showMessageDialog(mainFrame, "Directory not changed");

		}

//		} else {
//
//			JOptionPane.showMessageDialog(mainFrame, "Directory not changed");
//
//		}

	}

	public static void inicializeGeneralViewDirectory() {
//		 Choose the directory to work with
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		if (dc.getDirectory() != null) { // if a directory has been chosen

			if (dc.getDirectory().endsWith("predictions")) {
				// if the directory is the predictions directory, we work with
				// the parent that contains the original images
				Utils.callProgram(dc.getDirectory().replace("predictions", ""));

			} else {
				if (dc.getDirectory().endsWith("predictions" + File.separator)) {
					Utils.callProgram(dc.getDirectory().replace("predictions" + File.separator, ""));
				} else {
					Utils.callProgram(dc.getDirectory());
				}

			}
		} else {
			JOptionPane.showMessageDialog(Utils.mainFrame, "Nothing to do. No directory choosen");
		}
	}

	/**
	 * Action to add more allowed extensions
	 * 
	 */
	public void addFileExtension() {
		PropertiesFileFuntions prop = new PropertiesFileFuntions();
		String text = "The current extensions are: \n";
		String ext = prop.getProp().getProperty("imageFilesExtensions");
		if (ext != null) {
			List<String> list = getExtensions();

			for (String s : list) {
				text += s + " \n";
			}

			String seleccion = JOptionPane.showInputDialog(text);
			if (seleccion != null) {
				if (seleccion != "") {
					ext += "," + seleccion;
					prop.getProp().setProperty("imageFilesExtensions", ext);
					try {
						FileOutputStream out = new FileOutputStream(prop.getPath().getFile());
						prop.getProp().store(out, null);
						out.close();
					} catch (IOException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error while doing the required action", "Error saving",
								JOptionPane.ERROR_MESSAGE);
					}

					addFileExtension();

				}
			}
		}
	}

	/**
	 * GEts the allowed file extension from the properties file
	 * 
	 * @return the list of allowed file extensions
	 */
	public static List<String> getExtensions() {
		List<String> list = null;
		PropertiesFileFuntions prop = new PropertiesFileFuntions();
		String ext = prop.getProp().getProperty("imageFilesExtensions");
		if (ext != null) {
			String[] extensionSplit = ext.split(",");
			list = Arrays.asList(extensionSplit);
		}
		return list;
	}

	public static void changeDirOrNot() {
		if (Utils.getCurrentDirectory() != null) {
			changeDirectory();
		} else {
			inicializeGeneralViewDirectory();
		}
	}

	public static void detectAlgoImageMEnu() {

		JFileChooser chooser = new JFileChooser(Utils.getCurrentDirectory());
		chooser.setDialogTitle("Select an image of the current directory or subdirectories");
		List<String> exten = FileFuntions.getExtensions();
		FileNameExtensionFilter filter;

		for (String name : exten) {
			filter = new FileNameExtensionFilter(name, name);
			chooser.setFileFilter(filter);
		}
		chooser.setVisible(true);
		int returnVal = chooser.showOpenDialog(Utils.mainFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File image = chooser.getSelectedFile();
			if (image != null) {
				if (!image.getAbsolutePath().contains(Utils.getCurrentDirectory())) {
					JOptionPane.showMessageDialog(Utils.mainFrame,
							"Please select an image of the current directory or subdirectoties");
					detectAlgoImageMEnu();
				} else {
					new AlgorithmView(image);
				}
			}
		}

	}

	public static void saveAlgorithmConfi(String selectedItemFluo, String selectedItemTif, String selectedItemNd2,
			String selectedItemJPG) {

		URL urlUpdater = getProgramProps();
		if (urlUpdater != null) {
			PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);

			String fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
			String tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
			String nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
			String jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");

			if (fluoSave == null || !fluoSave.equals(selectedItemFluo)) {
				propUpdater.getProp().setProperty("SelectFluoAlgo", selectedItemFluo);
			}
			if (tifSave == null || !tifSave.equals(selectedItemTif)) {
				propUpdater.getProp().setProperty("SelectTifAlgo", selectedItemTif);
			}
			if (nd2Save == null || !nd2Save.equals(selectedItemNd2)) {
				propUpdater.getProp().setProperty("SelectNd2Algo", selectedItemNd2);
			}
			if (jpgSave == null || !jpgSave.equals(selectedItemJPG)) {
				propUpdater.getProp().setProperty("SelectJpgAlgo", selectedItemJPG);
			}

			try {

				propUpdater.getProp().store(new FileOutputStream(urlUpdater.getPath()), "Selected algoritms");

			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	public static boolean changedCombox(String selectedItemFluo, String selectedItemTif, String selectedItemNd2,
			String selectedItemJPG) {
		URL urlUpdater = getProgramProps();
		if (urlUpdater != null) {
			PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);

			String fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
			String tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
			String nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
			String jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");

			if (fluoSave == null || tifSave == null || nd2Save == null || jpgSave == null) {
				return true;
			} else {
				if (!fluoSave.equals(selectedItemFluo)) {
					return true;
				}
				if (!tifSave.equals(selectedItemTif)) {
					return true;
				}
				if (!nd2Save.equals(selectedItemNd2)) {
					return true;
				}
				if (!jpgSave.equals(selectedItemJPG)) {
					return true;
				}
			}

		}
		return false;
	}

	public static void openUserManual() {

		try {
			PropertiesFileFuntions prop = new PropertiesFileFuntions();
			String currentVersion = prop.getProp().getProperty("version");
			String pathpdf = getCurrentPAth() + File.separator + "updater" + File.separator + "Manual_" + currentVersion
					+ ".pdf";
			File filePDF = new File(pathpdf);

			if (!filePDF.exists()) {
				JOptionPane.showMessageDialog(Utils.mainFrame,
						"The manual file not exist or the current file is not the last version. "
								+ "\n Downloading the new version please wait.");

				OurProgressBar pb = new OurProgressBar(Utils.mainFrame, false);
				Thread t = new Thread() {
					public void run() {

						String urlPDF = prop.getProp().getProperty("urlUserManual");
						urlPDF += currentVersion + ".pdf";
						String location = getCurrentPAth() + File.separator + "updater";
						URL url;
						try {
							url = new URL(urlPDF);
							File aux = Utils.download(url, location);

							if (aux != null) {
								if (aux.exists()) {
									openPDF(aux);
//									Process p = Runtime.getRuntime().exec(
//											"rundll32 SHELL32.DLL," + "ShellExec_RunDLL " + aux.getAbsolutePath());
								} else {
									JOptionPane.showMessageDialog(Utils.mainFrame, "Error downloading the file");
								}
							} else {
								JOptionPane.showMessageDialog(Utils.mainFrame, "Error downloading the file");
							}
							pb.dispose();
						} catch (MalformedURLException e) {

							e.printStackTrace();
						}
					}
				};

				t.start();

			} else {
//				Process p = Runtime.getRuntime()
//						.exec("rundll32 SHELL32.DLL," + "ShellExec_RunDLL " + filePDF.getAbsolutePath());

				openPDF(filePDF);
			}

		} catch (Exception ev) {
			JOptionPane.showMessageDialog(null, "The file can not be open ," + " maybe it was deleted ", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void openPDF(File filePDF) {
		if (Desktop.isDesktopSupported()) {
			try {

				Desktop.getDesktop().open(filePDF);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null,
						"The file can not be open , check if you have a program that opens PDF files", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public static void openAboutSection() {

		PropertiesFileFuntions prop = new PropertiesFileFuntions();

		String version = prop.getProp().getProperty("version");
		String about = prop.getProp().getProperty("about");

		JDialog jDia = new JDialog(Utils.mainFrame);
		jDia.setTitle("About Spheroid APP");

		JLabel versionTextLabel = new JLabel("Version: ");
		JLabel versionLabel = new JLabel(version);
		JLabel aboutTextLabel = new JLabel("About: ");
//		JLabel aboutLabel = new JLabel(about);
		JTextPane aboutLabel = new JTextPane();
		aboutLabel.setEditable(false);
		aboutLabel.setText(about);
		JLabel name = new JLabel("SPHEROID APP");

		JPanel vePAnel = new JPanel(new GridLayout(0, 2));
		JPanel abPAnel = new JPanel(new GridLayout(2, 2));

		vePAnel.add(versionTextLabel);
		vePAnel.add(versionLabel);
		abPAnel.add(aboutTextLabel);
		abPAnel.add(aboutLabel);

		JPanel principalPanel = new JPanel(new GridLayout(4, 0));
		principalPanel.add(name);
		principalPanel.add(vePAnel);
		principalPanel.add(abPAnel);

		jDia.add(principalPanel);
		jDia.setVisible(true);
		jDia.pack();
	}

	public static boolean checkSavedAlgoPropertiesFile(String fluoSave, String tifSave, String nd2Save,
			String jpgSave) {
		boolean change = false;
		if (fluoSave == null || tifSave == null || nd2Save == null || jpgSave == null) {
			String hv2 = Methods.getAlgorithms()[3];
			String tbg = Methods.getAlgorithms()[5];
			String tp = Methods.getAlgorithms()[7];

			FileFuntions.saveAlgorithmConfi(hv2, hv2, tbg, tp);
			change = true;

		} else {
			List<String> listax = Arrays.asList(Methods.getAlgorithms());
			if (!listax.contains(fluoSave) || !listax.contains(tifSave) || !listax.contains(nd2Save)
					|| !listax.contains(jpgSave)) {
				String hv2 = Methods.getAlgorithms()[3];
				String tbg = Methods.getAlgorithms()[5];
				String tp = Methods.getAlgorithms()[7];

				FileFuntions.saveAlgorithmConfi(hv2, hv2, tbg, tp);
				change = true;
			}
		}
		return change;
	}

	public static List<ImageIcon> transformListToImageicon(List<String> pathImages) {
		List<ImageIcon> imageIcon = new ArrayList<ImageIcon>();
		ImageIcon image;
		for (String ima : pathImages) {
			image = ShowTiff.showTiffToImageIcon(ima);
			image.setDescription(ima);
			imageIcon.add(image);
		}
		return imageIcon;
	}
}
