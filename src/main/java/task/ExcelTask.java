package task;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import funtions.ExcelActions;
import interfaces.TabPanel;

public class ExcelTask extends TimerTask {

	static private final Logger LOGGER = Logger.getLogger("task.ExcelTask");
	private Integer counter;
	private TabPanel tp;
	private String dir;

	public ExcelTask(TabPanel tabpane, String directory) {
		counter = 0;
		this.tp = tabpane;
		this.dir = directory;

	}

	@Override
	public void run() {
		// For showing in the console how many times the task has been performed
		LOGGER.log(Level.INFO,"Numero de ejecución " + counter + " el directorio es " + dir + "     tp dir " + tp.getDir());
		counter++;

		// Checks if the content of the excels in the tabs has change
		ExcelActions.checkAllExcelTab(tp, dir);

		// If the tabPanel was delete or change to another one we kill the task
		if (tp.getParent().getParent() == null) {
			this.cancel();

		}

	}

}
