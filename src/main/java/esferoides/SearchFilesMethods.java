package esferoides;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.Utils;
import ij.io.DirectoryChooser;
import loci.plugins.in.ImporterOptions;

public class SearchFilesMethods {

	public static List<String> searchFilesFluo() {
		List<String> result = new ArrayList<String>();
		try {

			ImporterOptions options = new ImporterOptions();

			options.setWindowless(true);
			// We ask the user for a directory with nd2 images.
			DirectoryChooser dc = new DirectoryChooser("Select the folder containing the fluo and tif images");
			String dir = dc.getDirectory();

			// We store the list of tiff files in the result list.
			File folder = new File(dir);

			Utils.search(".*fluo.tif", folder, result);
			Collections.sort(result);
			result.add(0, dir);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while searching Fluo images");
		}
		return result;
	}

	public static List<String> searchFilesTeodora() {
		// Since we are working with nd2 images that are imported with the Bio-formats
		// plugins, we must set to true the option windowless to avoid that the program
		// shows us a confirmation dialog every time.
		List<String> result = new ArrayList<String>();
		try {

			ImporterOptions options = new ImporterOptions();

			options.setWindowless(true);
//			// We ask the user for a directory with nd2 images.
//			// We store the list of tiff files in the result list.

			String dir = Utils.getByFormat("nd2", result);
			result.add(0, dir);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while searching Nd2 images");
		}
		return result;
	}

	public static List<String> searchFilesHectorNoFluo() {
		// Since we are working with nd2 images that are imported with the Bio-formats
		// plugins, we must set to true the option windowless to avoid that the program
		// shows us a confirmation dialog every time.
		List<String> result = new ArrayList<String>();
		try {

			ImporterOptions options = new ImporterOptions();

			options.setWindowless(true);
			// We ask the user for a directory with nd2 images.
			DirectoryChooser dc = new DirectoryChooser("Select the folder containing the tif images");
			String dir = dc.getDirectory();

			// We store the list of tiff files in the result list.
			File folder = new File(dir);

			Utils.search(".*\\.tif", folder, result);
			Collections.sort(result);
			result.add(0, dir);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while searching tif images");
		}
		return result;
	}

}
