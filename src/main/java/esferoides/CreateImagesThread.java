package esferoides;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.Utils;
import loci.plugins.in.ImporterOptions;

public class CreateImagesThread extends Thread {

	private String directory;
	private List<String> result;
	private String type;
	private boolean all;
	private ArrayList<Integer> goodRows;
	private int num;

	public CreateImagesThread(List<String> result, String directory, String type, boolean all,int num) {

		this.directory = directory;
		this.result = result;
		this.type = type;
		this.all = all;
		this.num=num;

	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public ArrayList<Integer> getGoodRows() {
		return goodRows;
	}

	public void setGoodRows(ArrayList<Integer> goodRows) {
		this.goodRows = goodRows;
	}

	/**
	 * Method that creates the images in the temporal directory with the method
	 * given
	 */
	@Override
	public void run() {
		try {
System.out.println("Hilo "+ this.num);
			// In order to only take the tif images without the fluo ones
			if (type.contains("Hector")) {
				int i = 0;
				for (String name : result) {
					if (name.endsWith("fluo.tif")) {
						result.set(i, name.replace("fluo.tif", ".tif"));
						i++;
					}
				}
			}

			// We initialize the ResultsTable
			// ResultsTable rt = new ResultsTable();
			ImporterOptions options = new ImporterOptions();

			// We construct the EsferoidProcessorObject

			EsferoidProcessor esferoidProcessor = EsferoidProcessorFactory.createEsferoidProcessor(type, all);

			// OurProgressBar pb = new OurProgressBar(null);
			goodRows = new ArrayList<>();
			// For each file in the folder we detect the esferoid on it.
			for (String name : result) {
//				ThreadProcessor tp=new ThreadProcessor();
//				tp.start();
//				
				esferoidProcessor.getDetectEsferoid().apply(options, directory, name, goodRows, true);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog( Utils.mainFrame, "An error occurred while detecting the esferoid");
		}
	}

}
