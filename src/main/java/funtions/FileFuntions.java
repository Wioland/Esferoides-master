package funtions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

public class FileFuntions {

	public static String getPathSelectedTreeFile(TreePath tp) {
		String path = "";

		for (int i = 0; i < tp.getPathCount(); i++) {
			path += tp.getPath()[i].toString();
			if (i > 0 && i != (tp.getPathCount() - 1)) {

				path += File.separator;

			}
		}

		return path;

	}

	public static File getTemporalFolderFromNd2Path(String nd2Path) {
		File folder = new File(nd2Path);
		folder = new File(nd2Path.replace(folder.getName(), "") + "temporal");
		return folder;
	}

	// guardar la imagen del algoritmo no solo el tiff tambien el zip
	// generados
	public static void saveSelectedImage(File selectedFile, String saveDirPath) {
		// se bucan los archivos tif y zip con el mismo nombre dentro de la carpeta
		// temporal

		// se coge el nombre del archivo creado y se le quita el nombre del algoritmo
		// utilizado

		// Se sobre escriben los archivos de la carpeta general con el mismo nombre

		List<String> temporalFiles = new ArrayList<String>();
		List<String> originalFiles = new ArrayList<String>();
		File saveDir = new File(saveDirPath);

		String originalPath = RoiFuntions.getNd2FilePathFromTempralTiff(selectedFile.getAbsolutePath());
		File fOld = new File(originalPath);
		String originalName = fOld.getName();
		String pattern = originalName.replace(".nd2", "*");

		System.out.println(pattern);
		System.out.println(originalName);

		Utils.search(pattern, new File(selectedFile.getAbsolutePath().replace(selectedFile.getName(), "")),
				temporalFiles);

		Utils.search(pattern, saveDir, originalFiles);

		for (String s : temporalFiles) {
			File f = new File(s);
			File newpath = new File(saveDirPath + f.getName());
			if (f.renameTo(newpath)) {

				if (fOld.exists()) {
					if (fOld.delete()) {
						f.renameTo(fOld);
					}
				}

			}
		}

	}

	// si se sale de la app o para borrar la carpeta tras seleccionar una imagen
	public static void deleteTemporalFolder(File temporalFolder) {

		if(temporalFolder.exists()) {
			File[] files = temporalFolder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteTemporalFolder(file);
				} else {
					file.delete();
				}

			}
			temporalFolder.delete();
		}
		

	}
}
