package interfaces;

import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class JMenuPropertiesFile extends JMenu /* implements ActionListener */ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, JMenuItem> listMenusNotEnable;
	private HashMap<String, JMenuItem> listMenus;

//
//	public JMenuPropertiesFile() {
//
//		this.setText("Options");
//		this.setName("Options");
//
//		addMEnuItem("Current Directory", this);
//		addMEnuItem("Update", this);
//
//	}
	public JMenuPropertiesFile() {

		this.listMenusNotEnable = new HashMap<String, JMenuItem>();
		this.listMenus = new HashMap<String, JMenuItem>();
	}

	/**
	 * creates a jmenu without menuitems with the name given
	 * 
	 * @param name name of the jmenu
	 */
	public JMenuPropertiesFile(String name) {

		this.setText(name);
		this.setName(name);
		this.listMenusNotEnable = new HashMap<String, JMenuItem>();
		this.listMenus = new HashMap<String, JMenuItem>();
	}

	public JMenuPropertiesFile(String name, String textShow) {

		this.setText(textShow);
		this.setName(name);
		this.listMenusNotEnable = new HashMap<String, JMenuItem>();
		this.listMenus = new HashMap<String, JMenuItem>();
	}

	public HashMap<String, JMenuItem> getListMenus() {
		return listMenus;
	}

	public void setListMenus(HashMap<String, JMenuItem> listMenus) {
		this.listMenus = listMenus;
	}

	public HashMap<String, JMenuItem> getListMenusNotEnable() {
		return listMenusNotEnable;
	}

	public void setListMenusNotEnable(HashMap<String, JMenuItem> listMenusNotEnable) {
		this.listMenusNotEnable = listMenusNotEnable;
	}

	/**
	 * Adds a new menu item into the JMenu
	 * 
	 * @param name        name of the menu(id) and the name to show
	 * @param actionListe the action to perform
	 */
	public void addMEnuItem(String name, ActionListener actionListe) {
		JMenuItem mi = new JMenuItem(name);
		mi.setName(name);
		mi.addActionListener(actionListe);
		this.add(mi);
		this.listMenus.put(name, mi);

	}

	public void addMEnuItem(String name, String textShow, ActionListener actionListe, boolean enable) {
		JMenuItem mi = new JMenuItem(textShow);
		mi.setName(name);
		mi.addActionListener(actionListe);
		mi.setEnabled(enable);
		this.add(mi);
		this.listMenus.put(name, mi);
		if (!enable) {
			this.listMenusNotEnable.put(name, mi);
		}

	}

	public void changeEnableNoEnableMEnus(String nameMEnuItem) {
		boolean status = this.listMenusNotEnable.get(nameMEnuItem).isEnabled();
		this.listMenusNotEnable.get(nameMEnuItem).setEnabled(!status);
	}
//	
//	public void changeEnable(String nameMEnuItem) {
//		boolean status=this.listMenus.get(nameMEnuItem).isEnabled();
//		this.listMenus.get(nameMEnuItem).setEnabled(!status);
//	}

//	/**
//	 *Actions to perform when a menu item of the Jmenu is click
//	 */
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		String menuNAme = ((JMenuItem) e.getSource()).getName();
//
//		// Action to change the directory
//		if (menuNAme == "Current Directory") {
//
//			changeDirectory();
//
//		} else {
//			if (menuNAme == "Update") {
//				FileFuntions.createUpdater(false);
//			}
//		}
//	}

}
