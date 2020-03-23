package funtions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import ij.io.DirectoryChooser;
import interfaces.GeneralView;

public class Main {

	public static void main(String[] args) {
		GeneralView geView = new GeneralView(); // the main JFrame is create

		FileFuntions.CheckIfUpdate();
		FileFuntions.chargePlugins(); // imageJ options

		// Choose the directory to work with
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		if (dc.getDirectory() != null) { // if a directory has been chosen

			if (dc.getDirectory().endsWith("predictions")) {
				// if the directory is the predictions directory, we work with
				// the parent that contains the original images
				Utils.callProgram(dc.getDirectory().replace("predictions", ""), geView);

			} else {
				if (dc.getDirectory().endsWith("predictions" + File.separator)) {
					Utils.callProgram(dc.getDirectory().replace("predictions" + File.separator, ""), geView);
				} else {
					Utils.callProgram(dc.getDirectory(), geView);
				}

			}
		} else {// if a directory hasn't been chosen the main JFrame is close
				// (the program)
			geView.dispose();
		}

	}

	

}
