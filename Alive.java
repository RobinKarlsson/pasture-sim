import javax.swing.ImageIcon;
import java.util.List;

public class Alive {
	protected final ImageIcon image;
	protected final int breedDelay;
	protected int breedCounter;
	protected final Pasture pasture;

	/**
	 * Constructor
	 * Input: 	Pasture object
	 * 			int delay between childs
	 * 			String url to objects image
	 */
	public Alive(Pasture pasture, int breedDelay, String imagePath) {
		this.pasture = pasture;
		this.breedDelay = breedDelay;
		this.breedCounter = breedDelay;
		this.image = new ImageIcon(imagePath);
	}
	
	/**
	 * Select a random member from a list
	 * Input: 	List
	 * Return:	random element of list
	 */
	protected static <X> X getRandomMember(List<X> c) {
        if (c.size() == 0)
            return null;
        
        int n = (int)(Math.random() * c.size());
        
        return c.get(n);
    }
    
    /**
	 * Return objects ImageIcon
	 * Return:	ImageIcon
	 */
    public ImageIcon getImage() {
		return this.image;
	}
}
