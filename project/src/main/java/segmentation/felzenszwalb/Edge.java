package segmentation.felzenszwalb;

/**
 * Represents an edge connecting two nodes in a graph. The weight describes the
 * difference of said nodes, a high weight means a high difference. a and b
 * references to the nodes connected by the edge.
 * Implements Comparable so the edges can be sorted.
 * 
 * @author Leander
 *
 */
public class Edge implements Comparable<Edge> {
	
	final private float w;
	final private int a, b;
	
	public Edge(int w, int a, int b) {
		this.a = a;
		this.b = b;
		this.w = w;
	}

	public float getW() {
		return w;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	@Override
	public int compareTo(Edge o) {
		if (this.getW() < o.getW())
			return -1;
		else if (this.getW() > o.getW())
			return 1;
		return 0;
	}

}
