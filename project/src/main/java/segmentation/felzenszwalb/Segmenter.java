package segmentation.felzenszwalb;

import java.util.Arrays;
import ij.IJ;
import ij.process.ImageProcessor;

public class Segmenter {

	public static int DefaultMinSegmentSize = 50; // example
	public static double DefaultKValue = 1000;
	public static double DefaultSigma = 0.5;
	public static int DefaultNumLevels = 4;

	private final int minSegmentSize; // minimum size for components
	private final double k_Value; // threshold for calculating the similarity
	private final double sigma; // level of gaussian blur
	private final int numLevels; // number of levels of the region-hierarchy

	public Segmenter() {
		this(DefaultMinSegmentSize, DefaultKValue, DefaultSigma, DefaultNumLevels);
	}

	public Segmenter(int minSegmentSize, double k_Value, double sigma, int numLevels) {
		this.minSegmentSize = minSegmentSize;
		this.k_Value = k_Value;
		this.sigma = sigma;
		this.numLevels = numLevels;
	}

	/**
	 * Builds a graph of all the edges connecting the pixels of the given image,
	 * calculates the difference between the pixels, builds a disjoint-set
	 * universe and creates a new picture with random colors assigned to the
	 * regions found in the process
	 * 
	 * @param ip
	 *            The image to be segmented
	 */
	public ImageProcessor[] segmentImage(ImageProcessor orig) {
		final int width = orig.getWidth();
		final int height = orig.getHeight();

		ImageProcessor ip = orig.duplicate();
		ip.blurGaussian(sigma);

		// for checking and adjusting the blur
		// new ImagePlus("blurred", ip).show();

		IJ.log("Starting to produce graph");
		double start = System.currentTimeMillis();
		// Build graph
		Edge[] edges = getEdgeGraph(ip, width, height);
		int num = edges.length;
		double end = System.currentTimeMillis();
		IJ.log("Graph built, took " + (end - start) + " milliseconds");

		start = System.currentTimeMillis();

		// Segment the Graph
		Universe u = segmentGraph(width * height, num, edges);
		// post process small components
		u.postProcess(minSegmentSize, edges);

		// Hierarchical Graph is TODO made here
		// kind of iteratively post-process with different min-sizes and save
		// steps
		double newKValue;
		ImageProcessor[] output = new ImageProcessor[numLevels];
		for (int i = 0; i < numLevels; i++) {

			// Make new image with colored segments
			output[i] = colorSegments(ip, width, height, u);

			if (i != numLevels - 1) {
				newKValue = k_Value - i * k_Value / numLevels;
				u.joinRegions(num, edges, newKValue, num);
			}

		}

		// ------------------------------------------

		

//		end = System.currentTimeMillis();
//		IJ.log("Segmentation and post-processing done, took " + (end - start)
//				+ "milliseconds (including the sorting time)");
//		IJ.log("Got " + u.getNumSets() + " different segments");

		

		return output;
	}

	private ImageProcessor colorSegments(ImageProcessor ip, int width, int height, Universe u) {
		ImageProcessor output = ip.duplicate();
		int[][] colors = getRandCols(width * height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int comp = u.find(y * width + x);
				output.putPixel(x, y, colors[comp]);
			}
		}

