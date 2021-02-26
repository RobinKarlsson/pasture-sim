import javax.swing.ImageIcon;

public class Fence implements Entity {
	protected final Pasture pasture;
	protected final ImageIcon image;
	
	/**
	 * Constructor
	 * Input: 	Pasture object
	 */
	public Fence(Pasture pasture) {
		this.pasture = pasture;
		
		this.image = new ImageIcon("fence.gif");
	}
	
	/**
	 * Test if this object can be on the same position as another object
	 * Input:	Entety object
	 * Return:	boolean false if the two objects cant share position
	 * 			true if they can
	 */
	public boolean isCompatible(Entity other) {
		return false;
	}
	
	/**
	 * Return objects ImageIcon
	 * Return:	ImageIcon
	 */
	public ImageIcon getImage() {
		return this.image;
	}
	
	public void tick() {
	}
}
