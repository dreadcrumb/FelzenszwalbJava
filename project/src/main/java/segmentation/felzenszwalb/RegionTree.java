package segmentation.felzenszwalb;

public class RegionTree {
	
	private TreeElement[] treeElements;
	
	public RegionTree(int numRegions) {
		treeElements = new TreeElement[numRegions];
	}
	
	public void addLevel(Universe u, int level) {
		
		for (int i = 0; i < u.getNumSets(); i++) {
			int regionId = u.getRegionId(i);
			treeElements[i] = new TreeElement(level, regionId);
		}
	}
}
