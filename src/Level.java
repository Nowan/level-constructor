import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Level {

	private String fileAddress;
	
	private GameObjects gameObjects;
	
	private int width;
	
	private int height;
	
	private int defaultWidth;
	
	private int defaultHeight;
	
	public Level(int defaultWidth, int defaultHeight){
		gameObjects = new GameObjects();
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		setSize(defaultWidth,defaultHeight);
	}
	
	public GameObjects getObjects(){
		return gameObjects;
	}
	
	public void setObjects(ArrayList<GameObject> gameObjects){
		ConstructorWindow.instance.workspace.canvas.indexMap = new int [width][height];
		for(GameObject go : gameObjects)
			this.gameObjects.add(go);
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
	
	public int getDefaultWidth(){
		return defaultWidth;
	}
	
	public int getDefaultHeight(){
		return defaultHeight;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public String getFileAddress(){
		return fileAddress;
	}
	
	public void setFileAddress(String fileAddress){
		this.fileAddress = fileAddress;
	}
	
	public class GameObjects extends ArrayList<GameObject>{

		private static final long serialVersionUID = -724843783326240571L;

		public GameObjects(){
			super();
		}
		
		@Override 
		public boolean add(GameObject e){
			boolean returnBit = super.add(e);
			//sort array so the elements will be drawn in the right order
			Collections.sort(this, new Comparator<GameObject>() {
		        @Override
		        public int compare(GameObject gameObject1, GameObject  gameObject2)
		        {
		        	int c1 = gameObject1.getPosition().y+gameObject1.getPosition().x*getHeight();
		        	int c2 = gameObject2.getPosition().y+gameObject2.getPosition().x*getHeight();
		            return Integer.compare(c2, c1);
		        }
		    });
			//rebuild index map in canvas
			int [][] indexMap = ConstructorWindow.instance.workspace.canvas.indexMap;
			for(int i=0;i<this.size();i++)
				for(int c=0;c<this.get(i).getTiledWidth();c++)
					for(int l=0;l<this.get(i).getTiledHeight();l++){
						indexMap[this.get(i).getPosition().x+c][this.get(i).getPosition().y+l]=i;
					}
			return returnBit;
		}
		
		@Override 
		public GameObject remove(int index){
			GameObject returnObject = super.remove(index);
			//setting object indexes in canvas.indexMap
			int [][] indexMap = ConstructorWindow.instance.workspace.canvas.indexMap;
			int levelWidth = ConstructorWindow.globals.level.getWidth();
			int levelHeight = ConstructorWindow.globals.level.getHeight();
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
		
		@Override
		public boolean remove(Object o){
			boolean returnObject = super.remove(o);
			//setting object indexes in canvas.indexMap
			int [][] indexMap = ConstructorWindow.instance.workspace.canvas.indexMap;
			int levelWidth = ConstructorWindow.globals.level.getWidth();
			int levelHeight = ConstructorWindow.globals.level.getHeight();
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
						if(get(i).getPosition().x+c<ConstructorWindow.globals.level.getWidth()&&get(i).getPosition().y+l<ConstructorWindow.globals.level.getHeight())
						indexMap[get(i).getPosition().x+c][get(i).getPosition().y+l] = i;
			}
			return returnObject;
		}
	}
	
}
