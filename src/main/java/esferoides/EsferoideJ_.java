package esferoides;

import ij.IJ;

import ij.ImagePlus;
import ij.Prefs;
import ij.gui.Roi;
import ij.io.DirectoryChooser;
import ij.io.FileInfo;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.plugin.Thresholder;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import org.scijava.command.Command;
import org.scijava.command.Previewable;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import funtions.ExcelActions;
import funtions.Utils;

//@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Esferoids>EsferoideJ")
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>EsferoideJ")
public class EsferoideJ_ extends EsferoideDad implements Command  {

//	@Parameter
//	private ImagePlus imp;

//	@Parameter
//	private static boolean twox = true;

	

	// Method to count the number of pixels whose value is below a threshold.
	private int countBelowThreshold(ImagePlus imp1, int threshold) {

		ImageProcessor ip = imp1.getProcessor();
		int[] histogram = ip.getHistogram();

		int countpixels = 0;
		for (int i = 0; i < threshold; i++) {
			countpixels = countpixels + histogram[i];
		}

		return countpixels;

	}

	private boolean countBetweenThresholdOver(ImagePlus imp1, int threshold1, int threshold2, int num) {

		ImageProcessor ip = imp1.getProcessor();
		int[] histogram = ip.getHistogram(256);
		ImageStatistics is = ip.getStatistics();
		double min = is.min;
//		System.out.println(min);
		double max = is.max;
//		System.out.println(max);
		double range = (max - min) / 256;

		int i = 0;
		double pos = min;
		while (pos < threshold1) {
			pos = pos + range;
			i++;

		}

		while (pos < threshold2) {
			if (histogram[i] < num) {
				return true;
			}
			i++;
			pos = pos + range;
			System.out.println(pos);
		}

		return false;

	}


	


	private void processBlackHoles(ImagePlus imp2, boolean dilate) {
		IJ.setThreshold(imp2, 0, 2300);
		IJ.run(imp2, "Convert to Mask", "");
		if (dilate) {
			IJ.run(imp2, "Fill Holes", "");
			IJ.run(imp2, "Dilate", "");
		}
		IJ.run(imp2, "Watershed", "");
//		IJ.run(imp2, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private void processEsferoidesGeneralCase(ImagePlus imp2) {
		IJ.run(imp2, "Convolve...",
				"text1=[-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 50 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n] normalize");
//		IJ.run(imp2, "Convolve...",
//				"text1=[-1 -1 -1 -1 -1\n-1 -1 -1 -1 -1\n-1 -1 24 -1 -1\n-1 -1 -1 -1 -1\n-1 -1 -1 -1 -1\n] normalize");

		IJ.run(imp2, "Maximum...", "radius=2");
		Prefs.blackBackground = false;
		IJ.run(imp2, "Convert to Mask", "");

		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
//		IJ.run(imp2, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private void processEsferoidUsingThreshold(ImagePlus imp2) {
		IJ.setAutoThreshold(imp2, "Otsu");
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
//		IJ.run(imp2, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private ImagePlus processEsferoidUsingThresholdCombination(ImagePlus imp2) {

		ImagePlus imp1 = imp2.duplicate();
//		IJ.run(imp1, "Convolve...",
//				"text1=[-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 50 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n] normalize");
//		IJ.run(imp1, "Maximum...", "radius=2");
//		Prefs.blackBackground = false;
//		IJ.run(imp1, "Convert to Mask", "");
//		IJ.run(imp1, "Dilate", "");
//		IJ.run(imp1, "Dilate", "");
//		IJ.run(imp1, "Dilate", "");
//		IJ.run(imp1, "Fill Holes", "");
		IJ.run(imp1, "Find Edges", "");
		IJ.run(imp1, "Convert to Mask", "");
		IJ.run(imp1, "Morphological Filters", "operation=[Black Top Hat] element=Square radius=5");
		imp1.changes = false;
		imp1.close();
		ImagePlus imp4 = IJ.getImage();
//		imp3.close();
//		imp3= IJ.getImage();
		IJ.run(imp4, "Dilate", "");
		IJ.run(imp4, "Dilate", "");
		IJ.run(imp4, "Fill Holes", "");
		IJ.run(imp4, "Erode", "");
		IJ.run(imp4, "Erode", "");
		IJ.setAutoThreshold(imp2, "Otsu");
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
		ImageCalculator ic = new ImageCalculator();
		ImagePlus imp3 = ic.run("OR create", imp4, imp2);
//		IJ.run(imp3, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

		imp4.changes = false;
		imp4.close();
		imp2.close();
		return imp3;

	}

