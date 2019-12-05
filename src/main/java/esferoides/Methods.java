package esferoides;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.ExcelActions;
import ij.IJ;
import ij.measure.ResultsTable;
import interfaces.OurProgressBar;
import loci.plugins.in.ImporterOptions;

public class Methods {
	private static ArrayList<Integer> goodRows;
	private String[] algorithms = { "suspension", "colageno", "Hector no fluo v1", "Hector no fluo v2", "Teodora v1" };
	private static File temporalFolder;

	public Methods(String directory, List<String> result) {
		temporalFolder = new File(directory + "temporal");
		for (String type : algorithms) {
			if (type.equals("suspension") || type.equals("colageno")) {
				if (checkIfFluoImages(result)) {
					createImagesMetods(result, directory, type);
				}

			} else {
				if ((type.contains("Hector") && isExtension(result, "tif"))
						|| (type.equals("Teodora v1") && isExtension(result, "nd2"))) {
					createImagesMetods(result, directory, type);
				}
				
			}

		}
	}

	public static File getTemporalFolder() {
		return temporalFolder;
	}

	public void setTemporalFolder(File temporalFolder) {
		this.temporalFolder = temporalFolder;
	}

	private boolean isExtension(List<String> result, String extension) {
		boolean isTif = true;
		for (String name : result) {
			if (!name.endsWith(extension)) {
				isTif = false;
				break;
			}
		}
		return isTif;
	}



	private boolean checkIfFluoImages(List<String> result) {
		boolean haveFluo = true;
		File faux;
		String fileName;
		String nameNoextension;

		for (String name : result) {
			faux = new File(name);
			fileName = faux.getName();
			nameNoextension = fileName.split("\\.")[0];
			faux = new File(faux.getAbsolutePath().replace(nameNoextension, nameNoextension + "fluo"));

			if (!faux.exists()) {
				JOptionPane.showMessageDialog(null,
						"One or more images doesn´t have their fluo image or it isn´t in the same folder. This method needs a fluo image. Try it later when a fluo image is in the folder",
						"No fluo image. Can´t do the method", JOptionPane.WARNING_MESSAGE);
				haveFluo = false;
				break;
			}
		}

		return haveFluo;
	}

	private void createImagesMetods(List<String> result, String directory, String type) {
		try {
			// We initialize the ResultsTable
			ResultsTable rt = new ResultsTable();
			ImporterOptions options = new ImporterOptions();

			// We construct the EsferoidProcessorObject

			EsferoidProcessor esferoidProcessor = EsferoidProcessorFactory.createEsferoidProcessor(type);

			OurProgressBar pb = new OurProgressBar(null);
			goodRows = new ArrayList<>();
			// For each file in the folder we detect the esferoid on it.
			for (String name : result) {
				esferoidProcessor.getDetectEsferoid().apply(options, directory, name, goodRows, true);
			}

			rt = ResultsTable.getResultsTable();

			/// Remove empty rows
			int rows = rt.getCounter();
			for (int i = rows; i > 0; i--) {
				if (!(goodRows.contains(i - 1))) {
					rt.deleteRow(i - 1);
				}
			}

			ExcelActions ete = new ExcelActions(rt, directory);
			ete.convertToExcel();

			rt.reset();

			pb.setVisible(false);
			pb.dispose();
			IJ.showMessage("Process finished");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