		return output;
	}

	/**
	 * pick random colors for each pixel (This is because there's no registry
	 * for the pixels of a component, but each component has a "pixel-id" which
	 * indicates the location of the first pixel of this component
	 * 
	 * @param num
	 * @return
	 */
	private int[][] getRandCols(int num) {
		int[][] returnInt = new int[num][3];
		for (int i = 0; i < num; i++) {
			for (int j = 0; j < 3; j++) {
				returnInt[i][j] = (int) (Math.random() * 255);
			}
		}

		return returnInt;
	}

	private Edge[] getEdgeGraph(ImageProcessor ip, int width, int height) {

		Edge[] fEdges = new Edge[width * height * 4];
		int[] col1 = new int[3], col2 = new int[3];
		int num = 0;
		IJ.log("starting graph");
		for (int y = 0; y < height; y++) {

			// Debug Info
			IJ.showProgress(y, height);
			// Runtime runtime = Runtime.getRuntime();
			// long totalMemory = runtime.totalMemory();
			// long freeMemory = runtime.freeMemory();
			// System.out.format("y = " + y + ": free memory: %d Bytes\n",
			// freeMemory);

			for (int x = 0; x < width; x++) {
				if (x < width - 1) {
					ip.getPixel(x, y, col1);
					ip.getPixel(x + 1, y, col2);
					fEdges[num] = new Edge(diff(col1, col2), y * width + x, y * width + (x + 1));
					num++;
				}

				if (y < height - 1) {
					ip.getPixel(x, y, col1);
					ip.getPixel(x, y + 1, col2);
					fEdges[num] = new Edge(diff(col1, col2), y * width + x, (y + 1) * width + x);
					num++;
				}

				if ((x < width - 1) && (y < height - 1)) {
					ip.getPixel(x, y, col1);
					ip.getPixel(x + 1, y + 1, col2);
					fEdges[num] = new Edge(diff(col1, col2), y * width + x, (y + 1) * width + (x + 1));
					num++;
				}

				if ((x < width - 1) && (y > 0)) {
					ip.getPixel(x, y, col1);
					ip.getPixel(x + 1, y - 1, col2);
					fEdges[num] = new Edge(diff(col1, col2), y * width + x, (y - 1) * width + (x + 1));
					num++;
				}
				// IJ.log("Edge# " + num);
			}
		}

		// the array fEdges is bigger than the actual number of edges due to
		// border cutting, which causes a
		// Nullpointer-Exception in the sorting procedure
		// Therefore, the array to be sorted has to be capped
		Edge[] edges = new Edge[num];
		for (int i = 0; i < num; i++) {
			if (fEdges[i] != null) {
				edges[i] = fEdges[i];
			}
		}
		return edges;
	}

	/**
	 * Calculates the euclidian distance of the intensity values of each color
	 * channel of the given Colors WB: why was this public?
	 * 
	 * @param col1
	 *            the color values of one pixel
	 * @param col2
	 *            the color values of one pixel
	 * @return the distance/difference of the two colors
	 */
	private int diff(int[] col1, int[] col2) {
		int col1Red = col1[0];
		int col1Green = col1[1];
		int col1Blue = col1[2];

		int col2Red = col2[0];
		int col2Green = col2[1];
		int col2Blue = col2[2];

		int redDiff = (col1Red - col2Red) * (col1Red - col2Red);
		int greenDiff = (col1Green - col2Green) * (col1Green - col2Green);
		int blueDiff = (col1Blue - col2Blue) * (col1Blue - col2Blue);
		return redDiff + greenDiff + blueDiff;
	}

	/**
	 * Segments a graph into related regions and returns a disjoint-set of these
	 * regions
	 * 
	 * @param num_vertices
	 *            The number of nodes/pixels in the image
	 * @param num_edges
	 *            The number of edges in the graph
	 * @param edges
	 *            Array of Edge objects representing the graph
	 * @param k_Value
	 *            The threshold that defines the similarity two nodes have to
	 *            have and approximately the size of the calculated regions
	 * @return A disjoint-set of regions in the graph
	 */
	private Universe segmentGraph(int num_vertices, int num_edges, Edge[] edges) {
		IJ.log("Starting to segment graph");
		IJ.log("Number of vertices: " + num_vertices);
		IJ.log("Number of Edges: " + num_edges + ", length of array " + edges.length);
		IJ.log("k = " + k_Value);

		// sort edges by weight
		double start = System.currentTimeMillis();
		Arrays.sort(edges);
		double end = System.currentTimeMillis();

		IJ.log("Sorted the graph, took " + (end - start) + " milliseconds for " + num_edges + " edges");

		// make a disjoint-set forest
		Universe u = new Universe(num_vertices);

		u.joinRegions(num_edges, edges, k_Value, num_vertices);

		return u;

	}

}