	private void processEsferoidUsingThresholdWithWatershed(ImagePlus imp2) {
		IJ.setAutoThreshold(imp2, "Otsu");
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Watershed", "");
//		IJ.run(imp2, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private void processEsferoidUsingFindEdges(ImagePlus imp2) {
		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Morphological Filters", "operation=[Black Top Hat] element=Square radius=5");
		imp2.close();
		ImagePlus imp3 = IJ.getImage();
//		imp3.close();
//		imp3= IJ.getImage();
		IJ.run(imp3, "Dilate", "");
		IJ.run(imp3, "Dilate", "");
		IJ.run(imp3, "Fill Holes", "");
		IJ.run(imp3, "Erode", "");
		IJ.run(imp3, "Erode", "");
//		IJ.run(imp3, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private void processEsferoidVariance(ImagePlus imp2) {
		IJ.run(imp2, "Variance...", "radius=1");
		IJ.setAutoThreshold(imp2, "Default"); // dark
		IJ.run(imp2, "Convert to Mask", "");
//		IJ.run(imp2, "Fill Holes", "");

//		IJ.run(imp3, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private void processEsferoidVariance2(ImagePlus imp2) {
		IJ.run(imp2, "Variance...", "radius=1");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Fill Holes", "");

//		IJ.run(imp3, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	private void processEsferoidEdges(ImagePlus imp2, int iters) {

		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "Convert to Mask", "");
		for (int i = 0; i < iters; i++) {
			IJ.run(imp2, "Find Edges", "");
		}

