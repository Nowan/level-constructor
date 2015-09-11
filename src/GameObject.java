import java.awt.Point;
import java.awt.image.BufferedImage;

public class GameObject{

	private Prefab prefab;
	
	private int column;
	
	private int line;
	
	private int index;
	
	//in workspace, after setting the position of current, "master" prefab, user must set of the slave prefab
	private GameObject slaveObject;
			
	//prefab, to which current prefab is linked to
	private GameObject masterObject;
	
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
	
	public int getIndex(){return index;}
	
	public void setIndex(int index){ this.index = index; }

	public GameObject getSlave(){return this.slaveObject;}
	
	public GameObject getMaster(){return this.masterObject;}
	
	public void setMaster(GameObject masterObject){ this.masterObject = masterObject;}
	
	public boolean isRelationStart(){return isMaster()&&!isSlave(); }
	
	public boolean isRelationEnd(){return isSlave()&&!isMaster(); }
	
	public boolean isComplex(){return isMaster()||isSlave();}
	
	public boolean isMaster(){return slaveObject!=null;}
	
	public boolean isSlave(){return masterObject!=null;}
	
	public void setSlave(GameObject gameObject){
		gameObject.setMaster(this);
		this.slaveObject = gameObject;
	}
	
	//returns the first master of relation
	public GameObject getRelationMaster(){
		GameObject gameObject = this;
		while(!gameObject.isRelationStart())
			gameObject = gameObject.getMaster();
		return gameObject;
	}
	
}
