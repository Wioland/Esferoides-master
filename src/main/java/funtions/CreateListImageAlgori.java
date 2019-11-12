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

	public static File getTemporalFolder() {
		return temporalFolder;
	}

	public void setTemporalFolder(File temporalFol) {
		temporalFolder = temporalFol;
	}

	public CreateListImageAlgori() {

	}

	public CreateListImageAlgori(File image) {

		// Seleccionar un path para la carpeta temporal
		
		try {
			
			String[] j= image.getCanonicalPath().split("\\\\");
			String p=image.getCanonicalPath().replace(j[j.length-1], "");
			path=p+"temporal";
			System.out.println(path);
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
		algorithms= new ArrayList<Method>();
		for (Class class1 : listClass) {
			//System.out.println(class1.getName() + "    fgfdghdfjkghdfjkghfkjdghfkjghfkjghfjkghfjkghfjk  ");

			Method[] metClass = class1.getDeclaredMethods(); // no coge los privados
			//Method[] metClass = class1.getMethods();

			if (!class1.getName().equals("esferoides.EsferoideDad")) {
				for (Method method : metClass) {
//					System.out.println(method.getName());
//					Parameter[] kkk = method.getParameters();
//					 for (Parameter p : kkk) {
//						System.out.println("  -"+p.toString());
//					}
//					System.out.println("  ");
//					
					
					algorithms.add(method);
					// se realiza el metodo, se guarda la imagen con el nombre del la clase_metodo y
					// me muestra por pantalla
				}

			}

		}

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
			//imagen selected va a tener todo el path de la imagen original a la que ya se le ha aplicado un algoritmo, por loq ue hay que quitarle la ruta y sol quedarme con el nombre del archivo
			String[] splitName=this.imaSelected.getName().split("\\\\");
			String newImageName=splitName[splitName.length-1].replace(".tiff", "_"+m.getName()+"_"+".tiff");
			
		
			
			File newImage= new File(path+"\\"+newImageName);
			ima.add(newImage);
			//En lugar de cear una nueva imagen, a la hora de realizar el algoritm, este ya crea los archivos solo hayq ue cambiar el nombre añadiendole el nombre del algoritmo
		}

		return ima;

	}

	// guardar la imagen del algoritmo no solo el tiff tambien el nd2 y el zip
	// generados
	public void saveSelectedImage(File selectedFile, String saveDir,String algoritmClassName ) {
		this.imaSelected = selectedFile;
		// se bucan los archivos nd2 y zip con el mismo nombre dentro de la carpeta
		// temporal

		// se coge el nombre del archivo creado y se le quita el nombre del algoritmo
		// utilizado

		// Se sobre escriben los archivos de la carpeta general con el mismo nombre

		String[] splitUrl = imaSelected.getAbsolutePath().split("\\");
		String roiName = splitUrl[splitUrl.length - 1].replace("tiff", "zip");
		RoiManager roi = new RoiManager();
		roi.runCommand("Open", path + "\\" + roiName);

		try {
			EsferoideDad.showResultsAndSave(saveDir, new ImagePlus(selectedFile.getAbsolutePath()), roi, algoritmClassName);
			deleteTemporalFolder();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
