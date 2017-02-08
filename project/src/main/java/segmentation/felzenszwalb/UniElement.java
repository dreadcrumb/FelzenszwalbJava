package segmentation.felzenszwalb;

/**
 * Represents an element in the disjoint-set forest, with the label, rank and
 * size of the Element
 * 
 * @author Leander
 *
 */
public class UniElement {
	private int rank, p, size;
	
	// WB: added
	UniElement(int rank, int p, int size) {
		this.rank = rank;
		this.p = p;
		this.size = size;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
