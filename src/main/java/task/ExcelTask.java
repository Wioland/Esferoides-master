package task;

import java.io.File;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import funtions.ExcelActions;
import funtions.Utils;
import interfaces.TabPanel;

/**
 * Thread for checking the condition of an excel file
 * 
 * @author Yolanda
 *
 */
public class ExcelTask extends TimerTask {

	static private final Logger LOGGER = Logger.getLogger("task.ExcelTask");
	private Integer counter;
	private TabPanel tp;
	private String dir;
	private String currentDir;

	public ExcelTask(TabPanel tabpane, String directory) {
		counter = 0;
		this.tp = tabpane;
		this.dir = directory;

	}

	@Override
	public void run() {

		currentDir = Utils.getCurrentDirectory();

		if ((new File(currentDir).exists())) {
			// If the tabPanel was delete or change to another one we kill the task
			if (tp.getDir() != currentDir) {
				this.cancel();

			} else {
				// For showing in the console how many times the task has been performed
				LOGGER.log(Level.INFO, "Numero de ejecución " + counter + " el directorio es " + dir);
				counter++;

				// Checks if the content of the excels in the tabs has change
				ExcelActions.checkAllExcelTab(tp, dir);

			}
		}else {
			Utils.mainFrame.backinitialEstate();
		}

	}

}
