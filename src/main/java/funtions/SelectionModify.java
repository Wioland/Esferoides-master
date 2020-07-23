package funtions;

import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.Undo;
import ij.gui.EllipseRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.frame.LineWidthAdjuster;
import ij.process.FloatPolygon;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class SelectionModify {
	private ImagePlus imp;
	private float[] kernel = { 1f, 1f, 1f, 1f, 1f };

	public SelectionModify(ImagePlus imp) {
		this.imp = imp;
	}

	public Roi fitSpline() {
		Roi roi = imp.getRoi();
		if (roi == null) {
			noRoi("Spline");
			return roi;
		}
		int type = roi.getType();
		boolean segmentedSelection = type == Roi.POLYGON || type == Roi.POLYLINE;
		if (!(segmentedSelection || type == Roi.FREEROI || type == Roi.TRACED_ROI || type == Roi.FREELINE)) {
			IJ.error("Spline Fit", "Polygon or polyline selection required");
			return roi;
		}
		if (roi instanceof EllipseRoi)
			return roi;
		PolygonRoi p = (PolygonRoi) roi;
		Undo.setup(Undo.ROI, imp);
		if (!segmentedSelection && p.getNCoordinates() > 3) {
			if (p.subPixelResolution())
				p = trimFloatPolygon(p, p.getUncalibratedLength());
			else
				p = trimPolygon(p, p.getUncalibratedLength());
		}
		LineWidthAdjuster.update();
		return p;
	}

	PolygonRoi trimPolygon(PolygonRoi roi, double length) {
		int[] x = roi.getXCoordinates();
		int[] y = roi.getYCoordinates();
		int n = roi.getNCoordinates();
		x = smooth(x, n);
		y = smooth(y, n);
		float[] curvature = getCurvature(x, y, n);
		Rectangle r = roi.getBounds();
		double threshold = rodbard(length);
		double distance = Math.sqrt((x[1] - x[0]) * (x[1] - x[0]) + (y[1] - y[0]) * (y[1] - y[0]));
		x[0] += r.x;
		y[0] += r.y;
		int i2 = 1;
		int x1, y1, x2 = 0, y2 = 0;
		for (int i = 1; i < n - 1; i++) {
			x1 = x[i];
			y1 = y[i];
			x2 = x[i + 1];
			y2 = y[i + 1];
			distance += Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) + 1;
			distance += curvature[i] * 2;
			if (distance >= threshold) {
				x[i2] = x2 + r.x;
				y[i2] = y2 + r.y;
				i2++;
				distance = 0.0;
			}
		}
		int type = roi.getType() == Roi.FREELINE ? Roi.POLYLINE : Roi.POLYGON;
		if (type == Roi.POLYLINE && distance > 0.0) {
			x[i2] = x2 + r.x;
			y[i2] = y2 + r.y;
			i2++;
		}
		PolygonRoi p = new PolygonRoi(x, y, i2, type);
		if (roi.getStroke() != null)
			p.setStrokeWidth(roi.getStrokeWidth());
		p.setStrokeColor(roi.getStrokeColor());
		p.setName(roi.getName());
		imp.setRoi(p);
		return p;
	}

	double rodbard(double x) {
		double ex;
		if (x == 0.0)
			ex = 5.0;
		else
			ex = Math.exp(Math.log(x / 700.0) * 0.88);
		double y = 3.9 - 44.0;
		y = y / (1.0 + ex);
		return y + 44.0;
	}

	int[] smooth(int[] a, int n) {
		FloatProcessor fp = new FloatProcessor(n, 1);
		for (int i = 0; i < n; i++)
			fp.putPixelValue(i, 0, a[i]);
		GaussianBlur gb = new GaussianBlur();
		gb.blur1Direction(fp, 2.0, 0.01, true, 0);
		for (int i = 0; i < n; i++)
			a[i] = (int) Math.round(fp.getPixelValue(i, 0));
		return a;
	}

	float[] getCurvature(int[] x, int[] y, int n) {
		float[] x2 = new float[n];
		float[] y2 = new float[n];
		for (int i = 0; i < n; i++) {
			x2[i] = x[i];
			y2[i] = y[i];
		}
		ImageProcessor ipx = new FloatProcessor(n, 1, x2, null);
		ImageProcessor ipy = new FloatProcessor(n, 1, y2, null);
		ipx.convolve(kernel, kernel.length, 1);
		ipy.convolve(kernel, kernel.length, 1);
		float[] indexes = new float[n];
		float[] curvature = new float[n];
		for (int i = 0; i < n; i++) {
			indexes[i] = i;
			curvature[i] = (float) Math.sqrt((x2[i] - x[i]) * (x2[i] - x[i]) + (y2[i] - y[i]) * (y2[i] - y[i]));
		}
		return curvature;
	}

	PolygonRoi trimFloatPolygon(PolygonRoi roi, double length) {
		FloatPolygon poly = roi.getFloatPolygon();
		float[] x = poly.xpoints;
		float[] y = poly.ypoints;
		int n = poly.npoints;
		x = smooth(x, n);
		y = smooth(y, n);
		float[] curvature = getCurvature(x, y, n);
		double threshold = rodbard(length);
		double distance = Math.sqrt((x[1] - x[0]) * (x[1] - x[0]) + (y[1] - y[0]) * (y[1] - y[0]));
		int i2 = 1;
		double x1, y1, x2 = 0, y2 = 0;
		for (int i = 1; i < n - 1; i++) {
			x1 = x[i];
			y1 = y[i];
			x2 = x[i + 1];
			y2 = y[i + 1];
			distance += Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) + 1;
			distance += curvature[i] * 2;
			if (distance >= threshold) {
				x[i2] = (float) x2;
				y[i2] = (float) y2;
				i2++;
				distance = 0.0;
			}
		}
		int type = roi.getType() == Roi.FREELINE ? Roi.POLYLINE : Roi.POLYGON;
		if (type == Roi.POLYLINE && distance > 0.0) {
			x[i2] = (float) x2;
			y[i2] = (float) y2;
			i2++;
		}
		PolygonRoi p = new PolygonRoi(x, y, i2, type);
		if (roi.getStroke() != null)
			p.setStrokeWidth(roi.getStrokeWidth());
		p.setStrokeColor(roi.getStrokeColor());
		p.setDrawOffset(roi.getDrawOffset());
		p.setName(roi.getName());
		imp.setRoi(p);
		return p;
	}

	float[] smooth(float[] a, int n) {
		FloatProcessor fp = new FloatProcessor(n, 1);
		for (int i = 0; i < n; i++)
			fp.setf(i, 0, a[i]);
		GaussianBlur gb = new GaussianBlur();
		gb.blur1Direction(fp, 2.0, 0.01, true, 0);
		for (int i = 0; i < n; i++)
			a[i] = fp.getf(i, 0);
		return a;
	}

	float[] getCurvature(float[] x, float[] y, int n) {
		float[] x2 = new float[n];
		float[] y2 = new float[n];
		for (int i = 0; i < n; i++) {
			x2[i] = x[i];
			y2[i] = y[i];
		}
		ImageProcessor ipx = new FloatProcessor(n, 1, x, null);
		ImageProcessor ipy = new FloatProcessor(n, 1, y, null);
		ipx.convolve(kernel, kernel.length, 1);
		ipy.convolve(kernel, kernel.length, 1);
		float[] indexes = new float[n];
		float[] curvature = new float[n];
		for (int i = 0; i < n; i++) {
			indexes[i] = i;
			curvature[i] = (float) Math.sqrt((x2[i] - x[i]) * (x2[i] - x[i]) + (y2[i] - y[i]) * (y2[i] - y[i]));
		}
		return curvature;
	}

	void noRoi(String command) {
		IJ.error(command, "This command requires a selection");
	}

}