		IJ.run(imp2, "Fill Holes", "");
		for (int i = 0; i < iters; i++) {
			IJ.run(imp2, "Erode", "");
		}

	}

	private RoiManager analyzeParticles(ImagePlus imp2, boolean blackHole) {
		if (blackHole) {
			IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity circularity=0.5-1.00 show=Outlines exclude add");
		} else {
			IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity circularity=0.25-1.00 show=Outlines exclude add");
		}

		ImagePlus imp3 = IJ.getImage();
		imp2.close();
		imp3.close();

		RoiManager rm = RoiManager.getInstance();
		if (rm != null) {
			rm.setVisible(false);
//			Roi[] rois = rm.getRoisAsArray();
//			rm.runCommand("Select All");
//			rm.runCommand("Delete");
//			ImageStatistics stats ;
//			for(int i =0;i<rois.length;i++) {
//				stats = rois[i].getStatistics();
//				double round = 4*stats.area / (3.14 * stats.major * stats.major);
//				if(round>0.75) {
//					rm.addRoi(rois[i]);
//				}
//				
//			}

		}
		return rm;
	}

	private int analyzeSmallParticles(ImagePlus imp2) {
		IJ.run(imp2, "Analyze Particles...", "size=10-300 circularity=0-1.00 show=Outlines exclude add");
		ImagePlus imp3 = IJ.getImage();
		imp2.close();
		imp3.close();

		RoiManager rm = RoiManager.getInstance();
		if (rm != null) {
			rm.setVisible(false);
		}
		return rm.getRoisAsArray().length;
	}

	// Method to detect esferoides.
	private void detectEsferoide(ImporterOptions options, String dir, String name) throws FormatException, IOException {
		options.setId(name);

		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus imp = imps[0];
		ImagePlus imp2 = imp.duplicate();

		/// We consider two cases, when there is a "black hole" in the image (the first
		/// case), there is a lot of pixels below a given threshold, and those pixels
		/// belong to the Esferoide. In addition to be a black hole, there must be a
		/// difference between that region and the rest of the image.
//		int count = countBelowThreshold(imp2, 1100);
//		boolean realBlackHole = countBetweenThresholdOver(imp2, 1100, 2000, 1500);
//		System.out.println(realBlackHole);
		RoiManager rm;

		processEsferoidEdges(imp2, 0);
		rm = analyzeParticles(imp2, false);

		int iters = 1;
		while (rm == null || rm.getRoisAsArray().length == 0) {
			processEsferoidEdges(imp2, iters);
			rm = analyzeParticles(imp2, false);
			iters++;
		}

		/*
		 * Old version. Several options
		 * 
		 * if (count > 100 && realBlackHole) { if (count > 10000) {
		 * processBlackHoles(imp2, false); } else { processBlackHoles(imp2, true); }
		 * 
		 * rm = analyzeParticles(imp2, true);
		 * 
		 * } else { processEsferoidesGeneralCase(imp2);
		 * 
		 * rm = analyzeParticles(imp2, false);
		 * 
		 * if (rm != null) {
		 * 
		 * Roi[] r = rm.getRoisAsArray(); rm.runCommand("Select All");
		 * rm.runCommand("Delete");
		 * 
		 * int smallParticles = analyzeSmallParticles(imp2); if (smallParticles > 20) {
		 * rm.runCommand("Select All"); rm.runCommand("Delete"); imp2 = imp.duplicate();
		 * processEsferoidUsingFindEdges(imp2); imp2 = IJ.getImage(); imp2.changes =
		 * false;
		 * 
		 * rm = analyzeParticles(imp2, false);
		 * 
		 * } else { for (int i = 0; i < r.length; i++) { rm.addRoi(r[i]); } }
		 * 
		 * }
		 * 
		 * // We have to check whether the program has detected something (that is,
		 * whether // the RoiManager is not null). If the ROIManager is empty, we try a
		 * different // approach using a threshold. if (rm == null ||
		 * rm.getRoisAsArray().length == 0) {
		 * 
		 * // We try to find the esferoide using a threshold directly. imp2 =
		 * imp.duplicate(); imp2 = processEsferoidUsingThresholdCombination(imp2); rm =
		 * analyzeParticles(imp2, false); }
		 * 
		 * // We have to check whether the program has detected something (that is,
		 * whether // the RoiManager is not null). If the ROIManager is empty, we try a
		 * different // approach using a threshold combined with watershed. if (rm ==
		 * null || rm.getRoisAsArray().length == 0) {
		 * 
		 * // We try to find the esferoide using a threshold directly. imp2 =
		 * imp.duplicate(); processEsferoidUsingThresholdWithWatershed(imp2); rm =
		 * analyzeParticles(imp2, false);
		 * 
		 * }
		 * 
		 * if (rm == null || rm.getRoisAsArray().length == 0) { imp2 = imp.duplicate();
		 * processEsferoidUsingFindEdges(imp2); imp2 = IJ.getImage(); imp2.changes =
		 * false; rm = analyzeParticles(imp2, false);
		 * 
		 * }
		 * 
		 * // Idea: Probar varias alternativas y ver cuál es la que produce mejor //
		 * resultado. // ¿Cómo se define mejor resultado? } }
		 */
		showResultsAndSave(dir, imp, rm, this.getClass().getName());
		System.out.println("El nombre de la clase es "+this.getClass().getName());
		imp.close();

	}

