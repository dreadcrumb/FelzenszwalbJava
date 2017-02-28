package segmentation_plugins;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.util.IjLogStream;
//import imagingbook.lib.util.IjLogStream;
import segmentation.felzenszwalb.Segmenter;

/**
 * Java implementation of the image segmentation introduced by Felzenszwalb and
 * Huttenlocher in 2004. For more information about the algorithm, visit
 * http://cs.brown.edu/~pff/segment/
 * 
 * @author Leander Suda, FH Hagenberg, Interactive Media
 *
 */
public class Segmentation_Felzenszwalb implements PlugInFilter {

	// this causes more problems than it provides benefits
	static {
		IjLogStream.redirectSystem(); // enable System.out.printkn etc.
	}

	private ImagePlus imp = null;

	@Override
	public int setup(String arg, ImagePlus imp) {
		memoryCheck();
		this.imp = imp;
		return DOES_RGB + NO_CHANGES;
	}

	@Override
	public void run(ImageProcessor ip) {

		double start = System.currentTimeMillis();

		// Default Constructor
		Segmenter seg = new Segmenter();
		ImageProcessor output = seg.segmentImage(ip);

		new ImagePlus(imp.getShortTitle() + "-segmented", output).show();

		double end = System.currentTimeMillis();
		System.out.println("All done, took " + (end - start));

	}

	// remove later
	void memoryCheck() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long maxMemory = runtime.maxMemory();
		long freeMemory = runtime.freeMemory();

		System.out.format("allocated memory: %10d kBytes\n", totalMemory / 1024);
		System.out.format("free memory:      %10d kBytes\n", freeMemory / 1024);
		System.out.format("max. memory:      %10d kBytes\n", maxMemory / 1024);
		System.out.format("tot. free memory: %10d kBytes\n", (freeMemory + (maxMemory - totalMemory)) / 1024);
		System.out.println("Java runtime: " + System.getProperty("sun.arch.data.model") + " Bit");
	}

}
