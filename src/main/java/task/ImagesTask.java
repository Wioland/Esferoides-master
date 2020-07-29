package task;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import funtions.FileFuntions;
import funtions.Utils;
import interfaces.TabPanel;

/**
 * Thread for checking the condition of the images of the directory
 * 
 * @author Yolanda
 *
 */
public class ImagesTask extends TimerTask {

	static private final Logger LOGGER = Logger.getLogger("task.ImagesTask");
	private Integer counter = 0;
	private TabPanel tp;
	private String dir;
	private String currentDir;

	public ImagesTask(TabPanel tabpane) {
		this.tp = tabpane;
		this.dir = tabpane.getDir();
	}

	@Override
	public void run() {

		currentDir = Utils.getCurrentDirectory();
		// If the tabPanel was delete or change to another one we kill the task
		if (tp.getDir() != currentDir) {
			this.cancel();

		} else {
			// For showing in the console how many times the task has been performed
			LOGGER.log(Level.INFO, "ImageTask    Numero de ejecución " + counter);
			counter++;

			// Checks if the images of the prediction folder has changed

			FileFuntions.isDirectoryContentModify(dir, tp);

		}
	}

}
