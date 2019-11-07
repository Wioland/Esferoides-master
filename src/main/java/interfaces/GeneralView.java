
package interfaces;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;

import funtions.ExcelActions;
import funtions.Utils;
import ij.io.DirectoryChooser;
import ij.measure.ResultsTable;

public class GeneralView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String directory;
	

	public GeneralView(String directory) {

		this.directory = directory;
		
		// Parametros ventana
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
		setTitle("Results");
		setMinimumSize(new Dimension(1000,700));
		
		
		ImageTreePanel imageTree = new ImageTreePanel(directory);
		getContentPane().add(imageTree);
		
		setVisible(true);
	}
	
	
	
	
	



	// PRUEBAS

	public static void main(String[] args) {

		DirectoryChooser dc = new DirectoryChooser("Select the folder containing the nd2 images");

		GeneralView ventana = new GeneralView(dc.getDirectory());

	}
}
