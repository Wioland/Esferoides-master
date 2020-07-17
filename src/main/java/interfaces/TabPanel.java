package interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class TabPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private Map<Integer, Long> excelModificationIndexTab;
	private Map<Integer, File> indexTabExcel;
	private String dir;
	private Map<String, JButton> originalNewSelected;
	private int originalImagesNumber = 0;
	private ShowImages images;
	private ViewImagesBigger viewImagen;
	private LensMEnuButtons lens;
	private Thread t;
	private RoiModifyView roiModifyTab = null;

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
		File excel = null;
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
			noFileText("Images", null);
			if (!Utils.mainFrame.isAskedCreateImages()) {
				boolean proessImages = Utils.mainFrame.checkOriginalAndAskProcess(this.dir);
				if (proessImages) {
					Utils.mainFrame.getImageTree().repaintTabPanel(proessImages);
				}
			}

		} else {

			viewImagen = new ViewImagesBigger(imageIcon, this);

		}
		Utils.mainFrame.setAskedCreateImages(false);
		// the excels tab

		if (result.size() == 0) {
			noFileText("Excel", null);
		} else {

			for (String path : result) {

				excel = new File(path);
				if (excel.exists()) {
					ExcelActions.addExcelPanel(excel, this);

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
					if (tab.getSelectedIndex()== tab.indexOfTab("Images Scroll") || 
							tab.getSelectedIndex()== tab.indexOfTab("Images") ||
									tab.getTitleAt(tab.getSelectedIndex()).contains("ImageViewer ")) {
						 Utils.mainFrame.requestFocusInWindow();
						FileFuntions.isDirectoryContentModify(dir, tab);

					}
				}

			}
		});

		// We save the last time the directory was changed
		FileFuntions.addModificationDirectory(dir);

	}

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
	 * Function to use when you wants to paint the tabpanel when you have just
	 * detected new esferoides with all the algorithms
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

			// run the methods to process the images
//			int op = JOptionPane.showOptionDialog(Utils.mainFrame,
//					"Do you want to use all the algoritms or use the selected ones?", "Select algorithms to use",
//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
//					new Object[] { "Selected Algorithms", "All Algorithms" }, "Selected Algorithms");
//
//			if (op == 0) {

//			saveImagesOneAlgo(predictionsDir);

//			} else {
//				new Methods(directory, result);
//				createViewImagesAllAlgo(result);
//			}

		} else {
			// run the methods to process the images
			alreadyImageTiffFolderTab(dire);

		}
	}

	public void createViewImagesAllAlgo(List<String> result) {
		originalImagesNumber = result.size();

		// For each image we create a showimage
		ShowAllAlgorithmImages imaCreated = null;
		JPanel imagesPanel = new JPanel(new GridLayout(0, 2));

		for (String pathOriginalImage : result) {
			imaCreated = new ShowAllAlgorithmImages(pathOriginalImage, this);
			imagesPanel.add(imaCreated);
		}

		JPanel splitPane = new JPanel(new GridBagLayout());
		JPanel selectButtons = new JPanel();
		JScrollPane s = new JScrollPane(imagesPanel);

		addSelectedButtons(selectButtons);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;

		splitPane.add(selectButtons, constraints);

		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = 1;

		if (result.size() == 1) {
			splitPane.add(imaCreated, constraints);

		} else {
			splitPane.add(s, constraints);
		}

		addTab("Images", splitPane);
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
	 * Add to the given jpanel the jButtons save selection y clean selection
	 * 
	 * @param selectButtons jpanel to add the button
	 */
	private void addSelectedButtons(JPanel selectButtons) {

		JButton saveSelection = new JButton("Save selection");
		JButton cleanSelection = new JButton("Clean selection");

		// Adds the action to save the selected files
		saveSelection.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				saveImagesSelected();
			}
		});

		// puts the background of all the selected buttons to the original color
		// and
		// cleans the map
		// that contains witch button was selected
		cleanSelection.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				for (JButton b : originalNewSelected.values()) {
					b.setBackground(null);
				}
				originalNewSelected.clear();
			}
		});

		selectButtons.add(cleanSelection);
		selectButtons.add(saveSelection);

	}

	public int askReplaceOrchoose() {
		String message = "There is already images that detected the esferoides in this folder. Do you want to replace the all the current images with the new ones or do you prefere with ones you change?";
		Object[] opciones = { "Replace all", "Choose images" };

		int op = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, 2, null, opciones,
				"Replace all");
		return op;
	}

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

