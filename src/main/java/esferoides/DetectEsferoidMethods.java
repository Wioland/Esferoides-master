package esferoides;

import java.io.File;
import java.util.ArrayList;

import funtions.FileFuntions;
import funtions.Utils;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.plugin.ImageCalculator;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;
import loci.plugins.in.ImporterOptions;

/**
 * 
 * @author Jonathan
 * @see <a href = "https://github.com/joheras/SpheroidJ" > This code has changes
 *      compared the original in this Github repository </a>
 *
 */
public class DetectEsferoidMethods {

	// Method to detect esferoides.
	public static void detectEsferoideFluoColageno(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		String nameClass = "FluoColageno";
		name = name.replace("fluo", "");
		options.setId(name);
		ImagePlus impFluo = FileFuntions.openImageIJ(name, options);

		ImagePlus impNoFluo = FileFuntions.openImageIJ(name, options);

		String title = impNoFluo.getTitle();

		ImagePlus imp = impNoFluo.duplicate();
		imp.setTitle(title);

		DetectEsferoidImageMethods.processEsferoidFluo(impFluo, true);
		DetectEsferoidImageMethods.processEsferoidNoFluo(impNoFluo);
		ImageCalculator ic = new ImageCalculator();
		ImagePlus imp3 = ic.run("Add create", impFluo, impNoFluo);
		IJ.run(imp3, "Fill Holes", "");
		RoiManager rm = AnalyseParticleMethods.analyzeParticlesFluo(imp3);

		imp3.close();
		impFluo.close();
		impNoFluo.close();
		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);
		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}

		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideFluoSuspension(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		options.setId(name);
		ImagePlus impFluo = FileFuntions.openImageIJ(name, options);
		String nameClass = "FluoSuspension";
		name = name.replace("fluo", "");

		ImagePlus impNoFluo = FileFuntions.openImageIJ(name, options);

		DetectEsferoidImageMethods.processEsferoidFluo(impFluo, false);
		RoiManager rm = AnalyseParticleMethods.analyzeParticlesFluo(impFluo);

		impFluo.close();
		if (temp) {
			Utils.showResultsAndSave(dir, name, impNoFluo, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, impNoFluo, rm, goodRows);
		}

		impNoFluo.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideHectorv2(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		options.setId(name);
		ImagePlus impb = FileFuntions.openImageIJ(name, options);

		String title = impb.getTitle();
		String nameClass = "Hectorv2";

		ImagePlus imp = impb.duplicate();
		imp.setTitle(title);
		IJ.run(imp, "8-bit", "");
		ImagePlus imp2 = imp.duplicate();
		imp2.setTitle(title);
		RoiManager rm = null;

		DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, true);
		rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
		if (rm == null || rm.getRoisAsArray().length == 0) {
			DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, false);
			rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
		}
		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}

		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideHectorv1(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		options.setId(name);

		ImagePlus impb = FileFuntions.openImageIJ(name, options);

		String title = impb.getTitle();
		String nameClass = "Hectorv1";

		ImagePlus imp = impb.duplicate();
		imp.setTitle(title);
		IJ.run(imp, "8-bit", "");
		ImagePlus imp2 = imp.duplicate();
		imp2.setTitle(title);
		RoiManager rm = null;

		DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, true);
		rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
		if (rm == null || rm.getRoisAsArray().length == 0) {
			DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, false);
			rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
		}

		if (rm == null || rm.getRoisAsArray().length == 0) {
			double v = 1.75;

			while ((rm == null || rm.getRoisAsArray().length == 0) && v >= 1.0) {
				imp2 = imp.duplicate();
				DetectEsferoidImageMethods.processEsferoidesGeneralCaseHector(imp2, 3, v);
				rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
				v = v - 0.25;
			}
		}

		if (rm == null || rm.getRoisAsArray().length == 0) {
			double v = 1.75;
			while ((rm == null || rm.getRoisAsArray().length == 0) && v >= 1.0) {
				imp2 = imp.duplicate();
				DetectEsferoidImageMethods.processEsferoidesGeneralCaseHector(imp2, 5, v);
				rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
				v = v - 0.25;
			}
		}

		if (rm == null || rm.getRoisAsArray().length == 0) {
			double v = 1.75;
			while ((rm == null || rm.getRoisAsArray().length == 0) && v >= 1.0) {
				imp2 = imp.duplicate();
				DetectEsferoidImageMethods.processEsferoidesGeneralCaseHector(imp2, 7, v);
				rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
				v = v - 0.25;
			}
		}

		if (rm == null || rm.getRoisAsArray().length == 0) {
			imp2 = imp.duplicate();
			DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, false);
			rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);

		}

		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}

		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideTeodora(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		options.setId(name);
		String nameClass = "Teodora";

		ImagePlus imp = FileFuntions.openImageIJ(name, options);
		ImagePlus imp2 = imp.duplicate();

		RoiManager rm;

		DetectEsferoidImageMethods.processEsferoidEdges(imp2, 0);
		rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, true);
		int iters = 1;
		while ((rm == null || rm.getRoisAsArray().length == 0) && iters < 7) {
			DetectEsferoidImageMethods.processEsferoidEdges(imp2, iters);
			rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, true);
			iters++;
		}
		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}

		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideTeodoraBig(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		options.setId(name);
		String nameClass = "TeodoraBig";

		ImagePlus imp = FileFuntions.openImageIJ(name, options);
		ImagePlus imp2 = imp.duplicate();

		RoiManager rm;

		int count = Utils.countBelowThreshold(imp2, 1100);
		boolean realBlackHole1 = Utils.countBetweenThresholdOver(imp2, 1100, 2000, 1500);
		boolean realBlackHole2 = Utils.countBelowThreshold(imp2, 3000) < 200000;

		if (count > 100 && realBlackHole2 && realBlackHole1) {

			if (count > 10000) {
				DetectEsferoidImageMethods.processBlackHoles(imp2, false, 0, 0, 0);
			} else {
				DetectEsferoidImageMethods.processBlackHoles(imp2, true, 0, 0, 0);
			}
			rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, true, true);
		} else {
			DetectEsferoidImageMethods.processEsferoidBig(imp2);
			rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);
		}

		imp2 = imp.duplicate();
		int iters = 0;
		while ((rm == null || rm.getRoisAsArray().length == 0) && iters < 7) {
			DetectEsferoidImageMethods.processEsferoidEdges(imp2, iters);
			rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);
			iters++;
		}

		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}

		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideTeodoraBigNoHoles(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {
		options.setId(name);

		String nameClass = "TeodoraBigNoHoles";

		ImagePlus imp = IJ.openImage(name);
		ImagePlus imp2 = imp.duplicate();

		RoiManager rm;

		DetectEsferoidImageMethods.processEsferoidBig(imp2);
		rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);

		imp2 = imp.duplicate();
		int iters = 0;
		while ((rm == null || rm.getRoisAsArray().length == 0) && iters < 7) {
			DetectEsferoidImageMethods.processEsferoidEdges(imp2, iters);
			rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);
			iters++;
		}

		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}

		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideFluoStack(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		options.setId(name);
		String nameClass = "FluoStack";

		ImagePlus imp = FileFuntions.openImageIJ(name, options);

		ImageStack stack = imp.getStack();
		ImagePlus impFluo = null;
		Calibration cal = imp.getCalibration();

		if (stack.getSize() > 1) {
			impFluo = new ImagePlus(stack.getSliceLabel(2), stack.getProcessor(2));
			impFluo.setCalibration(cal);
		} else {
			impFluo = new ImagePlus(stack.getSliceLabel(1), stack.getProcessor(1));
			impFluo.setCalibration(cal);
		}

		ImagePlus impNoFluo = new ImagePlus(stack.getSliceLabel(1), stack.getProcessor(1));
		impNoFluo.setCalibration(cal);

		imp = impNoFluo.duplicate();

		ImagePlus impFluoD = impFluo.duplicate();
		DetectEsferoidImageMethods.processEsferoidFluo(impFluoD, true);
		RoiManager rm = AnalyseParticleMethods.analyzeParticlesHector(impFluoD);
		Utils.keepBiggestROI(rm);

		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}
		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideTeniposide(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		String nameClass = "Teniposide";
		options.setId(name);

		ImagePlus imp = FileFuntions.openImageIJ(name, options);
		ImagePlus impD = imp.duplicate();

		DetectEsferoidImageMethods.processEsferoidEdgesThreshold(impD, 22, 255);
		RoiManager rm = AnalyseParticleMethods.analyzeParticlesFluo(impD);

		if (rm != null && rm.getRoisAsArray().length > 0) {
			Utils.keepBiggestROI(rm);
			Roi r = rm.getRoi(0);
			ImageStatistics stats = r.getStatistics();
			double solidity = (stats.area / Utils.getArea(r.getConvexHull()));
			System.out.println(solidity);
			int thesh = 23;
			while (solidity < 0.8 && thesh > 15) {
				System.out.println(thesh);
				thesh--;
				impD = imp.duplicate();
				DetectEsferoidImageMethods.processEsferoidEdgesThresholdDilateErode(impD, thesh, 255);
				rm = AnalyseParticleMethods.analyzeParticlesFluo(impD);
				Utils.keepBiggestROI(rm);
				r = rm.getRoi(0);
				stats = r.getStatistics();
				solidity = (stats.area / Utils.getArea(r.getConvexHull()));
				System.out.println(solidity);
			}
		} else {
			DetectEsferoidImageMethods.processEsferoidEdgesThresholdDilateErode(impD, 22, 255);
			rm = AnalyseParticleMethods.analyzeParticlesFluo(impD);
		}

		imp.close();

		if (temp) {
			Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

		} else {
			Utils.showResultsAndSaveNormal(dir, name, imp, rm, goodRows);
		}
		imp.close();

	}

	// Method to detect esferoides.
	public static void detectEsferoideDeep(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {
		options.setId(name);

		String nameClass = "EsferoideDeep";

		RoiManager rm;

		ImagePlus imp2 = DetectEsferoidImageMethods.processSpheroidDeep(dir, name, options);

		if (imp2 != null) {
//			ImagePlus imp = imp2.duplicate();

			rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);

			imp2 = FileFuntions.openImageIJ(name, options);
			if (temp) {
				Utils.showResultsAndSave(dir, name, imp2, rm, goodRows, nameClass, temp);

			} else {
				Utils.showResultsAndSaveNormal(dir, name, imp2, rm, goodRows);
			}

			imp2.close();

			File deepFolder = new File(dir + "temporalDeepFolder");
			FileFuntions.deleteFolder(deepFolder);
		}

	}

}
