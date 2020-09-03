package esferoides;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;

/**
 * 
 * @author Jonathan
 * @see <a href = "https://github.com/joheras/SpheroidJ" > Github repository
 *      </a>
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
//		IJ.run(imp2, "8-bit", "");
//		IJ.setAutoThreshold(imp2, "Li dark");
//		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "8-bit", "");
		IJ.setAutoThreshold(imp2, "RenyiEntropy dark");// Li, MaxEntropy

		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Erode", "");
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
	public static void processSpheroidDeep(ImagePlus imp2) {

//		try {
//			ProcessBuilder pBuilder = null;
//
//			boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
//
//			if (isWindows) {
//				pBuilder = new ProcessBuilder("cmd.exe", "/c", "deep-tumour-spheroid.exe image \"" + name + "\" \"" + dir.replace("\\", "\\\\") + "\"");
//			} else {
//				System.out.println("Linux ");
//				pBuilder = new ProcessBuilder("bash", "-ic", "deep-tumour-spheroid image '" + name + "' '" + dir + "'");
//			}
//
//			Process process = pBuilder.start();
//
//			StringBuilder output = new StringBuilder();
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//
//			String line;
//			while ((line = reader.readLine()) != null) {
//				output.append(line + "\n");
//			}
//
//			int exitVal = process.waitFor();
//			if (exitVal == 0) {
//				System.out.println("Success!");
//				System.out.println(output);
//			} else {
//				System.out.println("Error");
//				System.out.println("DirName: "+dir);
//				System.out.println(output);
//				System.out.println("Exit Value: " + Integer.toString(exitVal));
//				System.out.println("--------ErrorEnd--------");
//			}

//			String predictionPath = name.substring(0, name.lastIndexOf('.')) + "_pred.png";

//			ImagePlus imp2 = IJ.openImage(predictionPath);
//			imp2.show();
			IJ.setThreshold(imp2, 1, 255);

			// IJ.run('Options...', 'black');
			IJ.run(imp2, "Convert to Mask", "");
			IJ.run(imp2, "Create Selection", "");
//			RoiManager roiManager = new RoiManager();
//			roiManager.addRoi(imp2.getRoi());
//			roiManager.runCommand("Save", predictionPath.replace("_pred.png", ".roi"));
//			roiManager.close();

			imp2.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
//	public static void processEsferoidDeep(ImagePlus imp2) {
////
////		try {
////			Process p;
////			ProcessBuilder processBuilder = new ProcessBuilder();
////			processBuilder.command("bash", "-c", "spheroids " + name + " " + dir);
////
////			Process process = processBuilder.start();
////
////			StringBuilder output = new StringBuilder();
////
////			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////
////			String line;
////			while ((line = reader.readLine()) != null) {
////				output.append(line + "\n");
////			}
////
////			int exitVal = process.waitFor();
////			if (exitVal == 0) {
////				System.out.println("Success!");
////				System.out.println(output);
////				// System.exit(0);
////			} else {
////				System.out.println("Error");
////				System.out.println(exitVal);
////			}
//
////			String predictionPath = name.substring(0, name.lastIndexOf('.')) + "_pred";
////			System.out.println(predictionPath);
////			ImagePlus imp2 = IJ.openImage(predictionPath);
////			imp2.show();
//		IJ.run(imp2, "8-bit", "");
//		IJ.setAutoThreshold(imp2, "Default");
//		IJ.run(imp2, "Convert to Mask", "");
//		imp2.changes = false;
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//	}

	public static void processBlackHoles(ImagePlus imp2, boolean dilate) {
		IJ.setThreshold(imp2, 0, 2300);

		IJ.run(imp2, "Convert to Mask", "");
		if (dilate) {
			IJ.run(imp2, "Fill Holes", "");
			IJ.run(imp2, "Dilate", "");
		}
		IJ.run(imp2, "Watershed", "");
//		IJ.run(imp2, "Shape Smoothing", "relative_proportion_fds=5 absolute_number_fds=2 keep=[Relative_proportion of FDs]");

	}

	public static void processEsferoidUsingThreshold(ImagePlus imp2) {
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

	public static void processEsferoidUsingThresholdCombination(ImagePlus imp2) {

		ImagePlus imp1 = imp2.duplicate();
		IJ.run(imp1, "Find Edges", "");
		IJ.run(imp1, "Convert to Mask", "");
		IJ.run(imp1, "Morphological Filters", "operation=[Black Top Hat] element=Square radius=5");
		imp1.changes = false;
		imp1.close();
		ImagePlus imp4 = IJ.getImage();
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

		imp4.changes = false;
		imp4.close();

		imp2 = imp3;

	}

	public static void processEsferoidUsingFindEdges(ImagePlus imp2) {
		IJ.run(imp2, "Find Edges", "");
		IJ.run(imp2, "Convert to Mask", "");
		IJ.run(imp2, "Morphological Filters", "operation=[Black Top Hat] element=Square radius=5");
		imp2.close();
		ImagePlus imp3 = IJ.getImage();
		IJ.run(imp3, "Dilate", "");
		IJ.run(imp3, "Dilate", "");
		IJ.run(imp3, "Fill Holes", "");
		IJ.run(imp3, "Erode", "");
		IJ.run(imp3, "Erode", "");

	}

}