//							excel.delete();

					folderDir.renameTo(new File(tempPath + folderDir.getName()));

					t = new Thread() {
						public void run() {

							// merge all the excels in one and save the selected files
							// in the new location
//						folderDir.mkdir();
//						moveFinalFilesToPredictions();
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

//							t.interrupt();

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

	public void saveImagesOneAlgo(List<String> predictionFolders, List<String> originalNAmes) {
		// if there is a predictions folder we ask to replace all files or
		// to choose the
		// ones to replace

		List<String> resultPredictions = new ArrayList<String>();
		Utils.search("predictions", ".*\\.tiff", new File(this.dir), resultPredictions, 2);
//		Utils.search(".*\\.tiff", folderDir, result, 2);		

		List<String> newresult = new ArrayList<String>();
		Utils.search("temporal", ".*\\.tiff", new File(this.dir), newresult, 2);
//		Utils.search(".*\\.tiff", temporalFolder, newresult, 2);

		Collections.sort(resultPredictions);
		Collections.sort(newresult);

		System.out.println("predictions folder " + resultPredictions.size());
		System.out.println("new " + newresult.size());

		int op = askReplaceOrchoose();
		if (op == 0) {// we delete the folder predictions and the excel
						// and move save the new ones

//							FileFuntions.deleteFolder(folderDir);

//			comprobar para cadacarpeta si el numero de archivos es el mismo, si es menor en el 
//			que esta se deja el metodo de ahora, sino hay que dejar la imagen en la carpeta actual que no esta en la nueva generada
//			

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
	 * Saves the files selected from a group of buttons If there is already a
	 * predictions folder and it isn't empty we ask for deleting the folder or to
	 * choose the files to exchange
	 */
	private void saveImagesSelected() {
		String dirPredictions = getDir();

		if (!dirPredictions.endsWith(File.separator)) {
			dirPredictions += File.separator;
		}
		dirPredictions += "predictions";

		File folder = new File(dirPredictions);
		File tempoFolder = new File(dirPredictions.replace("predictions", "temporal"));

		// if there isn't a button selected in each category we ask to select
		// all
		if (getOriginalNewSelected().values().isEmpty()
				|| getOriginalNewSelected().values().size() != originalImagesNumber) {
			JOptionPane.showMessageDialog(null, "Please select an image of each");
		} else {
			// if there is a predictions folder we ask to replace all files or
			// to choose the
			// ones to replace
			if (folder.exists() && folder.listFiles().length != 0) {

				String message = "There is already images that detected the esferoides in this folder. Do you want to replace the all the current images with the new ones or do you prefere with ones you change?";
				Object[] opciones = { "Replace all", "Choose images" };

				int op = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, 2, null,
						opciones, "Replace all");

				if (op == 0) {// we delete the folder predictions and the excel
								// and move save the new ones

					FileFuntions.deleteFolder(folder);

					String excelPath = getDir();
					if (!excelPath.endsWith(File.separator)) {
						excelPath += File.separator;
					}
					excelPath += "results.xls";
					File excel = new File(excelPath);
					excel.delete();

					saveImagesSelected();
				} else {
					// we open a comparer to
					List<String> result = new ArrayList<String>();
					Utils.search(".*\\.tiff", folder, result, 2);

					ViewImagesBigger vi = new ViewImagesBigger(result, getOriginalNewSelected(), this);

					this.removeAll();
					this.add("Compare Images", vi.getJPComparer());
				}

			} else {

				try {
					OurProgressBar pb = new OurProgressBar(Utils.mainFrame, false);
					Utils.mainFrame.setPb(pb);
					t = new Thread() {
						public void run() {

							// merge all the excels in one and save the selected files
							// in the new location
							folder.mkdir();
							moveFinalFilesToPredictions();

							// We delete the temporal folder
							FileFuntions.deleteFolder(tempoFolder);
							// change the tab to the already tiff view

							((ImageTreePanel) getParent()).repaintTabPanel(false);

							pb.dispose();
//							t.interrupt();

						}
					};

					t.start();

				} catch (Exception e) {
					e.printStackTrace();

					JOptionPane.showMessageDialog(null,
							"Error moving the images to the final folder. Please try again");
					FileFuntions.changeToriginalNameAndFolder(dirPredictions, getOriginalNewSelected());
					FileFuntions.deleteFolder(folder);
				}
			}
		}

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
