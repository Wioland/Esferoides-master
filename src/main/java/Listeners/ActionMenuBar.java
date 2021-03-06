package Listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import funtions.FileFuntions;
import funtions.Utils;

/**
 * Actions to be perform in the main JFrame
 * 
 * @author Yolanda
 *
 */
public class ActionMenuBar implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = "";

		if (e.getSource().getClass().equals(JMenuItem.class)) {
			JMenuItem menuItem = (JMenuItem) e.getSource();
			name = menuItem.getName();
		} else {
			JButton menuItem = (JButton) e.getSource();
			name = menuItem.getName();
		}

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
			Utils.mainFrame.DetectAlgoDirectory();
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
		case "UserManual":
			System.out.println("UserManual");
			FileFuntions.openUserManual();
			break;
		case "About":
			System.out.println("About");
			FileFuntions.openAboutSection();
			break;

		default:
			break;
		}

	}

}
