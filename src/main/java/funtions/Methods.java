package funtions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import bsh.This;
import ij.IJ;
import loci.formats.FormatException;
import loci.plugins.in.ImporterOptions;


public class Methods {

	protected static ArrayList<Integer> goodRows;

	public static void esferoideJ_Run(String imagePath,String format) {
		IJ.setForegroundColor(255, 0, 0);
		goodRows = new ArrayList<>();
		List<String> result = new ArrayList<String>();
		String imageName = "";
		String path = "";
		String className = "";

		if (imagePath == null) { // caso de que no se pase un directorio y entonces tenga que seleccionar el que
									// quiere

			Object[] options = { "ND2", "Tiff", "Cancel" };
			int n = JOptionPane.showOptionDialog(null, "Would you like green eggs and ham?", "A Silly Question",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			switch (n) {
			case 0:
				format = "nd2";
				break;

			case 1:
				format = "tiff";
				break;

			default:
				break;
			}

			if (format !=null) {
				path = EsferoideDad.getByFormat(format, result);
			} else {

				// cancelada la accion
			}

		} else { // caso en el que se le pasa un directorio y hay que distinguir si tambien se le
					// ha pasado una imagen o es solo una carpeta
			File folder = new File(imagePath);
			if (folder.isDirectory()) {
				path = imagePath;

				Utils.search(".*\\." + format, folder, result);
				Collections.sort(result);
			} else {
				path = imagePath;
				imageName = folder.getName();
				result.add(imagePath);
			}
		}

		EsferoideDad j = null;
		
		if (imagePath.endsWith(".nd2") || format == "nd2") {
			className = "EsferoideJ_";
			j= new EsferoideJ_();
		} else {
			if (imagePath.endsWith(".tiff") || format == "tiff") {
				className = "EsferoideJv2_";
				j= new EsferoideJv2_();
			}

		}
		if (path != null && !className.contentEquals("")) {

			//EsferoideDad.createResultTable(result, path, className);
			EsferoideDad.setGoodRows(goodRows);
			j.createResultTable(result, className);
			
		}
	}
//
//	public static void createDatasetRun() {
//
//		ImporterOptions options;
//		try {
//			options = new ImporterOptions();
//
//			options.setWindowless(true);
//
//			List<String> result = new ArrayList<String>();
//			String dir = EsferoideDad.getByFormat("nd2", result);
//
//			if (dir != null) {
//				for (String name : result) {
//					CreateDataset.saveImageAndMask(options, dir, name);
//
//				}
//
//			}
//
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (FormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public static void EsferoideHistogramMosaicJ_Run() {
//		EsferoideHistogramMosaicJ_ j = new EsferoideHistogramMosaicJ_();
//		j.run();
//
//	}
//
//	public static void esferoideMosaicJ_Run() {
//		EsferoideMosaicJ_ k = new EsferoideMosaicJ_();
//		k.run();
//
//	}
//
//	public static void ExtractHistogramsJ_Run() {
//		ExtractHistogramsJ_ j = new ExtractHistogramsJ_();
//		j.run();
//
//	}

}
