package Listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import funtions.FileFuntions;
import funtions.Utils;
import interfaces.JPanelComparer;
import interfaces.TabPanel;

public class KeyActionsProgram implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Type");
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		System.out.println("Tecla presionada "+evt.getSource());
		if (Utils.mainFrame.getMb().isEnabled()) {
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_O) {
				System.out.println("Control + O   abrir un directorio o cambiar de directorio");
				FileFuntions.changeDirOrNot();
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_C) {
				System.out.println("Control + C  cerrar el directorio");
				if (Utils.menuItemActive("Close Dir")) {
					Utils.mainFrame.backinitialEstate();
				}
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_U) {
				System.out.println("Control + U  update app");
				FileFuntions.createUpdater(false);
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_W) {
				System.out.println("Control + W  cerrar la aplicacion");
				Utils.mainFrame.dispose();

			}

			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_D) {
				System.out.println("Control + D  detectar en directorio");
				if (Utils.menuItemActive("Detect in directory")) {
					Utils.mainFrame.DetectAlgoDirectory();
				}
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_F) {
				System.out.println("Control + F    detectar en imagen");
				if (Utils.menuItemActive("Detect in image")) {
					FileFuntions.detectAlgoImageMEnu();
				}
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_G) {
				System.out.println("Control + G    ver todos los algoritmos y cambiarlos ");
				Utils.changeUsedAlgoritms();
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_A) {
				System.out.println("Control + A    ver seccion about ");
				FileFuntions.openAboutSection();
			}
			if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_M) {
				System.out.println("Control + M    ver manual ");
				FileFuntions.openUserManual();
			}
		}

		if (Utils.mainFrame.getImageTree() != null) {
			TabPanel tp = Utils.mainFrame.getImageTree().getFolderView();
			if (tp != null) {

				if (tp.getSelectedComponent().getClass().equals(JPanelComparer.class)) {

					switch (evt.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						tp.getViewImagen().getJPComparer().getBackButton().doClick();

						break;

					case KeyEvent.VK_RIGHT:
						tp.getViewImagen().getJPComparer().getForwarButtonButton().doClick();
						break;

					default:
						break;
					}
				}

			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Release");
	}

}
