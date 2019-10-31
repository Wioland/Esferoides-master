package esferoides;

import java.awt.Polygon;
import java.io.IOException;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;

public  class EsferoideDad {
	
	protected static ArrayList<Integer> goodRows;

	// Method to keep the ROI with the biggest area stored in the ROIManager, the
		// rest of ROIs are
		// deleted.
	protected static void keepBiggestROI(RoiManager rm) {

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
//			IJ.showMessage(""+getArea(biggestROI.getPolygon()));
			rm.addRoi(biggestROI);

		}

	}
	
	// Method to obtain the area from a polygon. Probably, there is a most direct
	// method to do this.
	public static final double getArea(Polygon p) {
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

	
	
	
	// Method to draw the results stored in the roi manager into the image, and then
	// save the
	// image in a given directory. Since we know that there is only one esferoide
	// per image, we
	// only keep the ROI with the biggest area stored in the ROI Manager.
	protected static void showResultsAndSave(String dir, ImagePlus imp1, RoiManager rm, String nameClass) throws IOException {
		IJ.run(imp1, "RGB Color", "");

		String name = imp1.getTitle();
		// FileInfo f = imp1.getFileInfo();
		name = name.substring(0, name.indexOf("."));

		ImageStatistics stats = null;
		double[] vFeret;// = 0;
		double perimeter = 0;
		if (rm != null) {
			rm.setVisible(false);
			keepBiggestROI(rm);
			rm.runCommand("Show None");
			rm.runCommand("Show All");
			boolean smooth = false;
			if (smooth) {
				ImagePlus impN = IJ.createImage("Untitled", "16-bit white", imp1.getWidth(), imp1.getHeight(), 1);
				rm.select(0);
				rm.runCommand(impN, "Fill");
				rm.runCommand("Delete");
				IJ.setAutoThreshold(impN, "Default");
				IJ.run(impN, "Convert to Mask", "");
				IJ.run(impN, "Shape Smoothing",
						"relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");
				IJ.run(impN, "Analyze Particles...", "exclude add");
				impN.close();
				rm = RoiManager.getInstance();
				rm.runCommand("Show None");
				rm.runCommand("Show All");
			}

			Roi[] roi = rm.getRoisAsArray();

			if (roi.length != 0) {
				if(nameClass=="EsferoideJ_") {
					
					imp1.show();
					rm.select(0);
					IJ.run(imp1, "Fit Spline", "");
					rm.addRoi(imp1.getRoi());
					rm.select(0);
					rm.runCommand(imp1,"Delete");
					
					roi = rm.getRoisAsArray();
				}
				
				
				
				rm.runCommand(imp1, "Draw");
				rm.runCommand("Save", dir + name + ".zip");
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
//            if (rt == null) {
//
//                rt = new ResultsTable();
//            }
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
		}

		IJ.saveAs(imp1, "Tiff", dir + name + "_pred.tiff");
	}

	
	
	
}
