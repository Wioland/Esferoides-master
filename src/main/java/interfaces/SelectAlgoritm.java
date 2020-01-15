package interfaces;

import ij.IJ;

public class SelectAlgoritm {

	private String dir;

	public SelectAlgoritm(String directory, ImageTreePanel folderView) {

		

		this.dir = directory;

		
		IJ.run("EsferoideJ" ,"select="+dir +" type=colageno");
//		
//		List<String> jarNames = FileFuntions.getPluginNames();
//
//		JComboBox jcb = new JComboBox(jarNames.toArray());
//		jcb.setEditable(true);
//		JOptionPane.showMessageDialog(null, jcb, "select and algorith ", JOptionPane.QUESTION_MESSAGE);
//
//		System.out.println(jcb.getSelectedItem());
//		IJ.run(jcb.getSelectedItem().toString());

	}

}
