package interfaces;

import java.awt.Color;
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
	private boolean originalIma;
	private Map<String, JButton> originalNewSelected;
	private int originalImagesNumber = 0;
	private ShowImages images;
	private LensMEnuButtons lens;
	private Thread t;

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

	public boolean isOriginalIma() {
		return originalIma;
	}

	public void setOriginalIma(boolean originalIma) {
		this.originalIma = originalIma;
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
		originalIma = false;

		Utils.searchDirectory(".*\\.xls", folder, result);
		Collections.sort(result);

		// create the jpanel, it content and we add the name to the tab

		// the images tab

		images = new ShowImages(directory, this);

		if (images.getComponents().length == 0) {
			noFileText("Images", null);
			originalIma = FileFuntions.isOriginalImage(folder);
		} else {
			lens = new LensMEnuButtons(images.getListImagesPrev());

			JPanel splitPane = createJPanelToShowImages(images, lens);

			addTab("Images", splitPane);

		}

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
					if (tab.getTitleAt(tab.getSelectedIndex()).contains("Images")) {
						FileFuntions.isDirectoryContentModify(dir + "predictions", tab);

					}
				}

			}
		});

		// We save the last time the directory was changed
		FileFuntions.addModificationDirectory(dir + "predictions");

	}

	public JPanel createJPanelToShowImages(ShowImages images, LensMEnuButtons lens) {
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
			Utils.searchDirectory(".*\\." + listExtensions.get(i), new File(directory), result);
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

			// check if in that folder there is more types of images and tell
			// the user that only one type of images is going to be detected
			if (i < listExtensions.size()) {
				boolean containsExt = false;
				List<String> otherExt = new ArrayList<String>();

				while (i < listExtensions.size()) {
					if (listExtensions.get(i).contentEquals("tiff")) {
						i++;
					}
					containsExt = Utils.containsExtension(".*\\." + listExtensions.get(i), new File(directory));
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
			File folder = new File(directory);
			String preDir = directory;
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

			if (showtiff) {
				// run the methods to process the images
				int op = JOptionPane.showOptionDialog(Utils.mainFrame,
						"Do you want to use all the algoritms or use the selected ones?", "Select algorithms to use",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new Object[] { "Selected Algorithms", "All Algorithms" }, "Selected Algorithms");

				if (op == 0) {
					new Methods(directory, result, true);
					
					saveImagesOneAlgo(predictionsDir);
					
				} else {
					new Methods(directory, result);
					createViewImagesAllAlgo(result);
				}

		
			} else {
				// run the methods to process the images
				new Methods(directory, result, false);
				alreadyImageTiffFolderTab(directory);

			}

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

	public void saveImagesOneAlgo(File folderDir) {
		// if there is a predictions folder we ask to replace all files or
		// to choose the
		// ones to replace
		if (folderDir.exists() && folderDir.listFiles().length != 0) {
			File tempoFolder = new File(folderDir.getAbsolutePath().replace("predictions", "temporal"));
			String message = "There is already images that detected the esferoides in this folder. Do you want to replace the all the current images with the new ones or do you prefere with ones you change?";
			Object[] opciones = { "Replace all", "Choose images" };

			int op = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, 2, null,
					opciones, "Replace all");

			if (op == 0) {// we delete the folder predictions and the excel
							// and move save the new ones

//							FileFuntions.deleteFolder(folderDir);
//
				String excelPath = getDir();
				if (!excelPath.endsWith(File.separator)) {
					excelPath += File.separator;
				}
				excelPath += "results.xls";
				File excel = new File(excelPath);
//							excel.delete();

				String tempPath = getDir();
				if (!tempPath.endsWith(File.separator)) {
					tempPath += File.separator;
				}
				tempPath += "temp" + File.separator;
				File newTempPredic = new File(tempPath);
				newTempPredic.mkdir();
				folderDir.renameTo(new File(tempPath + folderDir.getName()));
				excel.renameTo(new File(tempPath + excel.getName()));

				try {
					OurProgressBar pb = new OurProgressBar(Utils.mainFrame);
					t = new Thread() {
						public void run() {

							// merge all the excels in one and save the selected files
							// in the new location
//							folderDir.mkdir();
//							moveFinalFilesToPredictions();

							
							tempoFolder.renameTo(folderDir);
							
							List<String> excelList= new ArrayList<String>();
							Utils.searchDirectory(".*results.xls", folderDir, excelList);
							String nameNoExtension="";
							for (String fileExcel : excelList) {
								File aux= new File(fileExcel);
								nameNoExtension=aux.getName().replace("_results.xls", "");
								// merge the excel
								ExcelActions.mergeExcels(aux, nameNoExtension,
										new File(aux.getAbsolutePath().replace(aux.getName(), "")));
								
							}
							File excel = new File(folderDir+File.separator+"results.xls");
							excel.renameTo(new File(excel.getAbsolutePath().replace("predictions"+File.separator, "")));
							// We delete the temporal folder
							FileFuntions.deleteFolder(newTempPredic);
							// change the tab to the already tiff view

							((ImageTreePanel) getParent()).repaintTabPanel(false);

							pb.dispose();
							t.interrupt();

						}
					};

					t.start();

				} catch (Exception e) {
					e.printStackTrace();

					JOptionPane.showMessageDialog(null,
							"Error moving the images to the final folder. Please try again");
					FileFuntions.deleteFolder(folderDir);
					folderDir.renameTo(new File(folderDir.getAbsolutePath().replace("temp"+File.separator, "")));
					excel.renameTo(new File(folderDir.getAbsolutePath().replace("temp"+File.separator, "")));
				}

			} else {
				// we open a comparer to
				List<String> result = new ArrayList<String>();
				Utils.searchDirectory(".*\\.tiff", folderDir, result);
				
				List<String> newresult = new ArrayList<String>();
				Utils.searchDirectory(".*\\.tiff", tempoFolder, newresult);

				ViewImagesBigger vi = new ViewImagesBigger(result,newresult , this);

				this.removeAll();
				this.add("Compare Images", vi.getJPComparer());
			}

		}
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
					Utils.searchDirectory(".*\\.tiff", folder, result);

					ViewImagesBigger vi = new ViewImagesBigger(result, getOriginalNewSelected(), this);

					this.removeAll();
					this.add("Compare Images", vi.getJPComparer());
				}

			} else {

				try {
					OurProgressBar pb = new OurProgressBar(Utils.mainFrame);
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
							t.interrupt();

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
		OurProgressBar pb = new OurProgressBar(Utils.mainFrame);
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
