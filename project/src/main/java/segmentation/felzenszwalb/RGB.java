package segmentation.felzenszwalb;

import java.awt.Color;

/**
 * Helper class to generate random colors that are later assigned to pixels in
 * the return image
 * TODO: This class is not needed at all, use awt.Color instead! (WB)
 * 
 * @author Leander
 *
 */
public class RGB {
	private int r, g, b;

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public RGB randomRGB() {
		RGB c = new RGB();

		c.setR((int) (Math.random() * 255));
		c.setG((int) (Math.random() * 255));
		c.setB((int) (Math.random() * 255));

		return c;
	}

	public int getRGB() {
		Color color = new Color(this.getR(), this.getG(), this.getB(), 255);
		return color.getRGB();
	}
}
