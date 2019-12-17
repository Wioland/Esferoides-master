package task;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import funtions.FileFuntions;
import interfaces.TabPanel;

public class ImagesTask extends TimerTask {

	static private final Logger LOGGER = Logger.getLogger("task.ImagesTask");
	private Integer counter;
	private TabPanel tp;
	private String dir;


	public ImagesTask(TabPanel tabpane) {
		counter = 0;
		this.tp=tabpane;
		this.dir=tabpane.getDir();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		LOGGER.log(Level.INFO, "ImageTask    Numero de ejecución " + counter);
//		counter++;

		FileFuntions.isDirectoryContentModify(dir,tp);
	}

}
