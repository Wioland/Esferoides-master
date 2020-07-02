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
import interfaces.OurProgressBar;
import loci.plugins.in.ImporterOptions;

public class Methods {

	private static String[] algorithms = { "suspension", "colageno", "Hector no fluo v1", "Hector no fluo v2",
			"Teodora v1", "Teodora Big", "Hector fluo stack", "Teniposide" };
	private static File temporalFolder;
	private ArrayList<Integer> goodRows;
	private boolean setScale = false;
//	private List<Thread> threads;

	public Methods(String directory, List<String> result, boolean temp) {

//		setEscale();

		URL urlUpdater = FileFuntions.getProgramProps();
		if (urlUpdater != null) {
			PropertiesFileFuntions propUpdater = new PropertiesFileFuntions(urlUpdater);
			String fluoSave = propUpdater.getProp().getProperty("SelectFluoAlgo");
			String tifSave = propUpdater.getProp().getProperty("SelectTifAlgo");
			String nd2Save = propUpdater.getProp().getProperty("SelectNd2Algo");
			String jpgSave = propUpdater.getProp().getProperty("SelectJpgAlgo");

			if (fluoSave == null || tifSave == null || nd2Save == null || jpgSave == null) {
				String hv2 = "Hector no fluo v2";
				String tbg = "Teodora Big";
				String tp = "Teniposide";

				FileFuntions.saveAlgorithmConfi(hv2, hv2, tbg, tp);
			}

			if (FileFuntions.isExtension(result, "nd2")) {
				createImagesMetods(result, directory, nd2Save, false, temp);
			}

			if (FileFuntions.isExtension(result, "jpg") || FileFuntions.isExtension(result, "JPEG")) {
				createImagesMetods(result, directory, jpgSave, false, temp);
			}

			if (FileFuntions.isExtension(result, "tif")) {
				if (checkIfFluoImages(result)) {
					createImagesMetods(result, directory, fluoSave, false, temp);
				} else {
					createImagesMetods(result, directory, tifSave, false, temp);
				}
			}

		}
	}

	/**
	 * Constructor. Creates the images with the methods given in the temporal folder
	 * if it's possible
	 * 
	 * @param directory current directory
	 * @param result    List of the file paths of the current directory
	 */
	public Methods(String directory, List<String> result) {
//		setEscale();
//		threads = new ArrayList<Thread>();
		temporalFolder = new File(directory + "temporal");
//		int i = 0;
		for (String type : algorithms) {
			if (type.equals("suspension") || type.equals("colageno")) {
				if (checkIfFluoImages(result)) {
//					CreateImagesThread c = new CreateImagesThread(result, directory, type, all,i);
//					threads.add(c);
//					
//					c.start();
//					
//
//					i++;
//					
					createImagesMetods(result, directory, type, true, true);
				}

			} else {
				if ((type.contains("Hector") && FileFuntions.isExtension(result, "tif"))
						|| (type.equals("Teodora v1") && FileFuntions.isExtension(result, "nd2"))
						|| (type.equals("Teodora Big") && FileFuntions.isExtension(result, "nd2"))
						|| (type.equals("Teniposide") && (FileFuntions.isExtension(result, "jpg")
								|| FileFuntions.isExtension(result, "JPEG")))) {
//					CreateImagesThread c = new CreateImagesThread(result, directory, type, all,i);
//					threads.add(c);
//					c.start();
//					
//					i++;

					createImagesMetods(result, directory, type, true, true);
				}

			}

		}

//		for (Thread thread : threads) {
//			try {
//				thread.join();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}	

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
	private void createImagesMetods(List<String> result, String directory, String type, boolean all, boolean temp) {
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

			
			
			
			// We initialize the ResultsTable
			// ResultsTable rt = new ResultsTable();
			ImporterOptions options = new ImporterOptions();

			// We construct the EsferoidProcessorObject

			EsferoidProcessor esferoidProcessor = EsferoidProcessorFactory.createEsferoidProcessor(type, all);

			// OurProgressBar pb = new OurProgressBar(null);
			goodRows = new ArrayList<>();
			// For each file in the folder we detect the esferoid on it.
			for (String name : result) {
				esferoidProcessor.getDetectEsferoid().apply(options, directory, name, goodRows, temp);
			}

			if (!all && !temp) {
				ExcelActions.saveExcel(goodRows, new File(directory));
			}

//			Thread t;
//			for (String name : result) {
//				t= new Thread() {
//				    public void run() {
//					    esferoidProcessor.getDetectEsferoid().apply(options, directory, name, goodRows, true);
//					
//					    }  
//					};
//				threads.add(t);
//				t.start();
//			}
//			for (Thread thread : threads) {
//				try {
//					thread.join();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//System.out.println("fin for.........................");
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
				// JOptionPane.showMessageDialog(null,
				// "One or more images doesn´t have their fluo image or it isn´t
				// in the same folder. This method needs a fluo image. Try it
				// later when a fluo image is in the folder",
				// "No fluo image. Can´t do the method",
				// JOptionPane.WARNING_MESSAGE);
				haveFluo = false;
				break;
			}
		}

		return haveFluo;
	}

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
