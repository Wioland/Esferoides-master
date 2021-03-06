package esferoides;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.ExcelActions;
import funtions.FileFuntions;
import funtions.PropertiesFileFuntions;
import funtions.Utils;
import ij.IJ;
import ij.ImagePlus;
import loci.plugins.in.ImporterOptions;

/**
 * Methods to process the images
 * 
 * @author Yolanda
 *
 */
public class Methods {

	private static String[] algorithms = { "Fluorescence", "colageno", "Edges", "Hector no fluo v2",
			"Threshold + Edges", "Threshold & Edges", "Hector fluo stack", "Threshold", "HRNSeg" };
	private static File temporalFolder;
	private ArrayList<Integer> goodRows;
	private boolean setScale = false;

	/**
	 * Process the images with the algorithm save in the properties file.
	 * 
	 * @param directory current directory
	 * @param result    path of the images of the directory
	 * @param temp      true for save in temporary folder, false for predictions
	 *                  folder
	 */
	public Methods(String directory, List<String> result, boolean temp) {

		setEscale();

		URL urlUpdater = FileFuntions.getProgramProps();
		if (urlUpdater != null) {
			PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);
			String fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
			String tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
			String nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
			String jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");

			boolean changed = FileFuntions.checkSavedAlgoPropertiesFile(fluoSave, tifSave, nd2Save, jpgSave);
			if (changed) {
				propUpdater = new PropertiesFileFuntions(urlUpdater);
				fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
				tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
				nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
				jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");
			}

			if (FileFuntions.isExtension(result, "nd2")) {
				createImagesMetods(result, directory, nd2Save, temp);
			}

			if (FileFuntions.isExtension(result, "jpg") || FileFuntions.isExtension(result, "JPEG")) {
				createImagesMetods(result, directory, jpgSave, temp);
			}

			if (FileFuntions.isExtension(result, "tif")) {
				if (checkIfFluoImages(result)) {
					createImagesMetods(result, directory, fluoSave, temp);
				} else {
					createImagesMetods(result, directory, tifSave, temp);
				}
			}

		}
	}

	/**
	 * Process the images with the algorithms given in the temporary folder if it's
	 * possible
	 * 
	 * @param directory current directory
	 * @param result    path of the images of the directory
	 */
	public Methods(String directory, List<String> result) {
		setEscale();
		Utils.mainFrame.getPb().setTextMAxObject(algorithms.length);
		temporalFolder = new File(directory + "temporal");

		for (String type : algorithms) {
			if (type.equals(algorithms[5])) {
				if (FileFuntions.isExtension(result, "nd2")) {
					createImagesMetods(result, directory, type, true);
				}

			} else {
				createImagesMetods(result, directory, type, true);
			}

		}

	}

	// Getters and setters

	public static File getTemporalFolder() {
		return temporalFolder;
	}

	public void setTemporalFolder(File temporalFolde) {
		temporalFolder = temporalFolde;
	}

	public static String[] getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(String[] algorithm) {
		algorithms = algorithm;
	}

	// METHODS
	/**
	 * Method that creates the images in the temporal directory with the method
	 * given
	 * 
	 * @param result    paths of the files
	 * @param directory temporal directory to store the images
	 * @param type      the method used to create the images
	 */
	private void createImagesMetods(List<String> result, String directory, String type, boolean temp) {
		try {

			// In order to only take the tif images without the fluo ones
			if (type.contains("Hector")) {
				int i = 0;
				for (String name : result) {
					if (name.endsWith("fluo.tif")) {
						result.set(i, name.replace("fluo.tif", ".tif"));
						i++;
					}
				}
			}

			// initialize the ResultsTable

			ImporterOptions options = new ImporterOptions();

			// construct the EsferoidProcessorObject

			EsferoidProcessor esferoidProcessor = EsferoidProcessorFactory.createEsferoidProcessor(type);
			goodRows = new ArrayList<>();
			// For each file in the folder detect the spheroid on it.
			for (String name : result) {
				esferoidProcessor.getDetectEsferoid().apply(options, directory, name, goodRows, temp);
			}

			if (!temp) {
				ExcelActions.saveExcel(goodRows, new File(directory));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(Utils.mainFrame, "An error occurred while detecting the esferoid");
		}

	}

	/**
	 * Checks all the files in the directory has a fluo image
	 * 
	 * @param result list path of the files
	 * @return true if all have fluo images false if none of then have a fluo image
	 *         or some of then haven't it
	 */
	private boolean checkIfFluoImages(List<String> result) {
		boolean haveFluo = true;
		File faux;
		String nameNoextension;

		for (String name : result) {
			faux = new File(name);
			nameNoextension = FileFuntions.namewithoutExtension(name);
			if (!nameNoextension.endsWith("fluo")) {
				faux = new File(faux.getAbsolutePath().replace(nameNoextension, nameNoextension + "fluo"));
			}

			if (!faux.exists()) {
				haveFluo = false;
				break;
			}
		}

		return haveFluo;
	}

	/**
	 * Set the escale for the images that are going to be processed
	 */
	private void setEscale() {

		int op = JOptionPane.showConfirmDialog(Utils.mainFrame, "Do you want to set the scale?", "Set scale",
				JOptionPane.YES_NO_OPTION);
		if (op == 0) {
			setScale = true;
		}
		if (setScale) {

			ImagePlus imp = IJ.createImage("Untitled", "8-bit white", 1, 1, 1);
			IJ.run(imp, "Set Scale...", "");
			imp.close();
		}
	}
}
