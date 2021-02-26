import java.awt.Point;

public class Grass extends Alive implements Entity {
	
	/**
	 * Constructor
	 * Input: 	Pasture object
	 * 			int delay between childs
	 */
	public Grass(Pasture pasture, int breedDelay) {
		super(pasture, breedDelay, "plant.gif");
	}
	
	/**
	 * Test if this object can be on the same position as another object
	 * Input:	Entety object
	 * Return:	boolean false if the two objects cant share position
	 * 			true if they can
	 */
	public boolean isCompatible(Entity other) {
        if(other instanceof Animal) {
			return true;
        }
        return false;
    }
    
    /**
     * Update object
     */
    public void tick() {
		if(this.pasture.getPosition(this) == null)
			return;

		if(this.breedCounter-- == 0) {
			breed();
			this.breedCounter = breedDelay;
		}
	}
	
	/**
     * create a new Grass object if theres a free neighbouring Point for the child to occupy
     */
	private void breed() {
		Point neighbour = getRandomMember(this.pasture.getFreeNeighbours(this));

		if(neighbour != null) {
			this.pasture.addEntity(new Grass(this.pasture, this.breedDelay), neighbour);
		}
	}
}
