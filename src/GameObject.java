import java.awt.Point;
import java.awt.image.BufferedImage;

public class GameObject{

	private Prefab prefab;
	
	private int column;
	
	private int line;
	
	public GameObject(Prefab prefab, int column, int line){
		this.prefab=prefab;
		this.column=column;
		this.line=line;
	}
	
	public int getTiledWidth(){
		return prefab.getTiledWidth();
	}
	
	public int getTiledHeight(){
		return prefab.getTiledHeight();
	}
	
	public BufferedImage getTexture(){
		return prefab.getTexture();
	}
	
	public Point getPosition(){
		return new Point(column,line);
	}
	
	public Prefab getPrefab(){
		return prefab;
	}
	
	public void setPosition(int column, int line){
		this.column=column;
		this.line=line;
	}
	
	public void setPosition(Point point){
		this.column = point.x;
		this.line = point.y;
	}

}
