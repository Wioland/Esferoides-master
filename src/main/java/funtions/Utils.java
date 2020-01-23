package funtions;

import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sleepycat.je.rep.elections.Protocol.Value;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.DirectoryChooser;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

public class Utils {

	// Method to search the list of files that satisfies a pattern in a folder. The
	// list of files
	// is stored in the result list.
	public static void search(final String pattern, final File folder, List<String> result) {
		for (final File f : folder.listFiles()) {

			if (f.isDirectory()) {
				search(pattern, f, result);
			}

			if (f.isFile()) {
				if (f.getName().matches(pattern)) {
					result.add(f.getAbsolutePath());
				}
			}

		}
	}

	// Method to search the list of files that satisfies a pattern in a folder. The
	// list of files
	// is stored in the result list.
	public static void searchDirectory(final String pattern, final File folder, List<String> result) {
		for (final File f : folder.listFiles()) {

			if (f.isFile()) {
				if (f.getName().matches(pattern)) {
					result.add(f.getAbsolutePath());
				}
			}

		}
	}

	// Method to draw the results stored in the roi manager into the image, and then
	// save the
	// image in a given directory. Since we know that there is only one esferoide
	// per image, we
	// only keep the ROI with the biggest area stored in the ROI Manager.
	public static void showResultsAndSave(String dir, String name, ImagePlus imp1, RoiManager rm,
			ArrayList<Integer> goodRows, String nameClass, boolean temp) throws IOException {
		IJ.run(imp1, "RGB Color", "");
		File folder;

		// String name = imp1.getTitle();
		// FileInfo f = imp1.getFileInfo();
		// dir=dir.replace(name, "");
		name = name.substring(0, name.lastIndexOf("."));
		name = name.replace(dir, "");
		folder = new File(dir + "predictions");

		if (!temp && !folder.exists()) {
			folder.mkdir();
		} else {

			folder = new File(dir + "temporal");
			name += "_" + nameClass.substring(nameClass.lastIndexOf(".") + 1);
			folder.mkdir();
		}

		ImageStatistics stats = null;
		double[] vFeret;// = 0;
		double perimeter = 0;
		if (rm != null) {
			rm.setVisible(false);
			keepBiggestROI(rm);
			rm.runCommand("Show None");
			rm.runCommand("Show All");

			Roi[] roi = rm.getRoisAsArray();

			if (roi.length != 0) {

				imp1.show();
				rm.select(0);
				IJ.run(imp1, "Fit Spline", "");
				rm.addRoi(imp1.getRoi());
				rm.select(0);
				rm.runCommand(imp1, "Delete");

				roi = rm.getRoisAsArray();

				rm.runCommand(imp1, "Draw");
				rm.runCommand("Save", folder.getAbsolutePath() + File.separator + name + ".zip");
				rm.close();
				// saving the roi
				// compute the statistics (without calibrate)
				stats = roi[0].getStatistics();

				vFeret = roi[0].getFeretValues();// .getFeretsDiameter();
				perimeter = roi[0].getLength();
				Calibration cal = imp1.getCalibration();
				double pw, ph;
				if (cal != null) {
					pw = cal.pixelWidth;
					ph = cal.pixelHeight;
				} else {
					pw = 1.0;
					ph = 1.0;
				}
				// calibrate the measures
				double area = stats.area * pw * ph;
				double w = imp1.getWidth() * pw;
				double h = imp1.getHeight() * ph;
				double aFraction = area / (w * h) * 100;
				double perim = perimeter * pw;

				ResultsTable rt = ResultsTable.getResultsTable();
				int nrows = Analyzer.getResultsTable().getCounter();
				goodRows.add(nrows - 1);

				rt.setPrecision(2);
				rt.setLabel(name, nrows - 1);
				rt.addValue("Area", area);
				rt.addValue("Area Fraction", aFraction);
				rt.addValue("Perimeter", perim);
				double circularity = perimeter == 0.0 ? 0.0 : 4.0 * Math.PI * (area / (perim * perim));
				if (circularity > 1.0) {
					circularity = 1.0;
				}
				rt.addValue("Circularity", circularity);
				rt.addValue("Diam. Feret", vFeret[0]);
				rt.addValue("Angle. Feret", vFeret[1]);
				rt.addValue("Min. Feret", vFeret[2]);
				rt.addValue("X Feret", vFeret[3]);
				rt.addValue("Y Feret", vFeret[4]);

				
				
			}

			IJ.saveAs(imp1, "Tiff", folder.getAbsolutePath() + File.separator + name + "_pred.tiff");
			
			
			ResultsTable rt = ResultsTable.getResultsTable();
			int rows = rt.getCounter();
			for (int i = rows; i > 0; i--) {
				if (!(goodRows.contains(i - 1))) {
					rt.deleteRow(i - 1);
				}else {
					String[] s=rt.getRowAsString(i-1).split(",");
					if(s.length==1) {
						s=rt.getRowAsString(i-1).split("\t");
					}
					
				
					if(s[1].equals("")) {
						rt.deleteRow(i - 1);
					}
				}
				
				
			}

			ExcelActions ete = new ExcelActions(rt, folder.getAbsolutePath() + File.separator);
			ete.convertToExcel();

			rt.reset();


		}

	}

	// Method to obtain the area from a polygon. Probably, there is a most direct
	// method to do this.
	private static final double getArea(Polygon p) {
		if (p == null)
			return Double.NaN;
		int carea = 0;
		int iminus1;
		for (int i = 0; i < p.npoints; i++) {
			iminus1 = i - 1;
			if (iminus1 < 0)
				iminus1 = p.npoints - 1;
			carea += (p.xpoints[i] + p.xpoints[iminus1]) * (p.ypoints[i] - p.ypoints[iminus1]);
		}
		return (Math.abs(carea / 2.0));
	}

	// Method to keep the ROI with the biggest area stored in the ROIManager, the
	// rest of ROIs are
	// deleted.
	private static void keepBiggestROI(RoiManager rm) {

		Roi[] rois = rm.getRoisAsArray();

		if (rois.length >= 1) {
			rm.runCommand("Select All");
			rm.runCommand("Delete");

			Roi biggestROI = rois[0];

			for (int i = 1; i < rois.length; i++) {

				if (getArea(biggestROI.getPolygon()) < getArea(rois[i].getPolygon())) {

					biggestROI = rois[i];
				}

			}
			rm.addRoi(biggestROI);

		}

	}

	public static String getByFormat(String format, List<String> result) {
		// We ask the user for a directory with nd2 images.

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the " + format + " images");

		if (dc.getDirectory() != null) {
			String dir = dc.getDirectory();
			// We store the list of nd2 files in the result list.
			File folder = new File(dir);

			Utils.search(".*\\." + format, folder, result);
			Collections.sort(result);
			return dir;
		}

		return null;

	}

	public static int countBelowThreshold(ImagePlus imp1, int threshold) {

		ImageProcessor ip = imp1.getProcessor();
		int[] histogram = ip.getHistogram();

		int countpixels = 0;
		for (int i = 0; i < threshold; i++) {
			countpixels = countpixels + histogram[i];
		}

		return countpixels;

	}

	public static boolean countBetweenThresholdOver(ImagePlus imp1, int threshold1, int threshold2, int num) {

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

}
