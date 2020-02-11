package funtions;

import java.io.File;

import javax.swing.JOptionPane;

import ij.io.DirectoryChooser;
import interfaces.GeneralView;
import interfaces.ImageTreePanel;

public class Main {

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

	// METHODS

	/**
	 * Creates the Main frame or looks if there is a current one to repain
	 * 
	 * 
	 * @param dc         working directory
	 * @param folderView ImageTreePanel that shows the tree directory
	 */
	public static void callProgram(String dc, ImageTreePanel folderView) {

		if (dc != null) {
			boolean b = optionAction();
			if (b) {
				b = FileFuntions.isOriginalImage(new File(dc));
			}
			createGeneralViewOrNot(folderView, dc, b);
		}
	}

	/**
	 * JoptionPanel that ask you which action do you what to perform (DEtect
	 * esferoide or view results)
	 * 
	 * @return true if detected esferoide false if view result
	 */
	public static boolean optionAction() {

		boolean b = false;

		int selection = JOptionPane.showOptionDialog(null, "Select an option", "Option selecter",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new Object[] { "Detect esferoid", "View results" }, "Use algoritm");

		switch (selection) {
		case 0:
			b = true;
			break;
		case 1:
			b = false;

			break;
		default:
			break;
		}

		return b;
	}

	/**
	 * Checks if there is a main frame. if it is you repaint the tabpanel if not you
	 * create a new one
	 * 
	 * @param folderView imageTreepanel that shows the tree directory
	 * @param dc         the path of the current directory
	 * @param selectAlgo true if you select previously detect esferoide and false
	 *                   otherwise
	 */
	public static void createGeneralViewOrNot(ImageTreePanel folderView, String dc, boolean selectAlgo) {
		if (folderView == null) { // if there isn't a main/GenearalWiew Jframe open we create a new one
			new GeneralView(dc, selectAlgo);
		} else {
			// We repaint the tab panel with the new content
			folderView.repaintTabPanel(selectAlgo);

		}

	}

}
