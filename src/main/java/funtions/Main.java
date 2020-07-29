package funtions;

import interfaces.GeneralView;

/**
 * Main class of the program
 * 
 * @author Yolanda
 *
 */
public class Main {

	public static void main(String[] args) {
		GeneralView geView = new GeneralView("SpheroidJ"); // the main JFrame is create
		Utils.mainFrame = geView;
		
		FileFuntions.CheckIfUpdate(); // Check if update the program
		FileFuntions.chargePlugins(); // imageJ options
	}

}
