package esferoides;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import funtions.ExcelActions;
import ij.IJ;
import ij.measure.ResultsTable;
import interfaces.OurProgressBar;
import loci.plugins.in.ImporterOptions;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>EsferoideJ")
public class EsferoideJ_ implements Command {

	@Parameter(label = "Select type of images", choices = { "suspension", "colageno", "Hector no fluo v1","Hector no fluo v2", "Teodora v1" })
	private String type = "suspension";
	
	private static ArrayList<Integer> goodRows;
	
	@Override
	public void run() {
		
		try {
		// We initialize the ResultsTable
		ResultsTable rt = new ResultsTable();
		ImporterOptions options= new ImporterOptions();

		// We construct the EsferoidProcessorObject

		EsferoidProcessor esferoidProcessor = EsferoidProcessorFactory.createEsferoidProcessor(type);

		// We first read the list of files
		List<String> result = esferoidProcessor.getSearchFiles().apply();;
		String dir = result.get(0);
		result.remove(0);
		
		
//		// ProgressBar
//		
//		IJ.setForegroundColor(255, 0, 0);
		goodRows = new ArrayList<>();
//		JFrame frame = new JFrame("Work in progress");
//		JProgressBar progressBar = new JProgressBar();
//		progressBar.setValue(0);
//		progressBar.setString("");
//		progressBar.setStringPainted(true);
//		progressBar.setIndeterminate(true);
//		Border border = BorderFactory.createTitledBorder("Processing...");
//		progressBar.setBorder(border);
//		Container content = frame.getContentPane();
//		content.add(progressBar, BorderLayout.NORTH);
//		frame.setSize(300, 100);
//		frame.setVisible(true);
//		
		OurProgressBar pb= new OurProgressBar(null);

		// For each file in the folder we detect the esferoid on it.
		for (String name : result) {
			esferoidProcessor.getDetectEsferoid().apply(options, dir, name, goodRows,false);
		}

		rt = ResultsTable.getResultsTable();

		/// Remove empty rows
		int rows = rt.getCounter();
		for (int i = rows; i > 0; i--) {
			if (!(goodRows.contains(i - 1))) {
				rt.deleteRow(i - 1);
			}
		}

		ExcelActions ete = new ExcelActions(rt, dir);
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
