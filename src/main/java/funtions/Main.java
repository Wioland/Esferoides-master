package funtions;

import javax.swing.JOptionPane;

import ij.io.DirectoryChooser;
import interfaces.GeneralView;
import interfaces.ImageTreePanel;

public class Main {
	// PRUEBAS

	public static void callProgram(String dc, ImageTreePanel folderView) {
		


		if (dc != null) {
			int selection = JOptionPane.showOptionDialog(null, "Select an option", "Option selecter",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // null para icono por
																					// defecto.
					new Object[] { "Detect esferoid", "View results" }, // null para YES, NO y CANCEL
					"Use algoritm");

			switch (selection) {
			case 0:
				createGeneralViewOrNot(folderView, dc,true);
				break;
			case 1:
				createGeneralViewOrNot(folderView, dc,false);

				break;
			default:
				break;
			}

		}
	}

	public static void createGeneralViewOrNot(ImageTreePanel folderView, String dc,boolean selectAlgo) {
		if (folderView == null) { // si no se estaba ya en un GeneralView se crea uno nuevo
			 new GeneralView(dc,selectAlgo);
		} else {
			folderView.repaintTabPanel(selectAlgo);

		}

	}

	public static void main(String[] args) {
		FileFuntions.chargePlugins();
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		callProgram(dc.getDirectory(), null);

	}

}
