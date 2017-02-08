package segmentation.felzenszwalb;

public class TreeElement {
	
	private int level;
	private int regionId;
	private int pixel;
	
	//maybe not needed
	private TreeElement parent;
	private TreeElement[] children;
	
	public TreeElement(int level, int regionId) {
		this.level =level;
		this.regionId = regionId;
		
	}
}
