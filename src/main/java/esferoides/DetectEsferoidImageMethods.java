package esferoides;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;
/**
 * 
 * @author Jonathan
 * @see <a href =
 *      "https://github.com/joheras/SpheroidJ"
 *      > Github repository </a>
 *
 *
 */
public class DetectEsferoidImageMethods {

	public static void processEsferoidNoFluo(ImagePlus imp2) {

		IJ.run(imp2, "8-bit", "");
		IJ.run(imp2, "Find Edges", "");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.setRawThreshold(imp2, 30, 255, null);
		IJ.run(imp2, "Convert to Mask", "");

	}

	public static void processEsferoidNoFluoBis(ImagePlus imp2) {
		IJ.run(imp2, "8-bit", "");
		IJ.run(imp2, "Find Edges", "");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.setRawThreshold(imp2, 3000, 65550, null);
		IJ.run(imp2, "Convert to Mask", "");
	}

	public static void processEsferoidNoFluoThreshold(ImagePlus imp2) {
		IJ.run(imp2, "8-bit", "");
		IJ.setAutoThreshold(imp2, "Default");
		IJ.setRawThreshold(imp2, 0, 5000, null);
		IJ.run(imp2, "Convert to Mask", "");
	}

	public static void processEsferoidFluo(ImagePlus imp2, boolean threshold) {
		IJ.run(imp2, "8-bit", "");
		IJ.setAutoThreshold(imp2, "Li dark");
		IJ.run(imp2, "Convert to Mask", "");
	}

	public static void processEsferoidUsingThreshold(ImagePlus imp2, boolean dilate) {

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Default");
		}

		IJ.run(imp2, "Convert to Mask", "");
		if (dilate) {
			IJ.run(imp2, "Dilate", "");
		}
		IJ.run(imp2, "Fill Holes", "");
		if (dilate) {
			IJ.run(imp2, "Erode", "");
		}

		int w = imp2.getWidth();
		int h = imp2.getHeight();
		imp2.setRoi(10, 10, w - 10, h - 10);
		String title = imp2.getTitle();
		imp2 = imp2.duplicate();
		imp2.setTitle(title);
		IJ.run(imp2, "Canvas Size...", "width=" + w + " height=" + h + " position=Center");
		IJ.run(imp2, "Watershed", "");

	}

	public static void processEsferoidUsingThresholdOld(ImagePlus imp2) {

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Otsu");
		}

		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
		int w = imp2.getWidth();
		int h = imp2.getHeight();
		imp2.setRoi(10, 10, w - 10, h - 10);
		imp2 = imp2.duplicate();
		IJ.run(imp2, "Canvas Size...", "width=" + w + " height=" + h + " position=Center");

	}

	public static void processEsferoidUsingVariance(ImagePlus imp2) {

		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "Variance...", "radius=7");

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Otsu dark");
		}

		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		int w = imp2.getWidth();
		int h = imp2.getHeight();
		imp2.setRoi(10, 10, w - 10, h - 10);
		imp2 = imp2.duplicate();
		IJ.run(imp2, "Canvas Size...", "width=" + w + " height=" + h + " position=Center");

	}

	public static void processEsferoidesGeneralCaseHector(ImagePlus imp2, int maxFilter, double stdI) {

		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "Maximum...", "radius=" + maxFilter);

		ImageStatistics stats = imp2.getAllStatistics();
		double mean = stats.mean;
		double std = stats.stdDev;

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Default dark");
		}

		IJ.setRawThreshold(imp2, Math.floor(mean + stdI * std), 255, null);
		IJ.run(imp2, "Convert to Mask", "");

		if (maxFilter < 7) {
			IJ.run(imp2, "Dilate", "");
			IJ.run(imp2, "Dilate", "");
			IJ.run(imp2, "Fill Holes", "");
		}
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
		int w = imp2.getWidth();
		int h = imp2.getHeight();
		imp2.setRoi(50, 50, w - 50, h - 50);
		String title = imp2.getTitle();
		imp2 = imp2.duplicate();
		imp2.setTitle(title);
		IJ.run(imp2, "Canvas Size...", "width=" + w + " height=" + h + " position=Center");

	}

	public static void processEsferoidEdges(ImagePlus imp2, int iters) {

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

	public static void processBlackHoles(ImagePlus imp2, boolean dilate, int noNeedde, double Nonee, int noNE) {
		IJ.setThreshold(imp2, 0, 2300);
		IJ.run(imp2, "Convert to Mask", "");
		if (dilate) {
			IJ.run(imp2, "Fill Holes", "");
			IJ.run(imp2, "Dilate", "");
		}
		IJ.run(imp2, "Watershed", "");

	}

	public static void processEsferoidesGeneralCase(ImagePlus imp2) {
		IJ.run(imp2, "Convolve...",
				"text1=[-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 50 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n-1 -1 -1 -1 -1 -1 -1\n] normalize");

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

	}

	public static void processEsferoidUsingThreshold2(ImagePlus imp2) {

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Otsu");
		}

		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");

	}

	public static void processEsferoidUsingThresholdWithWatershed(ImagePlus imp2) {

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Otsu");
		}

		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Erode", "");
		IJ.run(imp2, "Watershed", "");

	}

	public static void processEsferoidVariance(ImagePlus imp2) {

		IJ.run(imp2, "Variance...", "radius=1");

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Default");
		}

		// IJ.setAutoThreshold(imp2, "Default"); // dark
		IJ.run(imp2, "Convert to Mask", "");

	}

	public static void processEsferoidVariance2(ImagePlus imp2) {

		IJ.run(imp2, "Variance...", "radius=1");

		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Default dark");
		}

		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Fill Holes", "");

	}

	public static void processEsferoidBig(ImagePlus imp2) {

		ImagePlus imp1 = imp2.duplicate();

		if (imp1.getBitDepth() == 24 || imp1.getBitDepth() == 32) {
			IJ.run(imp1, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp1, "Default");

		}

		IJ.run(imp1, "Convert to Mask", "");
		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "Find Edges", "");
		if (imp2.getBitDepth() == 24 || imp2.getBitDepth() == 32) {
			IJ.run(imp2, "Color Threshold...", "");

		} else {
			IJ.setAutoThreshold(imp2, "Default dark");

		}

		IJ.run(imp2, "Convert to Mask", "");
		ImageCalculator ic = new ImageCalculator();
		ic.run("ADD", imp2, imp1);
		IJ.run(imp2, "Fill Holes", "");
		imp1.changes = false;
		// imp2.changes=false;
		imp1.close();

	}

	public static void processEsferoidEdgesThreshold(ImagePlus imp2, int min, int max) {

		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "8-bit", "");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.setRawThreshold(imp2, min, max, null);
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Fill Holes", "");
	}

	public static void processEsferoidEdgesThresholdDilateErode(ImagePlus imp2, int min, int max) {

		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "8-bit", "");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.setRawThreshold(imp2, min, max, null);
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Dilate", "");
		IJ.run(imp2, "Fill Holes", "");
		IJ.run(imp2, "Erode", "");
	}

}
