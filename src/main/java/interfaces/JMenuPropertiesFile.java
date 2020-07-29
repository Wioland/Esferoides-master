package interfaces;

import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * JMenu for the options of the program
 * 
 * @author Yolanda
 *
 */
public class JMenuPropertiesFile extends JMenu  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, JMenuItem> listMenusNotEnable;
	private HashMap<String, JMenuItem> listMenus;

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

	// GETTERS & SETTERS

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

	// METHODS

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

	/**
	 * Adds a new menu item to the menu
	 * 
	 * @param name        name of the item
	 * @param textShow    text to be shown
	 * @param actionListe action to realize on click
	 * @param enable      true enable or false disable
	 */
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

	/**
	 * Changes the status of the menu with the name given set in the
	 * listMenusNotEnable map If they were enable it disable them and the opposite
	 * 
	 * @param nameMEnuItem name of the menu item to disable or enable
	 */
	public void changeEnableNoEnableMEnus(String nameMEnuItem) {
		boolean status = this.listMenusNotEnable.get(nameMEnuItem).isEnabled();
		this.listMenusNotEnable.get(nameMEnuItem).setEnabled(!status);
	}

}
