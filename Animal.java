import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;

public class Animal extends Alive {
	protected final int moveDelay, viewDistance, starveAt;
	protected int moveCounter, starveCounter;
	protected boolean hasEaten;
	protected Point oldPosition = null;
	
	/**
	 * Constructor
	 * Input: 	Pasture object
	 * 			int delay between childs
	 * 			int time object can survive without food
	 * 			int how far object can see
	 * 			String url to objects image
	 */
	public Animal(Pasture pasture, int moveDelay, int breedDelay, int timeWithoutFood, int viewDistance, String imagePath) {
		super(pasture, breedDelay, imagePath);

		this.moveDelay = moveDelay;
		this.moveCounter = moveDelay;
		this.viewDistance = viewDistance;
		this.starveAt = timeWithoutFood;
		this.starveCounter = timeWithoutFood;
		this.hasEaten = false;
	}
    
    /**
     * List of entitys in view of an entity
     * Input:	Entity object thats looking
     * Return:	List of Entity objects in view
     */
    protected List<Entity> entitysInView(Entity lookingEntity) {
		List<Entity> allEntitys = this.pasture.getEntities();
		List<Entity> inView = new ArrayList<Entity>();;
		
		for(int i = 0; i < allEntitys.size(); i++) {
			//if within view
			if(this.pasture.getPosition(lookingEntity).distance(this.pasture.getPosition(allEntitys.get(i))) <= this.viewDistance) {
				inView.add(allEntitys.get(i));
			}
		}
		
		return inView;
	}
	
	/**
	 * Evaluate how far a new position would be from an objects previous position
	 * Input:	Point to evaluate
	 * Return:	double distance between Points
	 */
	protected double distanceOldPosition(Point newPos) {
		if(this.oldPosition != null)
			return newPos.distance(this.oldPosition);
		return 0;
	}
    
    /**
	 * Evaluate distance to the closest Entity of a specific type from a position
	 * Input:	List of Entitys to evaluate
	 * 			class to evaluate for, ie a wolf might be looking for the Sheep class
	 * 			Point position to evaluate
	 * Return:	double distance between position and Entity
	 */
    protected double distanceToClosest(List<Entity> entitys, Class lookFor, Point position) {
		Double closestDistance = null;
		double distanceToEntity;
		
		for(int i = 0; i < entitys.size(); i++) {
			//if found proper class
			if(lookFor.isInstance(entitys.get(i))) {
				distanceToEntity = position.distance(this.pasture.getPosition(entitys.get(i)));
				
				if(closestDistance == null || distanceToEntity < closestDistance) {
					closestDistance = distanceToEntity;
				}
			}
		}
	
	//if nothing was found
	if(closestDistance == null)
		return 0;
	return closestDistance;
	}

	/**
	 * Perform a move to one of several potential positions
	 * Input:	Map of potential points and their evaluated value
	 * 			Entety object to move
	 */
	protected void performMove(Map<Point, Double> potentialPositions, Entity entityToMove) {
		Point moveTo;
		Double bestScore = null;

		//pick best position
		for (Map.Entry<Point, Double> element: potentialPositions.entrySet()) {
			if(bestScore == null || element.getValue() > bestScore) {
				bestScore = element.getValue();
			}
		}
		
		//if theres a posible move
		if(bestScore != null) {
			moveTo = pickNewPosition(potentialPositions, bestScore);
		} else {
			//last resort, try and grab any free neighbour
			moveTo = getRandomMember(this.pasture.getFreeNeighbours(entityToMove));
			if(moveTo == null) {
				//no move possible
				return;
			}
		}
		
		this.oldPosition = this.pasture.getPosition(entityToMove);
		this.pasture.moveEntity(entityToMove, moveTo);
	}
	
	/**
	 * Select key from a map with a specific value. If multiple elements
	 * share value a random element is picked among them
	 * Input:	Map of points and their evaluated value
	 * 			double value to select
	 * Return:	Point object
	 */
	private Point pickNewPosition(Map<Point, Double> potentialPositions, double bestValue) {
		List<Point> candidates = new ArrayList<Point>();
		
		for (Map.Entry<Point, Double> element: potentialPositions.entrySet()) {
			if(element.getValue() == bestValue) {
				candidates.add(element.getKey());
			}
		}
		return getRandomMember(candidates);
	}
	
	/**
	 * See if a specific class, like a Grass object, can be found at a specific location
	 * Input:	Point position to look at
	 * 			class to look for
	 * Return:	Entity object if class was found, null if not
	 */
	protected Entity findAt(Point position, Class lookFor) {
		Collection<Entity> entitys = this.pasture.getEntitiesAt(position);
		
		for(Entity ent: entitys) {
			if(lookFor.isInstance(ent)) {
				return ent;
			}
		}
		return null;
	}
	
	/**
	 * Eat another object, removing it from the simulation
	 * Input:	Entity object to be eaten
	 */
	protected void eat(Entity target) {
		this.pasture.removeEntity(target);
		this.starveCounter = this.starveAt;
		this.hasEaten = true;
	}
}
