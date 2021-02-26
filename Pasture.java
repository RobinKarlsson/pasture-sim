import java.util.*;
import java.awt.Point;


/**
 * A pasture contains sheep, wolves, fences, plants, and possibly
 * other entities. These entities move around in the pasture and try
 * to find food, other entities of the same kind and run away from
 * possible enimies. 
 */
public class Pasture {

    private final 	int   width = 35;
    private final 	int   height = 24;

    private final 	int   numberGrass = 40;
    private final 	int   breedDelayGrass = 10;
    
    private final 	int   numberRandomFence = 40;
    
    private final 	int   numberSheep = 20;
    private 		int   moveDelaySheep = 15;
    private final 	int   timeWithoutFoodSheep = 100;
    private final 	int   breedDelaySheep = 101;
    private 		int   viewDistanceSheep = 4;
    
    private final 	int   numberWolf = 10;
    private 		int   moveDelayWolf = 10;
    private final 	int   timeWithoutFoodWolf = 200;
    private final 	int   breedDelayWolf = 201;
    private 		int   viewDistanceWolf = 6;
    

    private final Set<Entity> world = 
        new HashSet<Entity>();
    private final Map<Point, List<Entity>> grid = 
        new HashMap<Point, List<Entity>>();
    private final Map<Entity, Point> point 
        = new HashMap<Entity, Point>();

    private final PastureGUI gui;

    /** 
     * Creates a new instance of this class
     */
    public Pasture() {
        Engine engine = new Engine(this);
        gui = new PastureGUI(width, height, engine);
        setup();
	}
	
	/** 
     * Creates a new instance of this class
     */
	public Pasture(int speedWolf, int speedSheep) {
		moveDelayWolf = speedWolf;
		moveDelaySheep = speedSheep;

        Engine engine = new Engine(this);
        gui = new PastureGUI(width, height, engine);
        setup();
	}
	
	/** 
     * Creates a new instance of this class
     */
	public Pasture(int speedWolf, int speedSheep, int visionWolf, int visionSheep) {
		moveDelayWolf = speedWolf;
		viewDistanceWolf = visionWolf;
		moveDelaySheep = speedSheep;
		viewDistanceSheep = visionSheep;

        Engine engine = new Engine(this);
        gui = new PastureGUI(width, height, engine);
        setup();
	}
	
	/**
	 * Create the initial entetys
	 */
	public void setup() {
        /* The pasture is surrounded by a fence. Replace Dummy for
         * Fence when you have created that class */
        for (int i = 0; i < width; i++) {
            addEntity(new Fence(this), new Point(i,0));
            addEntity(new Fence(this), new Point(i, height - 1));
        }
        for (int i = 1; i < height-1; i++) {
            addEntity(new Fence(this), new Point(0,i));
            addEntity(new Fence(this), new Point(width - 1,i));
        }

        /* 
         * Now insert the right number of different entities in the
         * pasture.
         */
         for (int i = 0; i < numberRandomFence; i++) {
            Entity fence = new Fence(this);
            addEntity(fence, getFreePosition(fence));
		}
		
		for (int i = 0; i < numberGrass; i++) {
            Entity grass = new Grass(this, breedDelayGrass);
            addEntity(grass, getFreePosition(grass));
        }
        
         for (int i = 0; i < numberSheep; i++) {
            Entity sheep = new Sheep(this, moveDelaySheep, timeWithoutFoodSheep, breedDelaySheep, viewDistanceSheep);
            addEntity(sheep, getFreePosition(sheep));
        }
        
        for (int i = 0; i < numberWolf; i++) {
            Entity wolf = new Wolf(this, moveDelayWolf, timeWithoutFoodWolf, breedDelayWolf, viewDistanceWolf);
            addEntity(wolf, getFreePosition(wolf));
        }

        gui.update();
    }

    public void refresh() {
        gui.update();
    }

