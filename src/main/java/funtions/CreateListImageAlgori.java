package funtions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ij.ImageJ;
import loci.plugins.in.ImporterOptions;

public class CreateListImageAlgori {

	private List<Method> algorithms;
	private File imaSelected;
	private String path;
	private static File temporalFolder;

	public CreateListImageAlgori() {
		initAlgo("esferoides.Methods");
	}

	public CreateListImageAlgori(File image) {

		String p = image.getAbsolutePath().replace(image.getName(), "");
		path = p.replace("predictions", "") + "temporal";

		this.imaSelected = image;
		initAlgo("esferoides.DetectEsferoidMethods");

	}



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
	
	
	

	public void initAlgo(String className) {
		algorithms = new ArrayList<Method>();
		Class class1;
		try {
			class1 = Class.forName(className);
			Method[] metClass = class1.getDeclaredMethods();
			for (Method method : metClass) {
				algorithms.add(method);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	// crear las imagenes con los distintos algoritmos de la imagen seleccionada
	// guardarlas en la carpeta y en la lista
	public void createImagesAlgorithms() {

		// se llama a los algoritmos de la lista de algoritmos y se aplican estos sobre
		// la imagen seleccionada

		for (Method m : algorithms) {
			// imagen selected va a tener todo el path de la imagen original a la que ya se
			// le ha aplicado un algoritmo, por loq ue hay que quitarle la ruta y sol
			// quedarme con el nombre del archivo

			// String imagePath,String format
			try {

				path = RoiFuntions.getNd2FilePathFromPredictions(this.imaSelected.getAbsolutePath());

				Methods executeMethods= new Methods(result);

			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
