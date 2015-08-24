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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

public class Workspace extends JPanel{
	
	public Canvas canvas;
	
	private boolean showGrid;
	private boolean showObjectBorder;
	private boolean showTileIndex;
	
	private Workspace workspacePointer;
	
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
			
			revalidate();
			repaint();
		}
		
		private void setScaleFactor(double scaleFactor){
			this.scaleFactor = scaleFactor;
			this.scaledTileSize=(int)(TILE_SIZE*scaleFactor);
		}
		
		private Point getTilePositionAt(int x, int y){
			int columnIndex = x/scaledTileSize;
			int lineIndex = y/scaledTileSize;
			return new Point(columnIndex,lineIndex);
		}
		
		private BufferedImage resizeImage(BufferedImage originalImage){
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			BufferedImage resizedImage = new BufferedImage(this.getWidth(), this.getHeight(), type);
			Graphics2D g = resizedImage.createGraphics();
			
			int resizedWidth = (int)(originalImage.getWidth()*scaleFactor);
			int resizedHeight = (int)(originalImage.getHeight()*scaleFactor);
			
			g.drawImage(originalImage, 0, 0, resizedWidth, resizedHeight, null);
			g.dispose();
		 
			return resizedImage;
		}
		
		@Override
		public void paint(Graphics g){
			Graphics2D g2d = (Graphics2D)g;
			super.paint(g);
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
					if(scaleFactor==1.0)
						g2d.drawImage(prefab.getTexture(), posX, posY, null);
					else
						g2d.drawImage(resizeImage(prefab.getTexture()), posX, posY, null);
					g2d.drawRect(posX, posY, prefab.getTiledWidth()*scaledTileSize, prefab.getTiledHeight()*scaledTileSize);
				}
				else 
					g2d.drawRect(posX, posY, scaledTileSize, scaledTileSize);
			}
			
			drawLevelObjects(g2d);
			/*
			if(showObjectBorder){
				g2d.setColor(Color.BLUE);
				for(int i=0;i<level.objectsLayer.size();i++){
					GameObject gameObject = level.objectsLayer.get(i);
					g2d.drawRect(gameObject.inGamePositionX,gameObject.inGamePositionY,gameObject.object.tiledWidth*60,gameObject.object.tiledHeight*60);
				}
				g2d.setColor(Color.BLACK);
			}*/
		}
		
		private void drawLevelObjects(Graphics2D g2d){
			if(level==null||level.getObjects().isEmpty())
				return;
			for(GameObject go : level.getObjects()){
				int posX=(int)go.getPosition().getX()*scaledTileSize;
				int posY=(int)go.getPosition().getY()*scaledTileSize;
				int width=(int)go.getTiledWidth()*scaledTileSize;
				int height=(int)go.getTiledHeight()*scaledTileSize;
				if(scaleFactor==1.0)
					g2d.drawImage(go.getTexture(), posX, posY, null);
				else
					g2d.drawImage(resizeImage(go.getTexture()), posX, posY, null);
				
				if(showObjectBorder)
					g2d.drawRect(posX, posY, width, height);
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			setScaleFactor(scaleFactor-arg0.getWheelRotation()*0.04);
			setPreferredSize(new Dimension((int)(level.getWidth()*scaledTileSize)+1,(int)(level.getHeight()*scaledTileSize)+1));
			setSize(getPreferredSize());
			workspacePointer.setPreferredSize(new Dimension(getSize()));
			Point tile = getTilePositionAt(arg0.getX(),arg0.getY());
			if(activeTile!=null&&(tile.getX()!=activeTile.getX()||tile.getY()!=activeTile.getY()))
				activeTile.setLocation(tile);
			revalidate();
			repaint();
		}

		@Override public void mouseClicked(MouseEvent arg0) {}

		@Override public void mouseEntered(MouseEvent arg0) {
			activeTile=new Point(getTilePositionAt(arg0.getX(),arg0.getY()));
		}

		@Override public void mouseExited(MouseEvent arg0) {
			activeTile = null;
			repaint();
		}

		@Override public void mousePressed(MouseEvent arg0) {
			if(arg0.getButton()==MouseEvent.BUTTON1&&Globals.toolBox.insertionTool.isActive()){
				//Insert new object to the level
				level.getObjects().add(new GameObject(Globals.toolBox.insertionTool.getPrefab(),activeTile.x,activeTile.y));
				System.out.println("tile inserted at "+activeTile.toString());
			}
			if(arg0.getButton()==MouseEvent.BUTTON2){
				setScaleFactor(1.0);
				setPreferredSize(new Dimension((int)(level.getWidth()*scaledTileSize)+1,(int)(level.getHeight()*scaledTileSize)+1));
				setSize(getPreferredSize());
				workspacePointer.setPreferredSize(getPreferredSize());
				
				revalidate();
				repaint();
			}
			if(arg0.getButton()==MouseEvent.BUTTON3){
				if(Globals.toolBox.insertionTool.isActive())
					Globals.toolBox.insertionTool.disable();
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
