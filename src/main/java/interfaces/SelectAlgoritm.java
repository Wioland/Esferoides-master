package interfaces;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.CreateListImageAlgori;

public class SelectAlgoritm {

	private Object selection;
	private Object[] options;

	public SelectAlgoritm() {
		CreateListImageAlgori cLiA = new CreateListImageAlgori();
		List<Method> methods = cLiA.getAlgorithms();
		options = new Object[methods.size()];
		int i = 0;
		String[] splitnameMethod;
		String s = "";
		Method selectedMethod;

		for (Method m : methods) {
			// System.out.println(m.toString());
			splitnameMethod = m.toString().split("\\.");
			s = splitnameMethod[1] + "  -->  " + m.getName();
			options[i] = s;
			i++;
		}

		// Con JCombobox

		selection = JOptionPane.showInputDialog(null, "Select an option", "Algorithm selecter",
				JOptionPane.QUESTION_MESSAGE, null, options, methods.get(0));

	
		 i=findIndexOfSelection();
		
		System.out.println(" la posicion del elemento dentro de la lista es "+ i
				+ " la seleccion es " + selection.toString() );
		// selectedMethod=methods.get(index);

		// segun el algoritmo seleccionado llamar a su metodo y crear las imagenes,
		// luego mostrar en la carpeta por medio de view images

	}

	private int findIndexOfSelection() {
		boolean find=false;
		int i=0;
		
		while(!find || i<options.length) {
			if(options[i].equals(selection)) {
				find=true;
				break;
			}
			i++;
		}
		
		return i;
		
	}

}
