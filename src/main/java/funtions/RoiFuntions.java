package funtions;

import java.io.File;
import java.io.IOException;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

public class RoiFuntions {
	
	public static String getRoiPathPredicctions(String pathNd2) {
		String roiPath=pathNd2.replace("nd2", "zip");
		File f = new File(roiPath);
		
		String dir=f.getAbsolutePath().replace(f.getName(), "");
		
		
		roiPath= dir+"predictions"+File.separator+f.getName();
		return roiPath;
	}
	
	public static void showNd2FilePlusRoi(String path, String roiPath) {
		ImagePlus[] imps;
		try {
			ImporterOptions options = new ImporterOptions();
			options.setWindowless(true);
			options.setId(path);
			options.setOpenAllSeries(true);
			imps = BF.openImagePlus(options);

			ImagePlus imp = imps[0];
			imp.show();

			ij.gui.Toolbar toolBarImageJ = new Toolbar();
			
			//toolBarImageJ.show(); // esto no hace que se muestre el toolbar en pantalla
			// System.out.println(IJ.getToolName());
			IJ.setTool("freehand");
			RoiManager roi = new RoiManager();

			//IJ.openImage(roiPath);
			
			roi.runCommand("Open", roiPath);
			roi.runCommand(imp, "Measure");
			ResultsTable r = ResultsTable.getResultsTable();
			// ij.WindowManager.addWindow(ij.measure.ResultsTable.getResultsWindow());

			r.show("Results");
			IJ.renameResults("d");
			//System.out.println(IJ.isResultsWindow());
			r.show("d");
			// IJ.renameResults("d","Results");
			// roi.multiMeasure(imp);

			// ij.WindowManager.getWindow("Results").show();;

		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String getNd2FilePathFromTempralTiff(String pathTemporalTiff) {

		File f = new File(pathTemporalTiff.replace("temporal" + File.separator, ""));
		String tiffName = f.getName();
		f = new File(f.getAbsolutePath().replace(f.getName(), ""));
		String[] listFiles = f.list();
		String nd2Name = "";

		for (String name : listFiles) {
			if(name.endsWith(".nd2")) {
				if (tiffName.contains(name.replace(".nd2", ""))) {
					nd2Name = name;
					break;
				}
			}
			
		}
		nd2Name = f.getAbsolutePath() + File.separator + nd2Name;
		return nd2Name;
	}

	public static String getNd2FilePathFromPredictions(String tiffPredictionsPath) {
		String path = tiffPredictionsPath.replace("_pred.tiff", ".nd2");
		path = path.replace(File.separator + "predictions", "");
		
		return path;
	}



}
