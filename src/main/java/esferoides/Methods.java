package esferoides;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.FileFuntions;
import ij.IJ;
import loci.plugins.in.ImporterOptions;


public class Methods {

	private static String[] algorithms = { "suspension", "colageno", "Hector no fluo v1", "Hector no fluo v2",
			"Teodora v1", "Teodora Big" };
	private static File temporalFolder;
	private ArrayList<Integer> goodRows;
	private List<Thread> threads;

	/**
	 * Constructor. Creates the images with the methods given in the temporal
	 * folder if it's possible
	 * 
	 * @param directory
	 *            current directory
	 * @param result
	 *            List of the file paths of the current directory
	 */
	public Methods(String directory, List<String> result, boolean all) {
threads= new ArrayList<Thread>();
		temporalFolder = new File(directory + "temporal");
int i=0;
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
					createImagesMetods( result,directory,  type, all);
				}

			} else {
				if ((type.contains("Hector") && FileFuntions.isExtension(result, "tif"))
						|| (type.equals("Teodora v1") && FileFuntions.isExtension(result, "nd2"))
						|| (type.equals("Teodora Big") && FileFuntions.isExtension(result, "nd2"))) {
//					CreateImagesThread c = new CreateImagesThread(result, directory, type, all,i);
//					threads.add(c);
//					c.start();
//					
//					i++;
					
					createImagesMetods( result,directory,  type, all);
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
	private void createImagesMetods(List<String> result, String directory, String type,boolean all) {
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

			EsferoidProcessor esferoidProcessor = EsferoidProcessorFactory.createEsferoidProcessor(type,all);

			// OurProgressBar pb = new OurProgressBar(null);
			goodRows = new ArrayList<>();
			// For each file in the folder we detect the esferoid on it.
			for (String name : result) {
			 esferoidProcessor.getDetectEsferoid().apply(options, directory, name, goodRows, true);
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
			JOptionPane.showMessageDialog(null, "An error occurred while detecting the esferoid");
		}

	
	}
	/**
	 * Checks all the files in the directory has a fluo image
	 * 
	 * @param result
	 *            list path of the files
	 * @return true if all have fluo images false if none of then have a fluo
	 *         image or some of then haven't it
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

}
