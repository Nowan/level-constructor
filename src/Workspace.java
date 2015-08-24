import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

public class Workspace extends JPanel{
	
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
	public void showObjectBorder(boolean flag){ this.showObjectBorder=flag; canvas.repaint(); }
	public void showTileIndex(boolean flag){ this.showTileIndex=flag; canvas.repaint(); }
	
	public void setLevel(Level level){
		ConstructorWindow.instance.globals.level = level;
		canvas.generateLevel();
		addMouseWheelListener(canvas);
	}

	private class Canvas extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener{
		
		private Level level;
		
		//Usual size of one tile in pixels
		private static final int TILE_SIZE = 60;
		
		private double scaleFactor;
		private int scaledTileSize;
		
		private Point activeTile;
		
		//Optimization. Image with painted level objects
		//Changes only when objects are added to or removed from level data
		private BufferedImage levelImage;
		private BufferedImage resizedLevelImage;
		
		//Optimization. Resized image of tile, selected in insertion tool
		//Changes only when new tile is chosen or scaleFactor changes
		private BufferedImage resizedTile;
		
		public Canvas(){
			setBackground(Color.GRAY);
			addMouseWheelListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		public void generateLevel(){
			level = ConstructorWindow.instance.globals.level;
			//Counting the scale factor to make sure that tiles are using all available vertical space 
			double sf = (ConstructorWindow.instance.workspace.getSize().getHeight())/(level.getHeight()*TILE_SIZE);
			setScaleFactor(sf);
			setPreferredSize(new Dimension((int)(level.getWidth()*scaledTileSize)+1,(int)(level.getHeight()*scaledTileSize)+1));
			setSize(getPreferredSize());
			workspacePointer.setPreferredSize(new Dimension(getSize()));
			setBackground(Color.WHITE);
			levelImage=new BufferedImage((int)(level.getWidth()*TILE_SIZE)+1, (int)(level.getHeight()*TILE_SIZE)+1, BufferedImage.TYPE_INT_ARGB);
			resizedLevelImage=new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			
			revalidate();
			repaint();
		}
		
		private void setScaleFactor(double scaleFactor){
			this.scaleFactor = scaleFactor;
			this.scaledTileSize=(int)(TILE_SIZE*scaleFactor);
		}
		
		private void resize(){
			setPreferredSize(new Dimension((int)(level.getWidth()*scaledTileSize)+1,(int)(level.getHeight()*scaledTileSize)+1));
			setSize(getPreferredSize());
			workspacePointer.setPreferredSize(new Dimension(getSize()));
			double sf = getPreferredSize().getHeight()/levelImage.getHeight();
			resizedLevelImage=resizeImage(levelImage,sf);

			System.out.println("= timer starts =");
			
			//updateLevelImage();
			if(Globals.toolBox.insertionTool.isActive())
				resizedTile=resizeImage(Globals.toolBox.insertionTool.getPrefab().getTexture(),scaleFactor);
		}
		
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
					g2d.setColor(Color.BLACK);
					g2d.drawRect(posX, posY, width, height);
					}
			}
			double sf = getPreferredSize().getHeight()/levelImage.getHeight();
			resizedLevelImage=resizeImage(levelImage,sf);
		}
		
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
		
		private void enlightItem(){
			Timer timer = new Timer();
		    timer.schedule(new TimerTask(){
		    	
		    	private int counter=0;
		    	private boolean lighted=false;
		    	
				@Override
				public void run() {
					if(counter==6){
						this.cancel();
						}
					else{
						if(lighted){
							//targetTMI.setBorder(BorderFactory.createEmptyBorder());
							lighted=false;
							}
						else{
							//targetTMI.setBorder(BorderFactory.createLineBorder(Color.RED,2));
							lighted=true;
						}
						counter++;
					}
					}
		    }, 0,75);
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			
			if(level!=null&&!level.getObjects().isEmpty())
				g2d.drawImage(resizedLevelImage, 0, 0, null);
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
			
			if(showTileIndex){
				g2d.setColor(Color.GRAY);
				for(int h=1;h<=level.getHeight();h++){
					for(int w=1;w<=level.getWidth();w++){
						Point tile = getTilePositionAt(w*scaledTileSize,h*scaledTileSize);
						String tileIndex = "["+(int)tile.getX()+";"+(int)tile.getY()+"]";
						int posX=(int)tile.getX()*scaledTileSize-scaledTileSize/2 - tileIndex.length()*3;
						int posY=(int)(tile.getY()*scaledTileSize-scaledTileSize/2 + 5);
						g2d.drawString(tileIndex, posX, posY);
					}
					
				}
				g2d.setColor(Color.BLACK);
			}
			
			if(activeTile!=null){
				int posX=(int)activeTile.getX()*scaledTileSize;
				int posY=(int)activeTile.getY()*scaledTileSize;
				if(Globals.toolBox.insertionTool.isActive()){
					Prefab prefab = Globals.toolBox.insertionTool.getPrefab();
					g2d.drawImage(resizedTile, posX, posY, null);
					g2d.drawRect(posX, posY, prefab.getTiledWidth()*scaledTileSize, prefab.getTiledHeight()*scaledTileSize);
				}
				else 
					g2d.drawRect(posX, posY, scaledTileSize, scaledTileSize);
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			setScaleFactor(scaleFactor-arg0.getWheelRotation()*0.04);
			resize();
			Point tile = getTilePositionAt(arg0.getX(),arg0.getY());
			if(activeTile!=null&&(tile.getX()!=activeTile.getX()||tile.getY()!=activeTile.getY()))
				activeTile.setLocation(tile);
			revalidate();
			repaint();
		}

		@Override public void mouseClicked(MouseEvent arg0) {}

		@Override public void mouseEntered(MouseEvent arg0) {
			activeTile=new Point(getTilePositionAt(arg0.getX(),arg0.getY()));
			if(Globals.toolBox.insertionTool.isActive())
				resizedTile=resizeImage(Globals.toolBox.insertionTool.getPrefab().getTexture(),scaleFactor);
		}

		@Override public void mouseExited(MouseEvent arg0) {
			activeTile = null;
			resizedTile = null;
			repaint();
		}

		@Override public void mousePressed(MouseEvent arg0) {
			if(arg0.getButton()==MouseEvent.BUTTON1&&Globals.toolBox.insertionTool.isActive()){
				//Insert new object to the level
				level.getObjects().add(new GameObject(Globals.toolBox.insertionTool.getPrefab(),activeTile.x,activeTile.y));
				System.out.println("tile inserted at "+activeTile.toString());
				updateLevelImage();
				repaint();
			}
			if(arg0.getButton()==MouseEvent.BUTTON2){
				setScaleFactor(1.0);
				resize();
				revalidate();
				repaint();
			}
			if(arg0.getButton()==MouseEvent.BUTTON3){
				if(Globals.toolBox.insertionTool.isActive())
					Globals.toolBox.insertionTool.disable();
				resizedTile=null;
				repaint();
			}
		}

		@Override public void mouseReleased(MouseEvent arg0) {}

		@Override public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			Point tile = getTilePositionAt(e.getX(),e.getY());
			if(tile.getX()!=activeTile.getX()||tile.getY()!=activeTile.getY()){
				activeTile.setLocation(tile);
				repaint();
			}
		}
	}	
	
	
	
}
