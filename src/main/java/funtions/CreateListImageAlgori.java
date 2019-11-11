package funtions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CreateListImageAlgori {

	private List<String> algorithms;
	private File imaSelected;
	private String path;
	private File temporalFolder;
	
	
	public CreateListImageAlgori() {

		

	}

	public CreateListImageAlgori(File image) {

		// Seleccionar un path para la carpeta temporal
		path = "  ";
		this.imaSelected = image;
		temporalFolder = new File(path);
		temporalFolder.mkdir();

	}




	
	public void iniA(Class[] listClass) {
		
		for (Class class1 : listClass) {
		System.out.println(class1.getName()+"    fgfdghdfjkghdfjkghfkjdghfkjghfkjghfjkghfjkghfjk  ");
			
			Method[] metClass=class1.getDeclaredMethods();
			
			
			for (Method method : metClass) {
				System.out.println(method.getName());
				
				// se realiza el metodo, se guarda la imagen con el nombre del la clase_metodo y me muestra por pantalla
			}
		}
		
	}
	
	public static Class[] getClasses(String pckgname) throws ClassNotFoundException {
	    ArrayList classes=new ArrayList();
	    File directory = null;
	    try {
	        directory = new File(Thread.currentThread().getContextClassLoader().getResource(pckgname.replace('.', '/')).getFile());
	    } catch(NullPointerException x) {
	        throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
	    }   
	    if (directory.exists()) {
	        // Get the list of the files contained in the package
	        String[] files = directory.list();
	        for (int i = 0; i < files.length; i++) {
	            // we are only interested in .class files
	            if(files[i].endsWith(".class")) {
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

		// se elige como

		return ima;

	}

	// guardar la imagen del algoritmo no solo el tiff tambien el nd2 y el zip
	// generados
	public void saveSelectedImage(File selectedFile, String saveDir) {
		this.imaSelected = selectedFile;
		// se bucan los archivos nd2 y zip con el mismo nombre dentro de la carpeta
		// temporal

		// se coge el nombre del archivo creado y se le quita el nombre del algoritmo
		// utilizado

		// Se sobre escriben los archivos de la carpeta general con el mismo nombre

		deleteTemporalFolder();

	}

	// si se sale de la app o para borrar la carpeta tras seleccionar una imagen
	public void deleteTemporalFolder() {

		if (temporalFolder.delete())
			System.out.println(temporalFolder + " ha sido borrado correctamente");
		else
			System.out.println(temporalFolder + " no se ha podido borrar");
	}

}
