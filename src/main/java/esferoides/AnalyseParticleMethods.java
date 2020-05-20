package esferoides;

import org.python.modules.synchronize;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

public class AnalyseParticleMethods {

	public static RoiManager analyzeParticlesFluo(ImagePlus imp2) {

		IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity circularity=0.0-2.00 exclude add");
		// IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity
		// circularity=0.0-2.00 show=Outlines exclude add");
		imp2.changes = false;
		// ImagePlus imp3 = IJ.getImage();
		// imp2.close();
		// imp3.close();

		RoiManager rm = RoiManager.getInstance();

		if (rm != null) {
			rm.setVisible(false);
		}

		return rm;
	}

	public synchronized static RoiManager analyzeParticlesHector(ImagePlus imp2) {

		// IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity
		// circularity=0.15-2.00 show=Outlines exclude add");
		// imp2.changes = false;
		// ImagePlus imp3 = IJ.getImage();
		// imp2.close();
		// imp3.close();
		//
		// RoiManager rm = RoiManager.getInstance();
		// if (rm != null) {
		// rm.setVisible(false);
		// }
		// return rm;

		// ResultsTable rt= new ResultsTable();
		// RoiManager manager = new RoiManager(true);
		// ParticleAnalyzer.setRoiManager(manager);
		// ParticleAnalyzer pa = new
		// ParticleAnalyzer(ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES, 0,rt, 0.01,
		// Double.POSITIVE_INFINITY, 0.15, 2.00);
		//
		// pa.analyze(imp2);
		// return manager;

		IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity  circularity=0.15-2.00 exclude add");
		imp2.changes = false;
		// RoiManager rm = RoiManager.getInstance();
		// if (rm != null) {
		// rm.setVisible(false);
		// }
		//
		// return RoiManager.getInstance();

		RoiManager rm = RoiManager.getInstance();

		if (rm != null) {
			rm.setVisible(false);
		}

		// Roi[] rois = rm.getRoisAsArray();
		// rm.removeAll();
		//
		// for (Roi r : rois) {
		// r.setImage(imp2);
		// rm.addRoi(r);
		// }

		return rm;
	}

	public synchronized static Roi[] analyzeParticlesHectorRoi(ImagePlus imp2) {

		IJ.run(imp2, "Analyze Particles...", "size=0.01-Infinity  circularity=0.15-2.00 exclude add");
		imp2.changes = false;

		RoiManager rm = RoiManager.getInstance();
		Roi[] rois = null;

		if (rm != null) {
			rm.setVisible(false);
			rois = rm.getRoisAsArray();
//			rm.removeAll();

//			for (Roi r : rois) {
//				r.setImage(imp2);
//			}
		}

		return rois;
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
		// if (blackHole) {
		// IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity
		// circularity=0.5-1.00 show=Outlines exclude add");
		// } else {
		// if (exclude) {
		// IJ.run(imp2, "Analyze Particles...",
		// "size=20000-Infinity circularity=0.15-1.00 show=Outlines exclude
		// add");
		// } else {
		// IJ.run(imp2, "Analyze Particles...", "size=20000-Infinity
		// circularity=0.00-1.00 show=Outlines add");
		// }
		// }
		// ImagePlus imp3 = IJ.getImage();
		// imp2.close();
		// imp3.close();

		RoiManager rm = RoiManager.getInstance();
		if (rm != null) {
			rm.setVisible(false);

		}
		return rm;

	}

}
