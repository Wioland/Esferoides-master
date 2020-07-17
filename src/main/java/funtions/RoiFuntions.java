package funtions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoiFuntions {

	/**
	 * GEt the roi of and image (current directory) in the predictions folder
	 * 
	 * @param pathOriginal path of the file in which the predictions folder is his
	 *                     roi
	 * @return path of the roi file
	 */
	public static String getRoiPathPredicctions(String pathOriginal) {

		String extension = FileFuntions.extensionwithoutName(pathOriginal);
		String roiPath = pathOriginal.replace(extension, "zip");

		File f = new File(roiPath);

		String dir = f.getAbsolutePath().replace(f.getName(), "");

		roiPath = dir + "predictions" + File.separator + f.getName();
		return roiPath;
	}

	/**
	 * Gets the path of the original file (nd2 or tiff) associated with the tiff
	 * file in the predictions folder
	 * 
	 * @param pathTemporalTiff path of a tiff image in the temporal folder
	 * @return The path of the original file associated with that tiff file
	 */
	public static String getoriginalFilePathFromTempralTiff(String pathTemporalTiff) {

		File f = new File(pathTemporalTiff.replace("temporal" + File.separator, ""));
		String tiffName = f.getName();
		f = new File(f.getAbsolutePath().replace(f.getName(), ""));
		String[] listFiles = f.list();
		String originalName = "";
		String extension;

		for (String name : listFiles) {
			if (!name.endsWith(".xls")) {
				extension = FileFuntions.extensionwithoutName(name);
				if (tiffName.contains(name.replace("." + extension, ""))) {
					originalName = name;
					break;
				}
			}

		}
		originalName = f.getAbsolutePath() + File.separator + originalName;
		return originalName;
	}

	/**
	 * gets the path of the original file (nd2 or tif) associated with the tiff file
	 * in predictions folder
	 * 
	 * @param tiffPredictionsPath path of a tiff file in the predictions folder
	 * @return the path of the original file associated with the tiff file in
	 *         prediction folder
	 */
	public static String getOriginalFilePathFromPredictions(String tiffPredictionsPath) {
		String path = tiffPredictionsPath.replace(File.separator + "predictions", "");
		File faux = new File(path);
		String pattern = faux.getName().replace("_pred.tiff", "") + ".*";
		List<String> result = new ArrayList<String>();

		Utils.search(pattern, new File(faux.getAbsolutePath().replace(faux.getName(), "")), result, 0);
		if (!result.isEmpty()) {
			path = result.get(0);
		}
		return path;
	}

}
