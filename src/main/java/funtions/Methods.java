package funtions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import esferoides.EsferoidProcessor;
import esferoides.EsferoidProcessorFactory;
import ij.IJ;
import ij.measure.ResultsTable;
import interfaces.OurProgressBar;
import loci.plugins.in.ImporterOptions;

public class Methods {

	public Methods(List<String> result) {
		try {
			// We initialize the ResultsTable
			ResultsTable rt = new ResultsTable();
			ImporterOptions options= new ImporterOptions();

			String dir = result.get(0);
			result.remove(0);
			
			boolean temp=false;
			
			if(result.size()==1) {
				temp=true;
			}
			
			if(temp) {
				dir+="temporal"+File.separator;
			}
					
			OurProgressBar pb= new OurProgressBar(null);

			// For each file in the folder we detect the esferoid on it.
			for (String name : result) {
				esferoidProcessor.getDetectEsferoid().apply(options, dir, name, goodRows,temp);
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

}
