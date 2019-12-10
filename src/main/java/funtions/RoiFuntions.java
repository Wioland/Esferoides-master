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
	
	public static String getRoiPathPredicctions(String pathOriginal) {
			
		String extension=FileFuntions.extensionwithoutName(pathOriginal);
		String roiPath=pathOriginal.replace(extension, "zip");
	
		
		File f = new File(roiPath);

		String dir=f.getAbsolutePath().replace(f.getName(), "");
		
		
		roiPath= dir+"predictions"+File.separator+f.getName();
		return roiPath;
	}
	
	public static void showOriginalFilePlusRoi(String path, String roiPath) {
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

		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String getoriginalFilePathFromTempralTiff(String pathTemporalTiff) {

		File f = new File(pathTemporalTiff.replace("temporal" + File.separator, ""));
		String tiffName = f.getName();
		f = new File(f.getAbsolutePath().replace(f.getName(), ""));
		String[] listFiles = f.list();
		String originalName = "";
		

		for (String name : listFiles) {
			if(!name.endsWith(".xls")) {
				String extension=FileFuntions.extensionwithoutName(name);
				if (tiffName.contains(name.replace("."+extension, ""))) {
					originalName = name;
					break;
				}
			}
			
		}
		originalName = f.getAbsolutePath() + File.separator + originalName;
		return originalName;
	}

	public static String getOriginalFilePathFromPredictions(String tiffPredictionsPath) {
		String path = tiffPredictionsPath.replace("_pred.tiff", ".nd2");
		path = path.replace(File.separator + "predictions", "");
		File faux= new File(path);
		
		if(!faux.exists()) {
			path = path.replace("nd2", "fluo.tif");
			faux= new File(path);
			if(!faux.exists()) {
				path = path.replace("fluo.tif", "tif");
			}
		}
		
		return path;
	}



}