//	
//	private static int getOtsuThreshold(ImagePlus imp1) {
//		ImagePlus imp = imp1.duplicate();
//		IJ.setAutoThreshold(imp, "Otsu");
//		ImageProcessor ip = imp.getProcessor();
//		int thresh = (int) ip.getMaxThreshold();
//		imp.close();
//		return thresh;
//				
//	}
//	
//	private static double getPercentageUnderOtsu(ImagePlus imp,int thresh) {
//		ImageProcessor ip = imp.getProcessor();
//		int[] histogram = ip.getHistogram();
//		int countpixelsbelow = 0;
//		for (int i = 0; i < thresh; i++) {
//			countpixelsbelow = countpixelsbelow + histogram[i];
//		}
//
//		int countpixelsover = 0;
//		for (int i = thresh; i < histogram.length; i++) {
//			countpixelsover = countpixelsover + histogram[i];
//		}
//
//		if(countpixelsbelow>countpixelsover) {
//			return countpixelsover * 1.0 / (countpixelsbelow+countpixelsover);
//		}else {
//			return countpixelsbelow * 1.0 / (countpixelsbelow+countpixelsover);
//		}
//	}
//	
//	
//	
//
//	
//	
//	private void newDetectEsferoide(ImporterOptions options, String dir, String name) throws FormatException, IOException {
//		options.setId(name);
//
//		ImagePlus[] imps = BF.openImagePlus(options);
//		ImagePlus imp = imps[0];
//		ImagePlus imp2 = imp.duplicate();
//
//		/// We consider two cases, when there is a "black hole" in the image (the first
//		/// case), there is a lot of pixels below a given threshold, and those pixels
//		/// belong to the Esferoide.
//		RoiManager rm;
//		int count = countBelowThreshold(imp2, 1800);
//		if (count > 10000) {
//			processBlackHoles(imp2);
//			rm  = analyzeParticles(imp2);
//		}else {
//			int thresh = getOtsuThreshold(imp2);
//			double perc = getPercentageUnderOtsu(imp2, thresh);
//			
//			// If the threshold is not over 7000 and the division using Otsu produces 
//			// two good regions, then the threshold method is employed. Otherwise, we 
//			// apply the filter
//			
//			if(thresh<7000 && perc>=0.1 && perc <=0.4) {
//				processEsferoidUsingThreshold(imp2);
//				rm = analyzeParticles(imp2);
//				if(rm==null) {
//					imp2 = imp.duplicate();
//					processEsferoidUsingThresholdWithWatershed(imp2);
//					rm = analyzeParticles(imp2);					
//				}
//			}else {
//				processEsferoidesGeneralCase(imp2);
//				rm = analyzeParticles(imp2);
//				if(rm==null) {
//					imp2 = imp.duplicate();
//					processEsferoidUsingFindEdges(imp2);
//					rm = analyzeParticles(imp2);					
//				}
//			}
//		}
//
//		showResultsAndSave(dir, imp, rm);
//		imp.close();
//
//	}

	@Override
	public void run() {
		IJ.setForegroundColor(255, 0, 0);
		goodRows = new ArrayList<>();
		try {

			// Since we are working with nd2 images that are imported with the Bio-formats
			// plugins, we must set to true the option windowless to avoid that the program
			// shows us a confirmation dialog every time.
			ImporterOptions options = new ImporterOptions();
			options.setWindowless(true);

			// We ask the user for a directory with nd2 images.
			DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");
			String dir = dc.getDirectory();

			JFrame frame = new JFrame("Work in progress");
			JProgressBar progressBar = new JProgressBar();
			progressBar.setValue(0);
			progressBar.setString("");
			progressBar.setStringPainted(true);
			progressBar.setIndeterminate(true);
			Border border = BorderFactory.createTitledBorder("Processing...");
			progressBar.setBorder(border);
			Container content = frame.getContentPane();
			content.add(progressBar, BorderLayout.NORTH);
			frame.setSize(300, 100);
			frame.setVisible(true);

			// We store the list of nd2 files in the result list.
			File folder = new File(dir);
			List<String> result = new ArrayList<String>();

			Utils.search(".*\\.nd2", folder, result);
			Collections.sort(result);
			// We initialize the ResultsTable
			ResultsTable rt = new ResultsTable();
//			rt.show("Results");

			// For each nd2 file, we detect the esferoide. Currently, this means that it
			// creates
			// a new image with the detected region marked in red.
			for (String name : result) {
				detectEsferoide(options, dir, name);
			}
			rt = ResultsTable.getResultsTable();
			/// Remove empty rows
			int rows = rt.getCounter();
			for (int i = rows; i > 0; i--) {
				if (!(goodRows.contains(i - 1))) {
					rt.deleteRow(i - 1);
				}
			}
			/// Remove unnecessary columns
			rt.deleteColumn("Mean");
			rt.deleteColumn("Min");
			rt.deleteColumn("Max");
			rt.deleteColumn("Circ.");
			rt.deleteColumn("Median");
			rt.deleteColumn("Skew");
			rt.deleteColumn("Kurt");
//			rt.deleteColumn("AR");
//			rt.deleteColumn("Round");
//			rt.deleteColumn("Solidity");

//			rt.saveAs(dir + "results.csv");
			// When the process is finished, we show a message to inform the user.

			ExcelActions ete = new ExcelActions(rt, dir);
			ete.convertToExcel();

			rt.reset();

			frame.setVisible(false);
			frame.dispose();
			IJ.showMessage("Process finished");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	public static void main(final String... args) throws Exception {
//		// Launch ImageJ as usual.
//		final ImageJ ij = new ImageJ();
//		ij.launch(args);
//
//		// Launch the "CommandWithPreview" command.
//		ij.command().run(EsferoideJ_.class, true);
//	}

}
