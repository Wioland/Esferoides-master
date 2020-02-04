package task;

import java.util.TimerTask;

import funtions.FileFuntions;
import interfaces.TabPanel;

public class ImagesTask extends TimerTask {

	// static private final Logger LOGGER = Logger.getLogger("task.ImagesTask");

	private TabPanel tp;
	private String dir;

	public ImagesTask(TabPanel tabpane) {
		this.tp = tabpane;
		this.dir = tabpane.getDir();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		LOGGER.log(Level.INFO, "ImageTask    Numero de ejecución " + counter);
//		counter++;

		FileFuntions.isDirectoryContentModify(dir + "predictions", tp);
		
	}

}
