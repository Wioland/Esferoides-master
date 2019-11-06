package funtions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateListImageAlgori {

	private List<String> algorithms;
	private File imaSelected;
	private String path;
	private File temporalFolder;

	public CreateListImageAlgori(File image) {
		
		//Seleccionar un path para la carpeta temporal 
		path="  ";
		this.imaSelected = image;
		temporalFolder = new File(path);
		temporalFolder.mkdir();
		iniAlgorithList();
		
	}

	private void iniAlgorithList() {
		
		
	}

	// crear las imagenes con los distintos algoritmos de la imagen seleccionada
	// guardarlas en la carpeta y en la lista
	public List<File> createImagesAlgorithms() {
		List<File> ima = new ArrayList<File>();
		
		//se llama a los algoritmos de la lista de algoritmos y se aplican estos sobre la imagen seleccionada
		
		// se elige como

		return ima;

	}

	// guardar la imagen del algoritmo no solo el tiff tambien el nd2 y el zip generados
	public void saveSelectedImage(File selectedFile, String saveDir) {
		this.imaSelected = selectedFile;
		// se bucan los archivos nd2 y zip con el mismo nombre dentro de la carpeta temporal
		
		//se coge el nombre del archivo creado y se le quita el nombre del algoritmo utilizado
		
		//Se sobre escriben los archivos de la carpeta general con el mismo nombre
		
		deleteTemporalFolder();

	}

	// si se sale de la app o para borrar la carpeta tras seleccionar una imagen
	public void deleteTemporalFolder() {

		if (temporalFolder.delete())
			System.out.println( temporalFolder + " ha sido borrado correctamente");
		else
			System.out.println(temporalFolder + " no se ha podido borrar");
	}

}
