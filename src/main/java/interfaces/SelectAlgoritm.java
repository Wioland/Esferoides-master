package interfaces;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import funtions.CreateListImageAlgori;
import funtions.Main;

public class SelectAlgoritm {

	private Object selection;
	private Object[] options;
	private String dir;

	public SelectAlgoritm(String directory) {
		
		this.dir=directory;
		
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

		if (selection != null) {
			i = findIndexOfSelection();

			selectedMethod = methods.get(i);

			// segun el algoritmo seleccionado llamar a su metodo y crear las imagenes,
			// luego mostrar en la carpeta por medio de view images

			selectedMethod.invoke(obj, args);
			
			GeneralView gb= new GeneralView(this.dir);

		}else {
			Main.callProgram(dir);
		}

	}

	private int findIndexOfSelection() {
		boolean find = false;
		int i = 0;

		while (!find || i < options.length) {
			if (options[i].equals(selection)) {
				find = true;
				break;
			}
			i++;
		}

		return i;

	}

}
