import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Wolf extends Animal implements Entity {
	
	/**
	 * Constructor
	 * Input: 	Pasture object
	 * 			int delay between moves
	 * 			int time object can survive without food
	 * 			int delay between childs
	 * 			int how far object can see
	 */
	public Wolf(Pasture pasture, int moveDelay, int timeWithoutFood, int breedDelay, int viewDistance) {
		super(pasture, moveDelay, breedDelay, timeWithoutFood, viewDistance, "wolf.gif");
	}
	
	/**
     * Update object
     */
	public void tick() {
		if(this.pasture.getPosition(this) == null)
			return;

        if(this.moveCounter-- == 0) {
			move();
			
			//if catched a sheep
			Entity sheep = findAt(this.pasture.getPosition(this), Sheep.class);
			if(sheep != null)
				eat(sheep);
			
            this.moveCounter = this.moveDelay;
        }
        
        if(this.starveCounter-- == 0)
			this.pasture.removeEntity(this);
        
        if(this.breedCounter-- <= 0 && this.hasEaten) {
			breed();
			this.breedCounter = this.breedDelay;
		}
    }
    
    /**
     * Move to a new position based on environment
     */
    private void move() {
		List<Entity> surroundingEntitys = entitysInView(this);
		List<Point> neighbours = this.pasture.getFreeNeighbours(this);
		
		Map<Point, Double> potentialPositions = new HashMap<Point, Double>();
		
		double score;
		
		for(int i = 0; i < neighbours.size(); i++) {
			score = evaluatePosition(surroundingEntitys, neighbours.get(i));
			potentialPositions.put(neighbours.get(i), score);
		}
		
		performMove(potentialPositions, this);
	}
	
	/**
	 * Evaluate a potential position based on environment
	 * Input:	List of Entity objects in view
	 * 			Point position to evaluate
	 * Return:	double representation of position
	 */
	private double evaluatePosition(List<Entity> surroundingEntitys, Point positionToEval) {
		double closestSheep = distanceToClosest(surroundingEntitys, Sheep.class, positionToEval);
		double distanceOldPos = distanceOldPosition(positionToEval);
		return 0.01 * distanceOldPos - closestSheep;
	}
    
    /**
	 * Test if this object can be on the same position as another object
	 * Input:	Entety object
	 * Return:	boolean false if the two objects cant share position
	 * 			true if they can
	 */
    public boolean isCompatible(Entity other) {
        if(other instanceof Wolf || other instanceof Fence) {
			return false;
        }
        return true;
    }
    
    /**
     * create a new Wolf object if theres a free neighbouring Point for the child to occupy
     */
    private void breed() {
		Point neighbour = getRandomMember(this.pasture.getFreeNeighbours(this));

		if(neighbour != null) {
			this.pasture.addEntity(new Sheep(this.pasture, this.moveDelay, this.breedDelay, this.starveAt, this.viewDistance), neighbour);
		}
	}
}
