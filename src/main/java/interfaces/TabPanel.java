package interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import esferoides.Methods;
import funtions.ExcelActions;
import funtions.FileFuntions;
import funtions.Utils;

/**
 * JTabbedPane for showing the different elements that a directory contains
 * 
 * @author Yolanda
 *
 */
public class TabPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private Map<Integer, Long> excelModificationIndexTab;
	private Map<Integer, File> indexTabExcel;
	private String dir;
	private Map<String, JButton> originalNewSelected;
	private ShowImages images;
	private ViewImagesBigger viewImagen;
	private LensMEnuButtons lens;
	private Thread t;
	private RoiModifyView roiModifyTab = null;
	private boolean alreadyprocessed = false;

	public TabPanel(String directory, boolean selectAlgo) {

		if (selectAlgo) {

			selectAlgorithmImagesTab(directory);

		} else {
			alreadyImageTiffFolderTab(directory);
		}

	}

	// GETTERS Y SETTERS

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public Map<Integer, Long> getExcelModificationIndexTab() {
		return excelModificationIndexTab;
	}

	public void setExcelModificationIndexTab(Map<Integer, Long> excelModificationIndexTab) {
		this.excelModificationIndexTab = excelModificationIndexTab;
	}

	public Map<Integer, File> getIndexTabExcel() {
		return indexTabExcel;
	}

	public void setIndexTabExcel(Map<Integer, File> indexTabExcel) {
		this.indexTabExcel = indexTabExcel;
	}

	public Map<String, JButton> getOriginalNewSelected() {
		return originalNewSelected;
	}

	public void setOriginalNewSelected(Map<String, JButton> originalNewSelected) {
		this.originalNewSelected = originalNewSelected;
	}

	public LensMEnuButtons getLens() {
		return lens;
	}

	public void setLens(LensMEnuButtons lens) {
		this.lens = lens;
	}

	public ViewImagesBigger getViewImagen() {
		return viewImagen;
	}

	public void setViewImagen(ViewImagesBigger viewImagen) {
		this.viewImagen = viewImagen;
	}

	public RoiModifyView getRoiModifyTab() {
		return roiModifyTab;
	}

	public void setRoiModifyTab(RoiModifyView roiModifyTab) {
		this.roiModifyTab = roiModifyTab;
	}

	// METHODS
	/**
	 * Function use to paint the tabpanel when there is tiff file in the directory
	 * 
	 * @param directory the current directory
	 */
	public void alreadyImageTiffFolderTab(String directory) {
		// We search for the excel files
		List<String> result = new ArrayList<String>();
		File folder = new File(directory);

		excelModificationIndexTab = new HashMap<Integer, Long>();
		indexTabExcel = new HashMap<Integer, File>();
		this.dir = directory;

		Utils.search(".*\\.xls", folder, result, 0);
		Collections.sort(result);

		List<String> imageIconString = new ArrayList<String>();
		Utils.search(".*\\.tiff", folder, imageIconString, 2);
		List<ImageIcon> imageIcon = FileFuntions.transformListToImageicon(imageIconString);

		// create the jpanel, it content and we add the name to the tab

		// the images tab

		if (imageIcon.isEmpty()) {
			if (this.alreadyprocessed) {
				noFileText("Images", null);
				tabsActions(result);
				this.alreadyprocessed = false;
			} else {
				boolean proessImages = Utils.mainFrame.checkOriginalAndAskProcess(this.dir);
				if (proessImages) {

					selectAlgorithmImagesTab(directory);

				} else {
					noFileText("Images", null);
					tabsActions(result);
				}
			}
		} else {

			viewImagen = new ViewImagesBigger(imageIcon, this);
			tabsActions(result);
		}

	}

	/**
	 * Adds the actions to be performed in the tabs when they have the focus
	 * 
	 * @param result list with the path of the excels in the directory
	 */
	private void tabsActions(List<String> result) {

		File excel = null;

		// the excels tab
		if (result.size() == 0) {
			noFileText("Excel", null);
		} else {

			for (String path : result) {

				excel = new File(path);
				if (excel.exists()) {
					ExcelActions.addExcelPanel(excel, this, -1);

				} else {
					noFileText("Excel", null);
				}

			}

		}

		// We add the listeners to check if the different tabs has changed

		this.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				TabPanel tab = (TabPanel) e.getSource();

				if (tab.getTitleAt(tab.getSelectedIndex()).contains("Excel")) {
					ExcelActions.checkExcelTab(tab, dir, tab.getSelectedIndex());
				} else {
					if (tab.getSelectedIndex() == tab.indexOfTab("Images Scroll")
							|| tab.getSelectedIndex() == tab.indexOfTab("Images")
							|| tab.getTitleAt(tab.getSelectedIndex()).contains("ImageViewer ")) {
						Utils.mainFrame.requestFocusInWindow();
						FileFuntions.isDirectoryContentModify(dir, tab);

					}
				}

			}
		});

		// We save the last time the directory was changed
		FileFuntions.addModificationDirectory(dir);

		this.setSelectedIndex(0);

	}

	/**
	 * Creates, adds the scrollview to a tab and makes it the current tab selected
	 * and focused
	 * 
	 * @param imagesShow showImages view
	 */
	public void scrollView(ShowImages imagesShow) {
		if (imagesShow == null) {
			images = new ShowImages(this.dir, this);
		} else {
			images = imagesShow;
		}

		lens = new LensMEnuButtons();
		lens.setListImagesPrev(images.getListImagesPrev());

		JPanel splitPane = createJPanelToShowImages(images, lens);

		addTab("Images Scroll", splitPane);

		this.setSelectedIndex(this.indexOfComponent(splitPane));

	}

	/**
	 * Creates the JPanel that the tab is going to display
	 * 
	 * @param images image or images to be display on the view
	 * @param lens   LensMEnuButtons
	 * @return the JPanel creaated with the params
	 */
	public static JPanel createJPanelToShowImages(Component images, LensMEnuButtons lens) {
		JPanel splitPane = new JPanel(new GridBagLayout());

		JScrollPane s = new JScrollPane(images);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;

		splitPane.add(lens, constraints);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		splitPane.add(s, constraints);
		return splitPane;
	}

	/**
	 * Paint the tabpanel when detected new esferoides with all the algorithms
	 * 
	 * @param directory the current directory
	 */
	public void selectAlgorithmImagesTab(String directory) {

		this.dir = directory;
		List<String> result = new ArrayList<String>();
		List<String> listExtensions = FileFuntions.getExtensions();
		originalNewSelected = new HashMap<String, JButton>();
		int i = 0;

		// We look what type of format are the images of the current folder if
		// it has
		// files, for it we search the extension files we accept until the
		// result list
		// isn't empty or we haven't more extensions to check.

		while (result.isEmpty() && i < listExtensions.size()) {
			if (listExtensions.get(i).contentEquals("tiff")) {
				i++;
			}
			Utils.search(".*\\." + listExtensions.get(i), new File(directory), result, 1);
			i++;
		}

		// If the folder is "empty" (hasn't got files with the extensions we are
		// looking),
		// we show that the folder is empty and call the main function to select
		// another
		// folder and do actions with it
		if (result.isEmpty()) {
			alreadyImageTiffFolderTab(directory);

			// if there is images we proceed to detect the esferoid
		} else {

			List<String> folderList = new ArrayList<String>();
			Utils.searchFolders(new File(dir), folderList, 1);
			boolean compare = false;

			Utils.mainFrame.getPb().setTextMAxObject(result.size());
			List<String> predictionsFolderPAth = new ArrayList<String>();
			Utils.searchFoldersName(new File(this.dir), "predictions", predictionsFolderPAth, 1);
			if (folderList.isEmpty() || predictionsFolderPAth.isEmpty()) {
				compare = processImage(i, listExtensions, directory, result);

				predictionsFolderPAth.clear();
				Utils.searchFoldersName(new File(this.dir), "predictions", predictionsFolderPAth, 1);
				File predic = null;
				for (String path : predictionsFolderPAth) {
					predic = new File(path);
					if (predic.listFiles().length == 0) {
						predic.delete();
					}
				}
				predictionsFolderPAth.clear();
				Utils.searchFoldersName(new File(this.dir), "predictions", predictionsFolderPAth, 1);
				if (predictionsFolderPAth.isEmpty()) {
					this.alreadyprocessed = true;
				}
				showImages(directory, compare, result, false);
			} else {

				boolean auxb = false;
				for (String string : folderList) {
					result.clear();
					Utils.search(".*\\." + listExtensions.get(i - 1), new File(string), result, 0);
					auxb = processImage(i, listExtensions, string + File.separator, result);
					if (auxb && !compare) {
						compare = true;
					}
				}

				if (compare) {
					result.clear();
					Utils.search(".*\\." + listExtensions.get(i - 1), new File(directory), result, 1);
					showImages(directory, compare, result, true);
				}
			}

		}

	}

	/**
	 * Process the images and saves the images generates in a prediction folder if
	 * there were not previous images saved or in temporal if there were previous
	 * images saved
	 * 
	 * @param i              the position in the listExtension of the extension
	 *                       found
	 * @param listExtensions list of image extensions the program works with
	 * @param dire           path of the directory to work with
	 * @param result         list of images path to process
	 * @return true if there where already images processed in the directory an the
	 *         ones created now are temporal
	 */
	private boolean processImage(int i, List<String> listExtensions, String dire, List<String> result) {

		// check if in that folder there is more types of images and tell
		// the user that only one type of images is going to be detected
		if (i < listExtensions.size()) {
			boolean containsExt = false;
			List<String> otherExt = new ArrayList<String>();

			while (i < listExtensions.size()) {
				if (listExtensions.get(i).contentEquals("tiff")) {
					i++;
				}
				containsExt = Utils.containsExtension(".*\\." + listExtensions.get(i), new File(dire));
				if (containsExt) {
					otherExt.add(listExtensions.get(i));
				}
				i++;
			}

			if (otherExt.size() > 0) {
				String ex = "";
				for (String string : otherExt) {
					ex += " " + string;
				}

				JOptionPane.showMessageDialog(null,
						"There is also files with this extensions:" + ex + "\n" + "But only the "
								+ FileFuntions.extensionwithoutName(result.get(0))
								+ " images are going to be detected");
			}

		}
		File folder = new File(dire);
		String preDir = dire;
		boolean showtiff = false;

		if (preDir.endsWith(File.separator)) {
			preDir += "predictions";
		} else {
			preDir += File.separator + "predictions";
		}
		File predictionsDir = new File(preDir);

		FileFuntions.moveTifffromParentToPredictions(folder, new ArrayList<String>(), predictionsDir);

		if (predictionsDir.exists()) {
			showtiff = true;
		}

		new Methods(dire, result, showtiff);

		return showtiff;

	}

	/**
	 * Shows the images if compare the saved images and the new images created will
	 * be shown in a viewImageBigger comparer
	 * 
	 * @param dire               path of the directory to work with
	 * @param compare            true if images are been compared
	 * @param imagesOriginalPath path of the original images
	 * @param noChildFolder      true if only 0 level of deep, no subdirectories
	 */
	private void showImages(String dire, boolean compare, List<String> imagesOriginalPath, boolean noChildFolder) {
		String preDir = dire;

		if (compare) {
			List<String> predictionsFolderPAth = new ArrayList<String>();
			if (noChildFolder) {
				if (preDir.endsWith(File.separator)) {
					preDir += "predictions";
				} else {
					preDir += File.separator + "predictions";
				}
				predictionsFolderPAth.add(preDir);

			} else {
				Utils.searchFoldersName(new File(this.dir), "predictions", predictionsFolderPAth, 1);
			}
			Collections.sort(imagesOriginalPath);
			List<String> originalNames = new ArrayList<String>();
			for (String path : imagesOriginalPath) {
				originalNames.add(FileFuntions.namewithoutExtension(path));
			}
			saveImagesOneAlgo(predictionsFolderPAth, originalNames);

		} else {
			// run the methods to process the images
			alreadyImageTiffFolderTab(dire);

		}
	}

	/**
	 * Changes the selected images with the new selected one and shows the button
	 * background yellow to show that that is the selected image
	 * 
	 * @param newImage path of the old image selected
	 * @param oldImage path of the new image selected
	 */
	public void changeSelectedImage(String newImage, String oldImage) {

		String key = FileFuntions.getKeyFRomButtonDescription(originalNewSelected, oldImage);
		JButton oldButton = originalNewSelected.get(key);
		oldButton.setBackground(null);
		images = (ShowImages) oldButton.getParent();

		JButton newButton = images.getListImagesPrev().get(newImage);
		newButton.setBackground(Color.yellow);

		originalNewSelected.put(key, newButton);
		this.repaint();

	}

	/**
	 * Ask for the action to do with the new images
	 * 
	 * @return 0 Replace all, 1 choose images and null if X
	 */
	public int askReplaceOrchoose() {
		String message = "There is already images that detected the esferoides in this folder. Do you want to replace the all the current images with the new ones or do you prefere with ones you change?";
		Object[] opciones = { "Replace all", "Choose images" };

		int op = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, 2, null, opciones,
				"Replace all");
		return op;
	}

	/**
	 * Deletes the current saved images and excels and replace them with the
	 * temporal ones
	 * 
	 * @param pathFoldersPrediction list of paths of the prediction folders
	 */
	public void deletePredicctionsAndSetTemporalPredictions(List<String> pathFoldersPrediction) {

		String excelPath = getDir();
		if (!excelPath.endsWith(File.separator)) {
			excelPath += File.separator;
		}
		excelPath += "results.xls";
		File excel = new File(excelPath);
		String tempPath = getDir();
		if (!tempPath.endsWith(File.separator)) {
			tempPath += File.separator;
		}
		tempPath += "temp" + File.separator;
		File newTempPredic = new File(tempPath);
		newTempPredic.mkdir();
		excel.renameTo(new File(tempPath + excel.getName()));
		File folderDir = new File(pathFoldersPrediction.get(0));

		try {
			OurProgressBar pb = new OurProgressBar(Utils.mainFrame, false);
			Utils.mainFrame.setPb(pb);
			for (String path : pathFoldersPrediction) {

				folderDir = new File(path);
				if (folderDir.exists() && folderDir.listFiles().length != 0) {
					File tempoFolder = new File(folderDir.getAbsolutePath().replace("predictions", "temporal"));

					folderDir.renameTo(new File(tempPath + folderDir.getName()));

					t = new Thread() {
						public void run() {

							// merge all the excels in one and save the selected files
							// in the new location

							File fDir = new File(path);
							tempoFolder.renameTo(fDir);

							List<String> excelList = new ArrayList<String>();
							Utils.search(".*results.xls", fDir, excelList, 1);
							String nameNoExtension = "";
							for (String fileExcel : excelList) {
								File aux = new File(fileExcel);
								nameNoExtension = aux.getName().replace("_results.xls", "");
								// merge the excel
								ExcelActions.mergeExcels(aux, nameNoExtension,
										new File(aux.getAbsolutePath().replace(aux.getName(), "")));

							}
							File excel = new File(fDir + File.separator + "results.xls");
							excel.renameTo(
									new File(excel.getAbsolutePath().replace("predictions" + File.separator, "")));

							// We delete the temporal folder
							FileFuntions.deleteFolder(newTempPredic);

						}
					};

					t.start();
					t.join();
				}
			}
			if (pathFoldersPrediction.size() > 1) { // Merge all excels in the result one

				// merge the excel
				ExcelActions.mergeExcelsDirectoryAndSubdir(new File(this.dir), 2);

			}
			// change the tab to the already tiff view
			Utils.mainFrame.getImageTree().repaintTabPanel(false);

			pb.dispose();
		} catch (Exception e) {
			e.printStackTrace();

			if (pathFoldersPrediction.size() == 1) {
				JOptionPane.showMessageDialog(null, "Error moving the images to the final folder. Please try again");
				FileFuntions.deleteFolder(folderDir);
				folderDir.renameTo(new File(folderDir.getAbsolutePath().replace("temp" + File.separator, "")));
				excel.renameTo(new File(folderDir.getAbsolutePath().replace("temp" + File.separator, "")));
			} else {
				JOptionPane.showMessageDialog(null,
						"Error moving the images to the final folder. Some data may have been deleted");
			}
		}
	}

	/**
	 * Saves the images when only the saved algorithm for that image is been used
	 * 
	 * @param predictionFolders paths for the prediction folders
	 * @param originalNAmes     names of the original files
	 */
	public void saveImagesOneAlgo(List<String> predictionFolders, List<String> originalNAmes) {
		// if there is a predictions folder we ask to replace all files or
		// to choose the ones to replace

		List<String> resultPredictions = new ArrayList<String>();
		Utils.search("predictions", ".*\\.tiff", new File(this.dir), resultPredictions, 2);

		List<String> newresult = new ArrayList<String>();
		Utils.search("temporal", ".*\\.tiff", new File(this.dir), newresult, 2);

		Collections.sort(resultPredictions);
		Collections.sort(newresult);

		System.out.println("predictions folder " + resultPredictions.size());
		System.out.println("new " + newresult.size());

		int op = askReplaceOrchoose();
		if (op == 0) {// we delete the folder predictions and the excel
						// and move save the new ones

			if (resultPredictions.size() == newresult.size()) { // if there is the same number of images in prediction
																// and in temporal delete and replace
				deletePredicctionsAndSetTemporalPredictions(predictionFolders);
			} else {
				OurProgressBar pb = new OurProgressBar(Utils.mainFrame, false);
				Utils.mainFrame.setPb(pb);
				Thread t = new Thread() {
					public void run() {

						int i = 0;
						int j = 0;

						for (String ori : originalNAmes) {
							if (i < resultPredictions.size()) {
								if (j < newresult.size()) {
									if (resultPredictions.get(i).contains(ori) && newresult.get(j).contains(ori)) {
										File aux = new File(resultPredictions.get(i));

										FileFuntions.saveSelectedImageNoQuestion(new File(newresult.get(j)),
												aux.getAbsolutePath().replace(aux.getName(), ""));
										i++;
										j++;
									} else {
										if (!resultPredictions.get(i).contains(ori) && newresult.get(j).contains(ori)) {
											File f = new File(newresult.get(j));
											FileFuntions.saveImageNoBeforeProcess(f, dir, ori);
											j++;
										} else {
											if (resultPredictions.get(i).contains(ori)
													&& !newresult.get(j).contains(ori)) {

												i++;
											}
										}
									}
								} else {
									break;
								}

							} else {

								File f = new File(newresult.get(j));
								FileFuntions.saveImageNoBeforeProcess(f, dir, ori);
								j++;
								break;
							}

						}
						pb.dispose();
						// change the tab to the already tiff view
						Utils.mainFrame.getImageTree().repaintTabPanel(false);
					}
				};

				t.start();
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} else {
			openComparerNewVsOld(originalNAmes, resultPredictions, newresult);

		}

	}

	/**
	 * Opens a comparer between the new images and the saved ones
	 * 
	 * @param originalNAmes     original names of the images
	 * @param resultpredictions saved images
	 * @param newresult         new images
	 */
	public void openComparerNewVsOld(List<String> originalNAmes, List<String> resultpredictions,
			List<String> newresult) {
		// we open a comparer to

		Map<String, String> imageIndexPredictions = new HashMap<String, String>();
		Map<String, String> imageIndexTemporal = new HashMap<String, String>();
		int i = 0;
		int j = 0;

		for (String ori : originalNAmes) {
			if (i < resultpredictions.size()) {
				if (resultpredictions.get(i).contains(ori)) {
					imageIndexPredictions.put(ori, resultpredictions.get(i));
					i++;
				} else {
					imageIndexPredictions.put(ori, null);
				}
			}

			if (j < newresult.size()) {
				if (newresult.get(j).contains(ori)) {
					imageIndexTemporal.put(ori, newresult.get(j));
					j++;
				} else {
					imageIndexTemporal.put(ori, null);
				}
			}

		}

		ViewImagesBigger vi = new ViewImagesBigger(imageIndexPredictions, imageIndexTemporal, this);

		this.removeAll();
		this.add("Compare Images", vi.getJPComparer());
	}

	/**
	 * Saves the selected files (tiff file and roi) in the temporal folder to the
	 * predictions folder and merge the excels to have the results.xls excel with
	 * all the data
	 */
	public void moveFinalFilesToPredictions() {
		OurProgressBar pb = new OurProgressBar(Utils.mainFrame, false);
		Utils.mainFrame.setPb(pb);
		JOptionPane.showMessageDialog(Utils.mainFrame, "Saving the images in the predicction folder");
		String auxName = "";
		File auxFile = null;
		String nameNoExtension = "";
		String nameNoPAth = "";

		for (JButton nameFile : getOriginalNewSelected().values()) {

			auxName = nameFile.getName().replace("temporal", "predictions");
			auxFile = new File(nameFile.getName());
			nameNoExtension = FileFuntions.getKey(getOriginalNewSelected(), nameFile);
			nameNoPAth = FileFuntions.namewithoutExtension(nameFile.getName());

			// changing the name of the algorithm with the original one
			auxName = auxName.replace(nameNoPAth, nameNoExtension + "_pred");
			auxFile.renameTo(new File(auxName));

			auxFile = new File(nameFile.getName().replace("_pred.tiff", "_results.xls"));
			auxName = auxName.replace("_pred.tiff", "_results.xls");

			File fileExcel = new File(auxName.replace(File.separator + "predictions", ""));
			auxFile.renameTo(fileExcel);

			auxFile = new File(nameFile.getName().replace("_pred.tiff", ".zip"));
			auxName = auxName.replace("_results.xls", ".zip");
			auxFile.renameTo(new File(auxName));

			// merge the excel
			ExcelActions.mergeExcels(fileExcel, nameNoExtension,
					new File(fileExcel.getAbsolutePath().replace(fileExcel.getName(), "")));

		}
		JOptionPane.showMessageDialog(Utils.mainFrame, "Images save");
		pb.dispose();

	}

	/**
	 * If there is no tiff files in the current director or the predictions folder,
	 * we create a tab with an enable text to notify the user
	 * 
	 * The same if there isn't any result excel in the current directory
	 * 
	 * @param tabName The tabpanel to add the new tab
	 * @param jp      contains the JTablepanel
	 */
	public void noFileText(String tabName, JViewport jp) {

		JTextArea j = new JTextArea();
		j.setText("There is no such file in this folder");
		j.setEnabled(false);
		j.setName(tabName);

		JScrollPane s = new JScrollPane(j);
		if (jp == null) {
			if (tabName.equals("Excel")) {
				insertTab(tabName, null, s, null, 1);
				excelModificationIndexTab.put(this.indexOfTab(tabName), 0L);

			} else {
				addTab(tabName, s);
			}
		} else {
			jp.remove(jp.getComponent(0));
			jp.add(j);
			jp.repaint();
		}

	}

}
