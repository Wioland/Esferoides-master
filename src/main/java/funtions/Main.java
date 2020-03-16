package funtions;

import java.io.File;

import ij.io.DirectoryChooser;
import interfaces.GeneralView;

public class Main {

	public static void main(String[] args) {
		GeneralView geView = new GeneralView();
		FileFuntions.createUpdater();
		FileFuntions.chargePlugins();
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		if (dc.getDirectory() != null) {

			if (dc.getDirectory().endsWith("predictions")) {
				Utils.callProgram(dc.getDirectory().replace("predictions", ""), geView);

			} else {
				if (dc.getDirectory().endsWith("predictions" + File.separator)) {
					Utils.callProgram(dc.getDirectory().replace("predictions" + File.separator, ""), geView);
				} else {
					Utils.callProgram(dc.getDirectory(), geView);
				}

			}
		}else{
			geView.dispose();
		}

	}

}
