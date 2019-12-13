package interfaces;

import java.awt.Component;

import esferoides.EsferoideJ_;
import esferoides.Methods;
import ij.IJ;
import ij.ImageJ;

public class SelectAlgoritm {


	private String dir;

	public SelectAlgoritm(String directory, ImageTreePanel folderView) {
	
		
	
		EsferoideJ_ esfe= new EsferoideJ_();
		IJ.runMacro("EsferoideJ");
		
		
		this.dir = directory;
		
		Methods.getAlgorithms();
		//IJ.run("EsferoideJ_.class");
	
		
//		Class<?> clazz = EsferoideJ_.class;
//		IJ.runPlugIn(clazz.getName(), "");
	
//		dir = esfe.getDir();
//		GeneralView ventana = new GeneralView(dir);

	}
	
	




}
