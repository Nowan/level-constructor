import java.awt.Dimension;
import java.util.ArrayList;

public class Level {

	private ArrayList<GameObject> gameObjects;
	
	private int width;
	
	private int height;
	
	public Level(int width, int height){
		gameObjects = new ArrayList<GameObject>();
		setSize(width,height);
	}
	
	public ArrayList<GameObject> getObjects(){
		return gameObjects;
	}
	
	public Dimension getSize(){
		return new Dimension(width,height);
	}
	
	public void setSize(int width, int height){
		this.width=width;
		this.height=height;
	}
	
	public void setSize(Dimension size){
		this.width=size.width;
		this.height=size.height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
}
