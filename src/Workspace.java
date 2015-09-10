import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class Workspace extends JPanel{
	
	private static final long serialVersionUID = 5180775942726499734L;

	public Canvas canvas;
	
	private boolean showGrid;
	private boolean showObjectBorder;
	private boolean showTileIndex;
	
	private static Workspace workspacePointer;
	
	public Workspace(){
		setBackground(Color.GRAY);
		SpringLayout slayout = new SpringLayout();
		setLayout(slayout);
		workspacePointer=this;
		
		canvas=new Canvas();
		slayout.putConstraint(SpringLayout.VERTICAL_CENTER, canvas, 
				0, SpringLayout.VERTICAL_CENTER, this);
		add(canvas);
	}
	
	public void showGrid(boolean flag){ this.showGrid=flag; canvas.repaint(); }
	public void showObjectBorder(boolean flag){ this.showObjectBorder=flag; canvas.updateLevelImage(); canvas.repaint(); }
	public void showTileIndex(boolean flag){ this.showTileIndex=flag; canvas.repaint(); }
	
	public void setLevel(Level level){
		ConstructorWindow.globals.level = level;
		canvas.generateLevel();
	}

	public class Canvas extends JPanel implements MouseListener, MouseMotionListener{
		
		private static final long serialVersionUID = -7903971845661537054L;

		private Level level;
		
		//Usual size of one tile in pixels
		private static final int TILE_SIZE = 60;
		
		private double scaleFactor;
		private int scaledTileSize;
		
		//Contains indexes of the tile cursor currently points on
		private Point activeTile;
		
		//Optimization. Image with painted level objects
		//Changes only when objects are added to or removed from level data
		private BufferedImage levelImage;
		private BufferedImage scaledLevelImage;
		
		//Optimization. Resized image of tile, selected in insertion tool
		//Changes only when new tile is chosen or scaleFactor changes
		private BufferedImage resizedTile;
		
		//Contains indexes of game objects in level.gameObjects array
		// -1 means empty tile
		public int [][] indexMap;
		
		//Number of columns which are incerted to level
		private int defaultColInsertValue = 5;
		
		public Canvas(){
			setBackground(Color.GRAY);
			addMouseListener(this);
			addMouseMotionListener(this);
			setEnabled(false);
		}
		
		public void generateLevel(){
			level = ConstructorWindow.globals.level;
			//Counting the scale factor to make sure that tiles are using all available vertical space 
			double sf = (ConstructorWindow.instance.workspace.getSize().getHeight())/(level.getHeight()*TILE_SIZE);
			setLevelSize(level.getWidth(),level.getHeight());
			updateLevelImage();
			for(int c=0;c<level.getWidth();c++)
				for(int l=0; l<level.getHeight();l++)
					indexMap[c][l]=-1;
			for(int i=0;i<level.getObjects().size();i++)
			for(int c=0;c<level.getObjects().get(i).getTiledWidth();c++)
				for(int l=0; l<level.getObjects().get(i).getTiledHeight();l++)
					indexMap[level.getObjects().get(i).getPosition().x+c][level.getObjects().get(i).getPosition().y+l]=i;
			ConstructorWindow.instance.toolsPanel.workspaceScaleJS.setValue((int)(sf*100));
			setEnabled(true);
		}
		
		private void setLevelSize(int width,int height){
			level.setSize(width,height);
			setPreferredSize(new Dimension((int)(level.getWidth()*scaledTileSize)+1,(int)(level.getHeight()*scaledTileSize)+1));
			setSize(getPreferredSize());
			workspacePointer.setPreferredSize(new Dimension(getSize()));
			setBackground(Color.WHITE);
			//setting original levelImage size to the usual, non-scaled, size of level
			levelImage=new BufferedImage((int)(level.getWidth()*TILE_SIZE)+1, (int)(level.getHeight()*TILE_SIZE)+1, BufferedImage.TYPE_INT_ARGB);
			//setting scaled levelImage size to fit size of canvas
			scaledLevelImage=new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			//initializing empty indexMap
			indexMap=new int[level.getWidth()][level.getHeight()];
			
			revalidate();
			repaint();
		}
		
		public void addLevelRow(boolean onFirstPosition){
			//saving indexMap before resizing
			int [][] indexMapBackup=new int [level.getWidth()][level.getHeight()];
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight();l++)
					indexMapBackup[c][l]=indexMap[c][l];
			//resizing level workspace, generating new indexMap
			setLevelSize(level.getWidth(),level.getHeight()+1);
			
			if(onFirstPosition){
				for(GameObject go : level.getObjects())
					go.setPosition(go.getPosition().x, go.getPosition().y+1);
			}
			updateLevelImage();
			
			//refilling indexMap
			if(onFirstPosition){
				for(int c=0;c<level.getWidth();c++)
					for(int l=0;l<level.getHeight();l++)
						if(l==0)
							//filling new row with empty indexes
							indexMap[c][l]=-1;
						else
							indexMap[c][l]=indexMapBackup[c][l-1];
			}
			else{
				for(int c=0;c<level.getWidth();c++)
					for(int l=0;l<level.getHeight();l++)
						if(l==level.getHeight()-1)
							//filling new row with empty indexes
							indexMap[c][l]=-1;
						else
							indexMap[c][l]=indexMapBackup[c][l];
			}
		}
		
		public void removeLevelRow(boolean onFirstPosition){
			if(level.getHeight()-1<level.getDefaultHeight())
				return;
			//saving indexMap before resizing
			int [][] indexMapBackup=new int [level.getWidth()][level.getHeight()-1];
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight()-1;l++)
					if(onFirstPosition)
						//saving all indexes except first row
						indexMapBackup[c][l]=indexMap[c][l+1];
					else
						//saving all indexes except last row
						indexMapBackup[c][l]=indexMap[c][l];
			
			//resizing level workspace, generating new indexMap
			setLevelSize(level.getWidth(),level.getHeight()-1);
			
			//refilling indexMap
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight();l++)
					indexMap[c][l]=indexMapBackup[c][l];
			
			//contains indexes of objects in level.gameObjects, which will be removed
			ArrayList<GameObject> objectsToRemove = new ArrayList<GameObject>();
			if(onFirstPosition){
				for(GameObject go : level.getObjects())
					if(go.getPosition().getY()==0)
						objectsToRemove.add(go);
					else
						go.setPosition(go.getPosition().x, go.getPosition().y-1);
			}
			else
				for(GameObject go : level.getObjects())
					if(go.getPosition().getY()+go.getTiledHeight()==level.getHeight()+1)
						objectsToRemove.add(go);
			
			for(GameObject go : objectsToRemove){
				level.getObjects().remove(go);
			}
			updateLevelImage();
		}
		
		public void addLevelCols(int colNumber){
			//saving indexMap before resizing
			int [][] indexMapBackup=new int [level.getWidth()][level.getHeight()];
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight();l++)
					indexMapBackup[c][l]=indexMap[c][l];
			//resizing level workspace, generating new indexMap
			setLevelSize(level.getWidth()+colNumber,level.getHeight());
			updateLevelImage();
			
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight();l++)
					if(c>=level.getWidth()-colNumber)
						//filling new columns with empty indexes
						indexMap[c][l]=-1;
					else
						indexMap[c][l]=indexMapBackup[c][l];
		}
		
		public void removeLevelCols(int colNumber){
			//saving indexMap before resizing
			int [][] indexMapBackup=new int [level.getWidth()-colNumber][level.getHeight()];
			for(int c=0;c<level.getWidth()-colNumber;c++)
				for(int l=0;l<level.getHeight();l++)
					indexMapBackup[c][l]=indexMap[c][l];
			
			//resizing level workspace, generating new indexMap
			setLevelSize(level.getWidth()-colNumber,level.getHeight());
			updateLevelImage();
			
			//refilling indexMap
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight();l++)
					indexMap[c][l]=indexMapBackup[c][l];
		}
		
		public void setScaleFactor(double scaleFactor){
			this.scaleFactor = scaleFactor;
			this.scaledTileSize=(int)(TILE_SIZE*scaleFactor);
		}
		
		public void resize(){
			setPreferredSize(new Dimension((int)(level.getWidth()*scaledTileSize)+1,(int)(level.getHeight()*scaledTileSize)+1));
			setSize(getPreferredSize());
			workspacePointer.setPreferredSize(new Dimension(getSize()));
			double sf = getPreferredSize().getHeight()/levelImage.getHeight();
			scaledLevelImage=resizeImage(levelImage,sf);

			if(Globals.toolBox.insertionTool.isActive())
				resizedTile=resizeImage(Globals.toolBox.insertionTool.getPrefab().getTexture(),scaleFactor);
			revalidate();
			workspacePointer.revalidate();
		}
		
		//repaints the image with all game objects
		private void updateLevelImage(){
			if(level==null||level.getObjects().isEmpty())
				return;
			Graphics2D g2d = levelImage.createGraphics();
			g2d.fillRect(0, 0, levelImage.getWidth(), levelImage.getHeight());
			
			for(GameObject go : level.getObjects()){
				int posX=(int)go.getPosition().getX()*TILE_SIZE;
				int posY=(int)go.getPosition().getY()*TILE_SIZE;
				int width=(int)go.getTiledWidth()*TILE_SIZE;
				int height=(int)go.getTiledHeight()*TILE_SIZE;
				g2d.drawImage(go.getTexture(), posX, posY, null);
				
				if(showObjectBorder){
					g2d.setColor(Color.BLUE);
					g2d.drawRect(posX, posY, width, height);
					g2d.setColor(Color.BLACK);
					}
			}
			double sf = getPreferredSize().getHeight()/levelImage.getHeight();
			scaledLevelImage=resizeImage(levelImage,sf);
		}
		
		//returns tile column and line indexes on selected position
		private Point getTilePositionAt(int x, int y){
			int columnIndex = x/scaledTileSize;
			int lineIndex = y/scaledTileSize;
			return new Point(columnIndex,lineIndex);
		}
		
		private BufferedImage resizeImage(BufferedImage originalImage, double scaleFactor){
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage resizedImage = new BufferedImage(this.getWidth(), this.getHeight(), type);
			Graphics2D g = resizedImage.createGraphics();
			
			int resizedWidth = (int)(originalImage.getWidth()*scaleFactor);
			int resizedHeight = (int)(originalImage.getHeight()*scaleFactor);
			
			g.drawImage(originalImage, 0, 0, resizedWidth, resizedHeight, null);
			g.dispose();
		 
			return resizedImage;
		}
		
		private Point getObjectMainTile(int objectIndex){
			for(int c=0;c<level.getWidth();c++)
				for(int l=0;l<level.getHeight();l++)
					if(indexMap[c][l]==objectIndex)
						return new Point(c,l);
			return null;
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			
			//draw level objects
			if(level!=null&&!level.getObjects().isEmpty())
				g2d.drawImage(scaledLevelImage, 0, 0, null);
			//draw grid
			if(showGrid){
				g2d.setColor(Color.GRAY);
				g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
				for(int i=1;i<level.getHeight();i++){
					g2d.drawLine(0, i*scaledTileSize, level.getWidth()*scaledTileSize, i*scaledTileSize);
				}
				for(int i=1;i<level.getWidth();i++){
					g2d.drawLine(i*scaledTileSize, 0, i*scaledTileSize, level.getHeight()*scaledTileSize);
				}
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(1));
			}
			//draw tile indexes
			if(showTileIndex){
				for(int h=1;h<=level.getHeight();h++){
					for(int w=1;w<=level.getWidth();w++){
						Point tile = getTilePositionAt(w*scaledTileSize,h*scaledTileSize);
						String tileIndex = "["+(int)(tile.getX()-1)+";"+(int)(tile.getY()-1)+"]";
						int posX=(int)tile.getX()*scaledTileSize-scaledTileSize/2 - tileIndex.length()*3;
						int posY=(int)(tile.getY()*scaledTileSize-scaledTileSize/2 + 5);
						g2d.drawString(tileIndex, posX, posY);
					}
					
				}
				g2d.setColor(Color.BLACK);
			}
			//draw pointer of active tile
			if(activeTile!=null){
				int posX=(int)activeTile.getX()*scaledTileSize;
				int posY=(int)activeTile.getY()*scaledTileSize;
				//if insertion tool is active - draw selected prefab texture on activeTile position
				if(Globals.toolBox.insertionTool.isActive()){
					Prefab prefab = Globals.toolBox.insertionTool.getPrefab();
					g2d.drawImage(resizedTile, posX, posY, null);
					//if selected prefab is linked, draw a pointer to master
					if(Globals.toolBox.insertionTool.hasMasterObject()){
						GameObject masterObject= Globals.toolBox.insertionTool.getMasterObject();
						int masterPositionX = masterObject.getPosition().x*scaledTileSize;
						int masterPositionY = masterObject.getPosition().y*scaledTileSize;
						int masterWidth = masterObject.getTiledWidth()*scaledTileSize;
						int masterHeight = masterObject.getTiledHeight()*scaledTileSize;
						g2d.drawRect(masterPositionX, masterPositionY, masterWidth, masterHeight);
						g2d.drawRect(posX, posY, prefab.getTiledWidth()*scaledTileSize, prefab.getTiledHeight()*scaledTileSize);
						
						g2d.setColor(Color.RED);
						g2d.drawLine(masterPositionX+masterWidth/2, masterPositionY+masterHeight/2,
								posX+prefab.getTiledWidth()*scaledTileSize/2,
								posY+prefab.getTiledHeight()*scaledTileSize/2);
						g2d.fillRect(masterPositionX+masterWidth/2-3, masterPositionY+masterHeight/2-3, 6, 6);
						g2d.fillRect(posX+prefab.getTiledWidth()*scaledTileSize/2-3, 
								posY+prefab.getTiledHeight()*scaledTileSize/2-3, 6, 6);
						g2d.setColor(Color.BLACK);
					}
					else
						g2d.drawRect(posX, posY, prefab.getTiledWidth()*scaledTileSize, prefab.getTiledHeight()*scaledTileSize);
				}
				//if insertion tool is inactive - show object borders and their info
				else {
					//if tile is not empty
					int index =indexMap [Math.min(activeTile.x,level.getWidth()-1)][Math.min(activeTile.y,level.getHeight()-1)];
					if(index!=-1){
						GameObject currentObject=level.getObjects().get(index);
						int width = currentObject.getTiledWidth()*scaledTileSize;
						int height = currentObject.getTiledHeight()*scaledTileSize;
						Point mainTilePosition = getObjectMainTile(index);
						//if active tile doesn't have linked objects, draw it's border
						if(!currentObject.isComplex())
							g2d.drawRect(mainTilePosition.x*scaledTileSize, mainTilePosition.y*scaledTileSize, width, height);
						else{
							//if this object has links, draw pointers to them
							GameObject linkedObject = null;
							if(currentObject.isMaster())
								linkedObject=currentObject.getSlave();
							else if(currentObject.isSlave())
								linkedObject=currentObject.getMaster();
							
							Point masterPosition = new Point(linkedObject.getPosition().x*scaledTileSize,linkedObject.getPosition().y*scaledTileSize);
							Point masterSize = new Point(linkedObject.getTiledWidth()*scaledTileSize,linkedObject.getTiledHeight()*scaledTileSize);
							Point masterCenter = new Point(masterPosition.x+masterSize.x/2,masterPosition.y+masterSize.y/2);
							Point mainPosition = new Point(mainTilePosition.x*scaledTileSize,mainTilePosition.y*scaledTileSize);
							Point mainSize = new Point(currentObject.getTiledWidth()*scaledTileSize,currentObject.getTiledHeight()*scaledTileSize);
							Point mainCenter = new Point(mainPosition.x+currentObject.getTiledWidth()*scaledTileSize/2,mainPosition.y+currentObject.getTiledHeight()*scaledTileSize/2);
							g2d.drawRect(masterPosition.x, masterPosition.y, masterSize.x, masterSize.y);
							g2d.drawRect(mainPosition.x, mainPosition.y, mainSize.x, mainSize.y);
							
							g2d.setColor(Color.RED);
							g2d.drawLine(masterCenter.x, masterCenter.y, mainCenter.x,mainCenter.y);
							g2d.fillRect(masterCenter.x-3, masterCenter.y-3, 6, 6);
							g2d.fillRect(mainCenter.x-3, mainCenter.y-3, 6, 6);
							g2d.setColor(Color.BLACK);
						}
					}
					else
						g2d.drawRect(posX, posY, scaledTileSize, scaledTileSize);
					}
			}
		}

		@Override public void mouseClicked(MouseEvent arg0) {}

		@Override public void mouseEntered(MouseEvent arg0) {
			if(!isEnabled()) return;
			activeTile=new Point(getTilePositionAt(arg0.getX(),arg0.getY()));
			int index = indexMap[Math.min(activeTile.x, level.getWidth()-1)][Math.min(activeTile.y, level.getHeight()-1)];
			if(index!=-1)
				ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(level.getObjects().get(index).getPrefab());
			if(Globals.toolBox.insertionTool.isActive()){
				ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(Globals.toolBox.insertionTool.getPrefab());
				resizedTile=resizeImage(Globals.toolBox.insertionTool.getPrefab().getTexture(),scaleFactor);
				}
			repaint();
		}

		@Override public void mouseExited(MouseEvent arg0) {
			if(!isEnabled()) return;
			activeTile = null;
			resizedTile = null;
			ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(null);
			repaint();
		}

		@Override public void mousePressed(MouseEvent arg0) {
			if(!isEnabled()) return;
			if(arg0.getButton()==MouseEvent.BUTTON1&&Globals.toolBox.insertionTool.isActive()){
				boolean overlapsSmth = false;
				Prefab prefab = Globals.toolBox.insertionTool.getPrefab();
				int prefabWidth = prefab.getTiledWidth();
				int prefabHeight = prefab.getTiledHeight();
				
				for(int c=0;c<prefabWidth;c++)
					for(int l=0;l<prefabHeight;l++){
						if(indexMap[Math.min(level.getWidth()-1, activeTile.x+c)][Math.min(level.getHeight()-1, activeTile.y+l)]!=-1)
							overlapsSmth=true;
					}
				if(!overlapsSmth&&activeTile.y+prefabHeight<=level.getHeight()){
					//if current level width is not enough to contain the object, add needed number of columns to the end of level
					if(activeTile.x+prefabWidth>level.getWidth()-defaultColInsertValue)
						addLevelCols((activeTile.x+prefabWidth)-(level.getWidth()-defaultColInsertValue));
					//Insert new object to the level
					GameObject gameObject = new GameObject(prefab,activeTile.x,activeTile.y);
					level.getObjects().add(gameObject);
					updateLevelImage();
					repaint();
					if(prefab.isComplex()){
						//get next prefab in the sequence
						Prefab linkedPrefab = null;
						if(prefab.isMaster())
							linkedPrefab = prefab.getSlavePrefab();
						else if(prefab.isSlave())
							linkedPrefab = prefab.getMasterPrefab();
						
						if(Globals.toolBox.insertionTool.hasMasterObject()){
							//if current object was the last in the sequence "master-slave"
							//set relations between game objects 
							Globals.toolBox.insertionTool.getMasterObject().setSlave(gameObject);
							
							//invoke usual insertion tool without link to a master
							Globals.toolBox.insertionTool.invoke(linkedPrefab);
							}
						else
							Globals.toolBox.insertionTool.invoke(linkedPrefab,gameObject);
						resizedTile=resizeImage(linkedPrefab.getTexture(),scaleFactor);
						ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(linkedPrefab);
					}
				}
			}
			if(arg0.getButton()==MouseEvent.BUTTON2){
				ConstructorWindow.instance.toolsPanel.workspaceScaleJS.setValue(100);
			}
			if(arg0.getButton()==MouseEvent.BUTTON3){
				int index = indexMap[activeTile.x][activeTile.y];
				//if insertion tool active, right click deactivates it
				if(Globals.toolBox.insertionTool.isActive()){
					//remove master object without a slave
					if(Globals.toolBox.insertionTool.hasMasterObject()){
						level.getObjects().remove(Globals.toolBox.insertionTool.getMasterObject());
						updateLevelImage();
						}
					Globals.toolBox.insertionTool.disable();
					ConstructorWindow.instance.collectionsPanel.tilesTab.removeSelection();
					resizedTile=null;
					if(index!=-1){
						Prefab prefab = level.getObjects().get(index).getPrefab();
						ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(prefab);
					}
				}
				else
					//if insertion tool isn't active, right click deletes object under the pointer
					if(index!=-1){
						//deleting the object from level and indexMap 
						GameObject selectedObject = level.getObjects().get(index);
						if(selectedObject.isComplex()){
							GameObject linkedObject = null;
							if(selectedObject.isMaster())
								linkedObject = selectedObject.getSlave();
							else if(selectedObject.isSlave())
								linkedObject = selectedObject.getMaster();
							level.getObjects().remove(selectedObject);
							level.getObjects().remove(linkedObject);
						}
						else
							level.getObjects().remove(index);
						
						
						//getting the last non-empty index and resizing the level
						for(int c=level.getWidth()-1;c>=0;c--){
							boolean indexFound = false;
							for(int l=0;l<level.getHeight();l++)
								if(indexMap[c][l]!=-1){
									int colNumber = level.getWidth() - Math.max(c+defaultColInsertValue+1,level.getDefaultWidth());
									removeLevelCols(colNumber);
									indexFound=true;
									break;
								}
							if(indexFound) break;
						}
						ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(null);
					}
				repaint();
			}
		}

		@Override public void mouseReleased(MouseEvent arg0) {}

		@Override public void mouseDragged(MouseEvent e) {
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(!isEnabled()) return;
			Point tile = getTilePositionAt(e.getX(),e.getY());
			if(tile.getX()!=activeTile.getX()||tile.getY()!=activeTile.getY()){
				activeTile.setLocation(tile);
				int index = indexMap[Math.min(activeTile.x, level.getWidth()-1)][Math.min(activeTile.y, level.getHeight()-1)];
				//if tile is not empty
				if(index!=-1&&!Globals.toolBox.insertionTool.isActive()){
					Prefab prefab = level.getObjects().get(index).getPrefab();
					ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(prefab);
				}
				else{
					ConstructorWindow.instance.collectionsPanel.tilesTab.showPrefabInfo(Globals.toolBox.insertionTool.getPrefab());
				}
				repaint();
			}
		}
	}	
	
	
	
}
