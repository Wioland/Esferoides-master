package Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import funtions.FileFuntions;
import funtions.Utils;

public class ActionMenuBar implements ActionListener {

	public ActionMenuBar() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JMenuItem menuItem = (JMenuItem) e.getSource();
		String name = menuItem.getName();

		switch (name) {
		case "Open Dir":
			System.out.println("Abrir directotio");
			FileFuntions.changeDirOrNot();
			break;
		case "Close Dir":
			System.out.println("cerrar directotio");
			Utils.mainFrame.backinitialEstate();
			break;
		case "Close":
			System.out.println("cerrar app");
			Utils.mainFrame.dispose();
			break;
		case "Detect in directory":
			System.out.println("detect dir");
			Utils.mainFrame.paintMainFRameDetectAlgo();
			break;
		case "Detect in image":
			System.out.println("detect ima");
			FileFuntions.detectAlgoImageMEnu();
			break;
		case "Change detection algorithm":
			System.out.println("ver algo");
			Utils.changeUsedAlgoritms();
			break;
		case "Update":
			System.out.println("Update");
			FileFuntions.createUpdater(false);
			break;

		default:
			break;
		}

	}

}
