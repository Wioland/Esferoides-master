package funtions;

import javax.swing.JOptionPane;

import ij.io.DirectoryChooser;
import interfaces.GeneralView;
import interfaces.SelectAlgoritm;

public class Main {
	// PRUEBAS

	public static void callProgram(String dc) {	

		if (dc != null) {
			int selection = JOptionPane.showOptionDialog(null, "Select an option", "Option selecter",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // null para icono por
																					// defecto.
					new Object[] { "Use algoritm", "View results" }, // null para YES, NO y CANCEL
					"Use algoritm");

			switch (selection) {
			case 0:
				SelectAlgoritm seletAl = new SelectAlgoritm(dc);
				break;
			case 1:
				GeneralView ventana = new GeneralView(dc);
				break;
			default:
				break;
			}

		}
	}

	public static void main(String[] args) {
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		callProgram(dc.getDirectory());
	}

//		CreateListImageAlgori j= new CreateListImageAlgori();
//		try {
//			j.iniA(j.getClasses("esferoides"));
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

}