    /**
     * Returns a random free position in the pasture if there exists
     * one.
     * 
     * If the first random position turns out to be occupied, the rest
     * of the board is searched to find a free position. 
     */
    private Point getFreePosition(Entity toPlace) 
        throws MissingResourceException {
        Point position = new Point((int)(Math.random() * width),
                                   (int)(Math.random() * height)); 

        int p = position.x+position.y*width;
        int m = height * width;
        int q = 97; //any large prime will do

        for (int i = 0; i<m; i++) {
            int j = (p+i*q) % m;
            int x = j % width;
            int y = j / width;

            position = new Point(x,y);
            boolean free = true;

            Collection <Entity> c = getEntitiesAt(position);
            if (c != null) {
                for (Entity thisThing : c) {
                    if(!toPlace.isCompatible(thisThing)) { 
                        free = false; break; 
                    }
                }
            }
            if (free) return position;
        }
        throw new MissingResourceException(
                  "There is no free space"+" left in the pasture",
                  "Pasture", "");
    }
    
            
    public Point getPosition (Entity e) {
        return point.get(e);
    }

    /**
     * Add a new entity to the pasture.
     */
    public void addEntity(Entity entity, Point pos) {

        world.add(entity);

        List<Entity> l = grid.get(pos);
        if (l == null) {
            l = new  ArrayList<Entity>();
            grid.put(pos, l);
        }
        l.add(entity);

        point.put(entity,pos);

        gui.addEntity(entity, pos);
    }
    
    public void moveEntity(Entity e, Point newPos) {
        
        Point oldPos = point.get(e);
        List<Entity> l = grid.get(oldPos);
        if (!l.remove(e)) 
            throw new IllegalStateException("Inconsistent stat in Pasture");
        /* We expect the entity to be at its old position, before we
           move, right? */
                
        l = grid.get(newPos);
        if (l == null) {
            l = new ArrayList<Entity>();
            grid.put(newPos, l);
        }
        l.add(e);

        point.put(e, newPos);

        gui.moveEntity(e, oldPos, newPos);
    }

    /**
     * Remove the specified entity from this pasture.
     */
    public void removeEntity(Entity entity) { 

        Point p = point.get(entity);
        world.remove(entity); 
        grid.get(p).remove(entity);
        point.remove(entity);
        gui.removeEntity(entity, p);

    }

    /**
     * Various methods for getting information about the pasture
     */

    public List<Entity> getEntities() {
        return new ArrayList<Entity>(world);
    }
        
    public Collection<Entity> getEntitiesAt(Point lookAt) {

        Collection<Entity> l = grid.get(lookAt);
        
        if (l==null) {
            return null;
        }
        else {
            return new ArrayList<Entity>(l);
        }
    }


    public List<Point> getFreeNeighbours(Entity entity) {
        List<Point> free = new ArrayList<Point>();
        
        if(getEntityPosition(entity) == null)
			return free;

        int entityX = getEntityPosition(entity).x;
        int entityY = getEntityPosition(entity).y;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Point p = new Point(entityX + x,
                          entityY + y);
                if (freeSpace(p, entity))
                    free.add(p);
            }
        }
        return free;
    }

    private boolean freeSpace(Point p, Entity e) {
                              
        List <Entity> l = grid.get(p);
        if ( l == null  ) return true;
        for (Entity old : l ) 
            if (! old.isCompatible(e)) return false;
        return true;
    }

    public Point getEntityPosition(Entity entity) {
        return point.get(entity);
    }


    /** The method for the JVM to run. */
    public static void main(String[] args) {
		//if an even number of args were entered
		if (args.length > 0 && args.length % 2 == 0) {
			try {
				int speedWolf = Integer.parseInt(args[0]);
				int speedSheep = Integer.parseInt(args[1]);
			
				System.out.println("Simulation started with values:\nmove delay wolf:\t\t" 
							+ speedWolf + "\nmove delay sheep:\t\t" + speedSheep);
								
				if(args.length >= 4) {
					int visionWolf = Integer.parseInt(args[2]);
					int visionSheep = Integer.parseInt(args[3]);
				
					System.out.println("view distance wolf:\t" + visionWolf + 
								"\nview distance sheep:\t" + visionSheep);
								
					new Pasture(speedWolf, speedSheep, visionWolf, visionSheep);
				} else
					new Pasture(speedWolf, speedSheep);
			
			} catch (NumberFormatException e) {
				System.out.println("Arguments must be integers!!");
			}

		} else {
			System.out.println("Simulation started with default values");
			new Pasture();
		}
    }
}
