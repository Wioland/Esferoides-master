package funtions;

import java.io.File;

import javax.swing.JOptionPane;

import ij.io.DirectoryChooser;
import interfaces.GeneralView;
import interfaces.ImageTreePanel;

public class Main {
	// PRUEBAS

	public static void callProgram(String dc, ImageTreePanel folderView) {

		if (dc != null) {
		boolean b=optionAction();
		if(b) {
			b = FileFuntions.isOriginalImage(new File(dc));
		}
		createGeneralViewOrNot(folderView, dc, b);
		}
	}
	
	
	public static boolean optionAction() {
		
		boolean b=false;
		
		int selection = JOptionPane.showOptionDialog(null, "Select an option", "Option selecter",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // null para icono por
																				// defecto.
				new Object[] { "Detect esferoid", "View results" }, // null para YES, NO y CANCEL
				"Use algoritm");

		switch (selection) {
		case 0:
			b=true;
			break;
		case 1:
			b=false;

			break;
		default:
			break;
		}
		
		
		return b;
	}

	public static void createGeneralViewOrNot(ImageTreePanel folderView, String dc, boolean selectAlgo) {
		if (folderView == null) { // si no se estaba ya en un GeneralView se crea uno nuevo
			new GeneralView(dc, selectAlgo);
		} else {

			folderView.repaintTabPanel(selectAlgo);

		}

	}

	public static void main(String[] args) {
		FileFuntions.chargePlugins();
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		if (dc.getDirectory().endsWith("predictions")) {
			callProgram(dc.getDirectory().replace("predictions", ""), null);

		} else {
			if (dc.getDirectory().endsWith("predictions" + File.separator)) {
				callProgram(dc.getDirectory().replace("predictions" + File.separator, ""), null);
			} else {
				callProgram(dc.getDirectory(), null);
			}

		}

	}

}
