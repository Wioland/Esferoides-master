package interfaces;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
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

	public void alreadyImageTiffFolderTab(String directory) {
		// Buscamos los excels que haya en la carpeta o en sus hijos
		List<String> result = new ArrayList<String>();
		File folder = new File(directory);
		File excel = null;
		excelModificationIndexTab = new HashMap<Integer, Long>();
		indexTabExcel = new HashMap<Integer, File>();
		this.dir = directory;
		originalIma = false;

		Utils.searchDirectory(".*\\.xls", folder, result);
		Collections.sort(result);

		// Creamos los paneles, creamos los componentes dentro de estos y aniadimos el
		// nombre a la pestania

		// los de las imagenes

		images = new ShowImages(directory, this);

		if (images.getComponents().length == 0) {
			noFileText("Images", null);
			originalIma = FileFuntions.isOriginalImage(folder);
		} else {
			JPanel splitPane = new JPanel(new GridBagLayout());
			lens = new LensMEnuButtons(images.getListImagesPrev());
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

			addTab("Images", splitPane);

		}

		// los del excel

		if (result.size() == 0) {
			noFileText("Excel", null);
		} else {

			for (String path : result) {
				// System.out.println(path);

				excel = new File(path);
				if (excel.exists()) {
					ExcelActions.addExcelPanel(excel, this);

				} else {
					noFileText("Excel", null);
				}

			}

		}

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

		FileFuntions.addModificationDirectory(dir + "predictions");
		FileFuntions.imagescheckWithTime(this, 60);

		ExcelActions.excelcheckWithTime(this, dir, 60);

	}

	public void selectAlgorithmImagesTab(String directory) {

		this.dir = directory;
		List<String> result = new ArrayList<String>();
		List<String> listExtensions = JMenuPropertiesFile.getExtensions();
		originalNewSelected = new HashMap<String, JButton>();
		int i = 0;

		// Miramos que tipo de formato contiene la carpeta seleccionada para ello
		// buscamos los tipos de formato que admitimos dentro de esa carpeta hasta que
		// result no sea vacio o no queden tipos de formato con los que comparar

		while (result.isEmpty() && i < listExtensions.size()) {
			if (listExtensions.get(i).contentEquals("tiff")) {
				i++;
			}
			Utils.searchDirectory(".*\\." + listExtensions.get(i), new File(directory), result);
			i++;
		}

		if (result.isEmpty()) { // mostramos k en esa carpeta no hay imagenes requeridas y volverr a llamar al
								// mail para seleccionar otra carpeta y realizar las acciones que queramos con
								// ella

			alreadyImageTiffFolderTab(directory);

		} else { // si hay imagens procedemos a realizar la decteccion de los esferoides en ellas
			// Realizamos los metodos de procesado
			new Methods(directory, result);
			originalImagesNumber = result.size();
			// para cada imagen original creamos un showimages mirando cuales de las nuevas
			// imagenes
			// creadas contienen el nombre de la imagen original sin el sufijo

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
			// splitPane.add(s, constraints);

			if (result.size() == 1) {
				splitPane.add(imaCreated, constraints);

			} else {
				// splitPane.add(imagesPanel, constraints);
				splitPane.add(s, constraints);
			}

			addTab("Images", splitPane);
		}

	}

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

	private void addSelectedButtons(JPanel selectButtons) {

		JButton saveSelection = new JButton("Save selection");
		JButton cleanSelection = new JButton("Clean selection");

		saveSelection.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				saveImagesSelected();
			}
		});

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

	private void saveImagesSelected() {
		String dirPredictions = getDir();

		if (!dirPredictions.endsWith(File.separator)) {
			dirPredictions += File.separator;
		}
		dirPredictions += "predictions";

		File folder = new File(dirPredictions);
		File tempoFolder = new File(dirPredictions.replace("predictions", "temporal"));

		if (folder.exists()) {

			String message = "There is already images that detected the esferoides in this folder. Do you want to replace the all the current images with the new ones or do you prefere with ones you change?";
			Object[] opciones = { "Replace all", "Choose images" };

			int op = JOptionPane.showOptionDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, 2, null,
					opciones, "Replace all");

			if (op == 0) {

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

				List<String> result = new ArrayList<String>();
				Utils.searchDirectory(".*\\.tiff", folder, result);

				JPanel panelDad= new JPanel(new GridBagLayout());
				JPanel panelLabels = new JPanel(new GridLayout(0, 2));

				JLabel originaText = new JLabel("Original image", SwingConstants.CENTER);
				originaText.setFont(new Font("Arial", Font.BOLD, 12));

				JLabel newImageText = new JLabel("New detected esferoid image", SwingConstants.CENTER);
				newImageText.setFont(new Font("Arial", Font.BOLD, 12));

				panelLabels.add(newImageText);
				panelLabels.add(originaText);
			
				ViewImagesBigger vi = new ViewImagesBigger(result, getOriginalNewSelected(), this);
				GridBagConstraints constraints = new GridBagConstraints();
				
				
		

				constraints.fill = GridBagConstraints.BOTH;
				constraints.weightx = 0;
				constraints.weighty = 0;

				constraints.gridx = 0;
				constraints.gridy = 0;

				
				panelDad.add(panelLabels, constraints);

				constraints.fill = GridBagConstraints.BOTH;
				constraints.weightx = 1;
				constraints.weighty = 1;

				constraints.gridy = 1;
				constraints.gridx = 0;

				panelDad.add(vi, constraints);
				
				this.removeAll();
				this.add("Compare Images", panelDad);
			}

		} else {

			try {
				if (getOriginalNewSelected().values().isEmpty()
						|| getOriginalNewSelected().values().size() != originalImagesNumber) {
					JOptionPane.showMessageDialog(null, "Please select an image of each");
				} else {
					// juntamos todos los excels en uno
					folder.mkdir();
					moveFinalFilesToPredictions();

					// pasar a la vista de los tif
					FileFuntions.deleteFolder(tempoFolder);
					// pasomos a la vista de tab tiff

					((ImageTreePanel) this.getParent()).repaintTabPanel(false);
				}

			} catch (Exception e) {
				e.printStackTrace();

				JOptionPane.showMessageDialog(null, "Error moving the images to the final folder. Please try again");
				FileFuntions.changeToriginalNameAndFolder(dirPredictions, getOriginalNewSelected());
				// FileFuntions.removeAllToOriginalFolder(dirPredictions, tempoFolder);
				FileFuntions.deleteFolder(folder);
			}
		}
	}

	public void moveFinalFilesToPredictions() {
		OurProgressBar pb = new OurProgressBar(getJFrameGeneral());
		JOptionPane.showMessageDialog(getJFrameGeneral(), "Saving the images in the predicction folder");
		String auxName = "";
		File auxFile = null;
		String nameNoExtension = "";
		String nameNoPAth = "";

		for (JButton nameFile : getOriginalNewSelected().values()) {

			auxName = nameFile.getName().replace("temporal", "predictions");
			auxFile = new File(nameFile.getName());
			nameNoExtension = FileFuntions.getKey(getOriginalNewSelected(), nameFile);
			nameNoPAth = FileFuntions.namewithoutExtension(nameFile.getName());

			auxName = auxName.replace(nameNoPAth, nameNoExtension + "_pred"); // el nombre con todos los procesos por el
			// nombre original
			auxFile.renameTo(new File(auxName));

			auxFile = new File(nameFile.getName().replace("_pred.tiff", "_results.xls"));
			auxName = auxName.replace("_pred.tiff", "_results.xls");

			File fileExcel = new File(auxName.replace(File.separator + "predictions", ""));
			auxFile.renameTo(fileExcel);

			auxFile = new File(nameFile.getName().replace("_pred.tiff", ".zip"));
			auxName = auxName.replace("_results.xls", ".zip");
			auxFile.renameTo(new File(auxName));

			ExcelActions.mergeExcels(fileExcel, nameNoExtension,
					new File(fileExcel.getAbsolutePath().replace(fileExcel.getName(), "")));

		}
		JOptionPane.showMessageDialog(getJFrameGeneral(), "Images save");
		pb.dispose();

	}

	public JFrame getJFrameGeneral() {
		return (JFrame) ((ImageTreePanel) this.getParent()).getJFrameGeneral();
	}

	public void noFileText(String tabName, JViewport jp) {

		JTextArea j = new JTextArea();
		j.setText("There is no such file in this folder");
		j.setEnabled(false);
		// j.enable(false);
		j.setName(tabName);

		JScrollPane s = new JScrollPane(j);
		if (jp == null) {
			if (tabName.equals("Excel")) { // igual hay que cambiarlo por el nombre
				insertTab(tabName, null, s, null, 1);
				excelModificationIndexTab.put(this.indexOfTab(tabName), 0L);

				// int index= indexOfComponent(s);

				// ExcelActions.excelcheckWithTime(this, dir, index, 60);
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
