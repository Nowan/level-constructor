import java.awt.Dimension;
import java.util.ArrayList;

public class Level {

	private GameObjects gameObjects;
	
	private int width;
	
	private int height;
	
	public Level(int width, int height){
		gameObjects = new GameObjects();
		setSize(width,height);
	}
	
	public GameObjects getObjects(){
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
	
	public class GameObjects extends ArrayList<GameObject>{
		
		public GameObjects(){
			super();
		}
		
		@Override 
		public boolean add(GameObject e){
			boolean returnBit = super.add(e);
			//setting object indexes in canvas.indexMap
			int [][] indexMap = ConstructorWindow.instance.workspace.canvas.indexMap;
			for(int c=0;c<e.getTiledWidth();c++)
				for(int l=0;l<e.getTiledHeight();l++)
					indexMap[e.getPosition().x+c][e.getPosition().y+l]=this.size()-1;
			return returnBit;
		}
		
		@Override 
		public GameObject remove(int index){
			GameObject returnObject = super.remove(index);
			//setting object indexes in canvas.indexMap
			int [][] indexMap = ConstructorWindow.instance.workspace.canvas.indexMap;
			int levelWidth = ConstructorWindow.instance.globals.level.getWidth();
			int levelHeight = ConstructorWindow.instance.globals.level.getHeight();
			//clearing indexMap
			for(int c=0;c<levelWidth;c++)
				for(int l=0;l<levelHeight;l++)
					indexMap[c][l]=-1;
			//adding object indexes to indexMap
			for(int i=0;i<size();i++){
				int objectWidth=get(i).getTiledWidth();
				int objectHeight=get(i).getTiledHeight();
				for(int c=0;c<objectWidth;c++)
					for(int l=0;l<objectHeight;l++)
						indexMap[get(i).getPosition().x+c][get(i).getPosition().y+l] = i;
			}
			return returnObject;
		}
	}
	
}
