package esferoides;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.frame.RoiManager;

/**
 *
 * 
 * @author Jonathan
 * @see <a href = "https://github.com/joheras/SpheroidJ" > This code has changes
 *      compared the original in this Github repository </a>
 *
 */
public class AnalyseParticleMethods {

	public static RoiManager analyzeParticlesFluo(ImagePlus imp2) {

		IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity circularity=0.0-2.00 exclude add");

		imp2.changes = false;

		RoiManager rm = RoiManager.getInstance();

		if (rm != null) {
			rm.setVisible(false);
		}

		return rm;
	}

	public synchronized static RoiManager analyzeParticlesHector(ImagePlus imp2) {

		IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity  circularity=0.15-2.00 exclude add");
		imp2.changes = false;

		RoiManager rm = RoiManager.getInstance();

		if (rm != null) {
			rm.setVisible(false);
		}

		return rm;
	}

	public static RoiManager analyseParticlesTeodora(ImagePlus imp2, boolean blackHole, boolean exclude) {

		if (blackHole) {
			IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity circularity=0.5-1.00 exclude add");
		} else {
			if (exclude) {
				IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity circularity=0.15-1.00 exclude add");
			} else {
				IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity circularity=0.00-1.00  add");
			}
		}

		RoiManager rm = RoiManager.getInstance();
		if (rm != null) {
			rm.setVisible(false);

		}
		return rm;

	}

}
