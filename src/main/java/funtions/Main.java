package funtions;

import java.io.File;

import ij.io.DirectoryChooser;

public class Main {

	public static void main(String[] args) {
		FileFuntions.chargePlugins();
		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the images");
		if (dc.getDirectory().endsWith("predictions")) {
			Utils.callProgram(dc.getDirectory().replace("predictions", ""), null);

		} else {
			if (dc.getDirectory().endsWith("predictions" + File.separator)) {
				Utils.callProgram(dc.getDirectory().replace("predictions" + File.separator, ""), null);
			} else {
				Utils.callProgram(dc.getDirectory(), null);
			}

		}

	}

}
