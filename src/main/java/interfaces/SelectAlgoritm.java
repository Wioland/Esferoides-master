package interfaces;

import esferoides.EsferoideJ_;
import esferoides.Methods;
import ij.IJ;
import ij.ImageJ;

public class SelectAlgoritm {


	private String dir;

	public SelectAlgoritm(String directory, ImageTreePanel folderView) {
	
		
		
		this.dir = directory;
		
		Methods.getAlgorithms();
		IJ.run("EsferoideJ_");
	
//		dir = esfe.getDir();
//		GeneralView ventana = new GeneralView(dir);

	}
	
	




}
