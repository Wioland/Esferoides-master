package esferoides;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import funtions.Utils;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.frame.RoiManager;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

public class DetectEsferoidMethods {

	public static Method[] getMethodsProcessor() {
		return DetectEsferoidImageMethods.class.getDeclaredMethods();

	}

	// Method to detect esferoides.
	public static void detectEsferoideFluoColageno(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		ImagePlus impFluo = IJ.openImage(name);

		name = name.replace("fluo", "");
		ImagePlus impNoFluo = IJ.openImage(name);

		String title = impNoFluo.getTitle();

		ImagePlus imp = impNoFluo.duplicate();
		imp.setTitle(title);

//		DetectEsferoidImageMethods.processEsferoidFluo(impFluo, true);
//		DetectEsferoidImageMethods.processEsferoidNoFluo(impNoFluo);

		Method[] methods = getMethodsProcessor();
		ImagePlus imp3 = null;
		RoiManager rm;
		String nameClass ;
		ImageCalculator ic;
		for (Method method : methods) {

			try {
				method.invoke(null, impFluo, true, -1, -1, 0);

				for (Method method2 : methods) {

					method2.invoke(null, impNoFluo, true, -1, -1, 0);
					 ic = new ImageCalculator();
					imp3 = ic.run("Add create", impFluo, impNoFluo);

					if (imp3.getBitDepth() != 8) {
						IJ.run(imp3, "8-bit", "");
						IJ.run(imp3, "Make Binary", "");
					}

					IJ.run(imp3, "Fill Holes", "");
					rm = AnalyseParticleMethods.analyzeParticlesFluo(imp3);

					 nameClass = "FluoColageno_" + method.getName() + "_" + method2.getName();
					Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

					
					
					IJ.run("Close All");
					
					impFluo = IJ.openImage(name);

					name = name.replace("fluo", "");
					impNoFluo = IJ.openImage(name);

					title = impNoFluo.getTitle();

					imp = impNoFluo.duplicate();
					imp.setTitle(title);
					//imp3 = null;

				}
			} catch (IllegalAccessException | IllegalArgumentException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		imp3.close();
		impFluo.close();
		impNoFluo.close();
		imp.close();
	}

	// Method to detect esferoides.
	public static void detectEsferoideFluoSuspension(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {

		ImagePlus impFluo = IJ.openImage(name);

		name = name.replace("fluo", "");
		ImagePlus impNoFluo = IJ.openImage(name);

		// DetectEsferoidImageMethods.processEsferoidFluo(impFluo, false);

		Method[] methods = getMethodsProcessor();
		RoiManager rm;
		String nameClass;
		for (Method method : methods) {

			try {

				method.invoke(null, impFluo, false, -1, -1, 0);

				 rm= AnalyseParticleMethods.analyzeParticlesFluo(impFluo);

				 nameClass = "FluoSuspension_" + method.getName();
				Utils.showResultsAndSave(dir, name, impNoFluo, rm, goodRows, nameClass, temp);
				
				
				IJ.run("Close All");
				impFluo = IJ.openImage(name);
				name = name.replace("fluo", "");
				impNoFluo = IJ.openImage(name);

			} catch (IllegalAccessException | IllegalArgumentException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		impFluo.close();
		impNoFluo.close();
	}

	// Method to detect esferoides.
	public static void detectEsferoideHectorv2(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {
		ImagePlus impb = IJ.openImage(name);
		String title = impb.getTitle();

		ImagePlus imp = impb.duplicate();
		imp.setTitle(title);
		IJ.run(imp, "8-bit", "");
		ImagePlus imp2 = imp.duplicate();
		imp2.setTitle(title);
		RoiManager rm = null;
		String nameClass;
		String aux;
		
		// DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, true);
		String bits = String.valueOf(imp2.getBitDepth()) + "-bit";
		if (bits == "24-bit") {
			bits = "RGB";
		}

		Method[] methods = getMethodsProcessor();
		for (Method method : methods) {

			try {
				method.invoke(null, imp2, true, -1, -1, 0);

				rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);

				if (rm == null || rm.getRoisAsArray().length == 0) {
					// DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, false);

					try {
						method.invoke(null, imp2, false, -1, -1, 0);

					} catch (IllegalAccessException | IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

				 nameClass = "Hectorv2_" + method.getName();
				Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

				
				IJ.run("Close All");
				impb = IJ.openImage(name);
				title = impb.getTitle();

				imp = impb.duplicate();
				imp.setTitle(title);
				IJ.run(imp, "8-bit", "");
				imp2 = imp.duplicate();
				imp2.setTitle(title);

				if (rm != null) {
					rm.runCommand("Deselect");
					rm.run("Delete");
				}
			aux = String.valueOf(imp2.getBitDepth()) + "-bit";
				if (aux == "24-bit") {
					aux = "RGB";
				}
				if (aux != bits) {
					IJ.run(imp2, bits, "");

				}
			} catch (IllegalAccessException | IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		imp.close();
	}

	// Method to detect esferoides.
	public static void detectEsferoideHectorv1(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {
		ImagePlus impb = IJ.openImage(name);
		String title = impb.getTitle();

		ImagePlus imp = impb.duplicate();
		imp.setTitle(title);
		IJ.run(imp, "8-bit", "");
		ImagePlus imp2 = imp.duplicate();
		imp2.setTitle(title);
		RoiManager rm = null;
		String nameClass;
		String aux;
		
		// DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, true);
		String bits = String.valueOf(imp2.getBitDepth()) + "-bit";
		if (bits == "24-bit") {
			bits = "RGB";
		}

		Method[] methods = getMethodsProcessor();
		for (Method method : methods) {
			try {
				method.invoke(null, imp2, true, -1, -1, 0);
				rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
				if (rm == null || rm.getRoisAsArray().length == 0) {
					method.invoke(null, imp2, false, -1, -1, 0);
					// DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, false);

					rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
				}

				if (rm == null || rm.getRoisAsArray().length == 0) {
					double v = 1.75;

					while ((rm == null || rm.getRoisAsArray().length == 0) && v >= 1.0) {
						imp2 = imp.duplicate();
						method.invoke(null, imp2, false, 3, v, 0);
						// DetectEsferoidImageMethods.processEsferoidesGeneralCaseHector(imp2, 3, v);

						rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
						v = v - 0.25;
					}
				}

				if (rm == null || rm.getRoisAsArray().length == 0) {
					double v = 1.75;
					while ((rm == null || rm.getRoisAsArray().length == 0) && v >= 1.0) {
						imp2 = imp.duplicate();
						// DetectEsferoidImageMethods.processEsferoidesGeneralCaseHector(imp2, 5, v);
						method.invoke(null, imp2, false, 5, v, 0);
						rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
						v = v - 0.25;
					}
				}

				if (rm == null || rm.getRoisAsArray().length == 0) {
					double v = 1.75;
					while ((rm == null || rm.getRoisAsArray().length == 0) && v >= 1.0) {
						imp2 = imp.duplicate();
						// DetectEsferoidImageMethods.processEsferoidesGeneralCaseHector(imp2, 7, v);
						method.invoke(null, imp2, false, 7, v, 0);
						rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);
						v = v - 0.25;
					}
				}

				if (rm == null || rm.getRoisAsArray().length == 0) {
					imp2 = imp.duplicate();
					// DetectEsferoidImageMethods.processEsferoidUsingThreshold(imp2, false);
					method.invoke(null, imp2, false, -1, -1, 0);
					rm = AnalyseParticleMethods.analyzeParticlesHector(imp2);

				}

				 nameClass = "Hectorv1_" + method.getName();
				Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

				
				IJ.run("Close All");
				impb = IJ.openImage(name);
				title = impb.getTitle();

				imp = impb.duplicate();
				imp.setTitle(title);
				IJ.run(imp, "8-bit", "");
				imp2 = imp.duplicate();
				imp2.setTitle(title);

				if (rm != null) {
					rm.runCommand("Deselect");
					rm.run("Delete");
				}
				 aux = String.valueOf(imp2.getBitDepth()) + "-bit";
				if (aux == "24-bit") {
					aux = "RGB";
				}
				if (aux != bits) {
					IJ.run(imp2, bits, "");

				}

			} catch (IllegalAccessException | IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		imp.close();
	}

	// Method to detect esferoides.
	public static void detectEsferoideTeodora(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {
		options.setId(name);

		ImagePlus[] imps;
		try {
			imps = BF.openImagePlus(options);

			ImagePlus imp = imps[0];
			ImagePlus imp2 = imp.duplicate();

			/// We consider two cases, when there is a "black hole" in the image (the first
			/// case), there is a lot of pixels below a given threshold, and those pixels
			/// belong to the Esferoide. In addition to be a black hole, there must be a
			/// difference between that region and the rest of the image.
//			int count = countBelowThreshold(imp2, 1100);
//			boolean realBlackHole = countBetweenThresholdOver(imp2, 1100, 2000, 1500);
//			System.out.println(realBlackHole);
			RoiManager rm;
			String nameClass;
			String aux;
			// DetectEsferoidImageMethods.processEsferoidEdges(imp2, 0);

			String bits = String.valueOf(imp2.getBitDepth()) + "-bit";
			if (bits == "24-bit") {
				bits = "RGB";
			}

			Method[] methods = getMethodsProcessor();
			for (Method method : methods) {

				method.invoke(null, imp2, false, -1, -1, 0);
				rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, true);

				int iters = 1;
				while ((rm == null || rm.getRoisAsArray().length == 0) && iters < 7) {
					// DetectEsferoidImageMethods.processEsferoidEdges(imp2, iters);
					method.invoke(null, imp2, false, -1, -1, iters);
					rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, true);
					iters++;
				}

				 nameClass = "TeodoraV1_" + method.getName();
				Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

				
				IJ.run("Close All");
				imps = BF.openImagePlus(options);
				imp = imps[0];
				imp2 = imp.duplicate();

				if (rm != null) {
					rm.runCommand("Deselect");
					rm.run("Delete");
				}

				 aux = String.valueOf(imp2.getBitDepth()) + "-bit";
				if (aux == "24-bit") {
					aux = "RGB";
				}
				if (aux != bits) {
					IJ.run(imp2, bits, "");

				}

			}
			imp.close();

		} catch (FormatException | IOException | IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Method to detect esferoides.
	public static void detectEsferoideTeodoraBig(ImporterOptions options, String dir, String name,
			ArrayList<Integer> goodRows, boolean temp) {
		options.setId(name);

		ImagePlus[] imps;
		try {
			imps = BF.openImagePlus(options);

			ImagePlus imp = imps[0];
			ImagePlus imp2 = imp.duplicate();

			RoiManager rm;
			String nameClass;
			String aux ;

			int count = Utils.countBelowThreshold(imp2, 1100);
			boolean realBlackHole1 = Utils.countBetweenThresholdOver(imp2, 1100, 2000, 1500);
			boolean realBlackHole2 = Utils.countBelowThreshold(imp2, 3000) < 200000;

			String bits = String.valueOf(imp2.getBitDepth()) + "-bit";
			if (bits == "24-bit") {
				bits = "RGB";
			}

			Method[] methods = getMethodsProcessor();

			for (Method method : methods) {

				if (count > 100 && realBlackHole2 && realBlackHole1) {

					if (count > 10000) {
						// DetectEsferoidImageMethods.processBlackHoles(imp2, false);
						method.invoke(null, imp2, false, -1, -1, 0);
					} else {
						// DetectEsferoidImageMethods.processBlackHoles(imp2, true);
						method.invoke(null, imp2, true, -1, -1, 0);
					}
					rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, true, true);
				} else {
					// DetectEsferoidImageMethods.processEsferoidBig(imp2);
					method.invoke(null, imp2, false, -1, -1, 0);
					rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);
				}

				int iters = 0;
				while ((rm == null || rm.getRoisAsArray().length == 0) && iters < 7) {
					// DetectEsferoidImageMethods.processEsferoidEdges(imp2, iters);
					method.invoke(null, imp2, false, -1, -1, iters);
					rm = AnalyseParticleMethods.analyseParticlesTeodora(imp2, false, false);
					iters++;
				}

				 nameClass = "Teodora_Big_" + method.getName();
				Utils.showResultsAndSave(dir, name, imp, rm, goodRows, nameClass, temp);

				
				IJ.run("Close All");
				imps = BF.openImagePlus(options);
				imp = imps[0];
				imp2 = imp.duplicate();

				if (rm != null) {
					rm.runCommand("Deselect");
					rm.run("Delete");
				}
				 aux = String.valueOf(imp2.getBitDepth()) + "-bit";
				if (aux == "24-bit") {
					aux = "RGB";
				}
				if (aux != bits) {
					IJ.run(imp2, bits, "");

				}
			}
			imp.close();

		} catch (FormatException | IOException | IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
