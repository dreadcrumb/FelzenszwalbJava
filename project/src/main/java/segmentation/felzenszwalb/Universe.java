package segmentation.felzenszwalb;

/**
 * Universe represents a disjoint-set forests using union-by-rank and path
 * compression. The elements inside reference to different regions
 * 
 * @author Leander
 *
 */
public class Universe {

	private int num;
	private final UniElement[] elts;

	public Universe(int elements) {
		elts = new UniElement[elements];
		num = elements;
		for (int i = 0; i < elements; i++) {
			elts[i] = new UniElement(0, i, 1);
		}
	}

	/**
	 * Finds a region in the array of elements
	 * 
	 * @param x
	 *            Reference to an edge
	 * @return The reference to a region
	 */
	public int find(int x) {
		int y = x;
		while (y != elts[y].getP()) {
			y = elts[y].getP();
		}
		elts[x].setP(y); // WB: find() has a side effect? Why?
		return y;
	}

	public int getRegionId(int elementCounter) {
		return elts[elementCounter].getP();
	}

	/**
	 * Joins two separate regions of the graph
	 * 
	 * @param x
	 *            reference to region 1
	 * @param y
	 *            reference to region 2
	 */
	public void join(int x, int y) {
		if (elts[x].getRank() > elts[y].getRank()) {
			elts[y].setP(x);
			elts[x].setSize(elts[x].getSize() + elts[y].getSize());
		} else {
			elts[x].setP(y);
			elts[y].setSize(elts[y].getSize() + elts[x].getSize());
			if (elts[x].getRank() == elts[y].getRank())
				elts[y].setRank(elts[y].getRank() + 1);
		}
		num--;
	}

	public int getSize(int x) {
		return elts[x].getSize();
	}

	public int getNumSets() {
		return num;
	}

	public void postProcess(int minSize, Edge[] edges) {
		for (int i = 0; i < edges.length; i++) {
			int a = this.find(edges[i].getA());
			int b = this.find(edges[i].getB());
			if ((a != b) && ((this.getSize(a) < minSize) || (this.getSize(b) < minSize))) {
				this.join(a, b);
			}
		}
	}

	public void joinRegions(int num_edges, Edge[] edges, double k_Value, int num_vertices) {

		// init thresholds
		float[] threshold = new float[num_vertices];
		for (int i = 0; i < num_vertices; i++)
			threshold[i] = (float) k_Value;

		// for each edge, in non-decreasing weight order...
		for (int i = 0; i < num_edges; i++) {
			Edge pedge = edges[i];

			// components connected by this edge
			int a = this.find(pedge.getA());
			int b = this.find(pedge.getB());
			if (a != b) {
				if ((pedge.getW() <= threshold[a]) && (pedge.getW() <= threshold[b])) {
					this.join(a, b);
					a = this.find(a);
					threshold[a] = (float) (pedge.getW() + k_Value / this.getSize(a));
				}
			}
		}

	}
}
