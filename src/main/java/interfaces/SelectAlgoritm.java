package interfaces;

import esferoides.EsferoideJ_;
import ij.IJ;

public class SelectAlgoritm {

	private String dir;

	public SelectAlgoritm(String directory, ImageTreePanel folderView) {

		// EsferoideJ_ esfe= new EsferoideJ_();

		this.dir = directory;

		// Methods.getAlgorithms();
		// IJ.run( "EsferoideJ");
		// IJ.run("EsferoideJ_.class");

		Class<?> clazz = EsferoideJ_.class;
		IJ.runPlugIn(clazz.getName(), "");

//		dir = esfe.getDir();
//		GeneralView ventana = new GeneralView(dir);

	}

}
