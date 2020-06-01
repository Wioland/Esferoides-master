package funtions;

import interfaces.GeneralView;

public class Main {

	public static void main(String[] args) {
		GeneralView geView = new GeneralView(); // the main JFrame is create
		Utils.mainFrame = geView;
		
		FileFuntions.CheckIfUpdate(); // Check if update the program
		FileFuntions.chargePlugins(); // imageJ options
	}

}
