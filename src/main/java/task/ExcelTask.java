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
		// TODO Auto-generated method stub
		LOGGER.log(Level.INFO, "Numero de ejecución " + counter + " el directorio es "+ dir + "     tp dir "+tp.getDir());
		counter++;

		ExcelActions.checkAllExcelTab(tp, dir);

		if (tp.getParent().getParent() == null) {
			this.cancel();

		}
		
	}

}
