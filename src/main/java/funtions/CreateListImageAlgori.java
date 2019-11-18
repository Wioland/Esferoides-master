package funtions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import esferoides.EsferoideDad;
import ij.ImagePlus;
import ij.plugin.frame.RoiManager;

public class CreateListImageAlgori {

	private List<Method> algorithms;
	private File imaSelected;
	private String path;
	private static File temporalFolder;
	private List<String> noValidMethods;

	public List<Method> getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(List<Method> algorithms) {
		this.algorithms = algorithms;
	}

	public static File getTemporalFolder() {
		return temporalFolder;
	}

	public void setTemporalFolder(File temporalFol) {
		temporalFolder = temporalFol;
	}

	public CreateListImageAlgori() {
		iniNoValidMethods();
		try {
			iniA(getClasses("esferoides"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CreateListImageAlgori(File image) {

		// Seleccionar un path para la carpeta temporal
		iniNoValidMethods();
		try {

			String[] j = image.getCanonicalPath().split("\\\\");
			String p = image.getCanonicalPath().replace(j[j.length - 1], "");
			path = p + "temporal";
			// System.out.println(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.imaSelected = image;
		temporalFolder = new File(path);
		temporalFolder.mkdir();
		try {
			iniA(getClasses("esferoides"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void iniA(Class[] listClass) {
		algorithms = new ArrayList<Method>();
		for (Class class1 : listClass) {

			Method[] metClass = class1.getDeclaredMethods();
			// Method[] metClass = class1.getMethods();

			if (!class1.getName().equals("esferoides.EsferoideDad")) {
				for (Method method : metClass) {
					if (!validMethod(method.getName())) {
						algorithms.add(method);
						// se realiza el metodo, se guarda la imagen con el nombre del la clase_metodo y
						// me muestra por pantalla
					}

				}

			}

		}

	}

	private void iniNoValidMethods() {
		this.noValidMethods = new ArrayList<String>();

		noValidMethods.add("main");
		noValidMethods.add("draw");
		noValidMethods.add("keepBiggestROI");
		noValidMethods.add("analyzeParticles");
		noValidMethods.add("findLocalMaxima");
		noValidMethods.add("analyzeSmallPArticles");
		noValidMethods.add("getArea");

		// noValidMethods.add("run");
		// noValidMethods.add("");

	}

	private boolean validMethod(String name) {
		return this.noValidMethods.contains(name);

	}

	public static Class[] getClasses(String pckgname) throws ClassNotFoundException {
		ArrayList classes = new ArrayList();
		File directory = null;
		try {
			directory = new File(
					Thread.currentThread().getContextClassLoader().getResource(pckgname.replace('.', '/')).getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		if (directory.exists()) {
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					try {
						Class cl = Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6));
						classes.add(cl);
					} catch (ClassNotFoundException ex) {
					}
				}
			}
		} else {
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}

	// crear las imagenes con los distintos algoritmos de la imagen seleccionada
	// guardarlas en la carpeta y en la lista
	public List<File> createImagesAlgorithms() {
		List<File> ima = new ArrayList<File>();

		// se llama a los algoritmos de la lista de algoritmos y se aplican estos sobre
		// la imagen seleccionada

		for (Method m : algorithms) {
			// imagen selected va a tener todo el path de la imagen original a la que ya se
			// le ha aplicado un algoritmo, por loq ue hay que quitarle la ruta y sol
			// quedarme con el nombre del archivo
			
			//m.invoke(obj, args);

			boolean done = renameFile(m, ima);

			if (done) {
				System.out.println("El renombrado ha sido correcto");

				// En lugar de cear una nueva imagen, a la hora de realizar el algoritm, este ya
				// crea los archivos solo hayq ue cambiar el nombre añadiendole el nombre del
				// algoritmo
			}

			else {
				System.out.println("El renombrado no se ha podido realizar");
			}

		}

		return ima;

	}

	private boolean renameFile(Method m, List<File> ima) {
		String[] splitnameMethod = m.toString().split("\\.");
		String s = "_" + splitnameMethod[1] + "_" + m.getName();

		String[] splitName = this.imaSelected.getName().split("\\\\");
		String newImageName = splitName[splitName.length - 1].replace(".tiff", "_" + s + ".tiff");

		// File newImage = new File(path + "\\" + newImageName);
		File newImage = new File("C:\\Users\\yomendez\\Desktop\\prueba.tiff");

		File folderFile = new File(path + imaSelected.getName());
		ima.add(newImage);

		return folderFile.renameTo(newImage);

	}

	// guardar la imagen del algoritmo no solo el tiff tambien el zip
	// generados
	public void saveSelectedImage(File selectedFile, String saveDirPath, String algoritmClassName) {
		this.imaSelected = selectedFile;
		// se bucan los archivos tif y zip con el mismo nombre dentro de la carpeta
		// temporal

		// se coge el nombre del archivo creado y se le quita el nombre del algoritmo
		// utilizado

		// Se sobre escriben los archivos de la carpeta general con el mismo nombre

		List<String> temporalFiles = new ArrayList<String>();
		List<String> originalFiles = new ArrayList<String>();
		File saveDir = new File(saveDirPath);
		String pattern = selectedFile.getName().replace("tiff", "*");
		String originalName = selectedFile.getName().replace(algoritmClassName, "");

		System.out.println(pattern);
		System.out.println(originalName);

		Utils.search(pattern, new File(imaSelected.getAbsolutePath().replace(imaSelected.getName(), " ")),
				temporalFiles);

		pattern = pattern.replace(algoritmClassName, "");
		Utils.search(pattern, saveDir, originalFiles);

		for (String s : temporalFiles) {
			File f = new File(s);
			File newpath = new File(saveDirPath + f.getName());
			if (f.renameTo(newpath)) {

				File fOld = new File(f.getAbsolutePath().replace(algoritmClassName, ""));
				if (fOld.exists()) {
					if (fOld.delete()) {
						f.renameTo(fOld);
					}
				}

			}
		}

	}

	// si se sale de la app o para borrar la carpeta tras seleccionar una imagen
	public static void deleteTemporalFolder() {

		if (temporalFolder.delete())
			System.out.println(temporalFolder + " ha sido borrado correctamente");
		else
			System.out.println(temporalFolder + " no se ha podido borrar");
	}

}
