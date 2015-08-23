import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TilesTab extends JPanel{
	
	//navigation panel, shows all existing prefabs in the game 
	private JPanel prefabsPane;
	
	//filters for prefabsPane
	private JComboBox<String> selectCategoryJCB;

	//panel, which displays selected prefab
	public PreviewPanel previewPanel;
	private JScrollPane vbar;
	
	//selected prefab in prefabsPane
	private PrefabPreviewItem selectedItem;
	
	public TilesTab(){
		
		//adding filters for prefabsPane to the content panel
		JPanel FilterPane = new JPanel(new GridLayout(1,2));
		selectCategoryJCB = new JComboBox<String>(GOBase.prefabCategoryBase.getNameCollection());
		selectCategoryJCB.setPreferredSize(new Dimension(150,25));
		selectCategoryJCB.setFont(Globals.DEFAULT_FONT);
		selectCategoryJCB.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        onFilterChanged();
		    }
		});
		JLabel selectCategoryJL = new JLabel("Select category:  ",JLabel.RIGHT);
		selectCategoryJL.setFont(Globals.PARAMETER_FONT);
		FilterPane.add(selectCategoryJL);
		FilterPane.add(selectCategoryJCB);
		
		add(FilterPane);
		
		//adding prefab panel to content panel
		prefabsPane = new JPanel();
		prefabsPane.setAutoscrolls(true);
		prefabsPane.setBackground(new Color(0,0,0));
		for(Prefab P : GOBase.prefabsBase){
			prefabsPane.add(new PrefabPreviewItem(P));
		}
		selectCategoryJCB.setSelectedIndex(0);
		
		vbar = new JScrollPane(prefabsPane,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		vbar.setPreferredSize(new Dimension(300,340));
		add(vbar);
		
		//adding preview panel to content panel
		previewPanel = new PreviewPanel();
		add(previewPanel);
	}

	//invokes when value in one of the filter checkboxes changed
	private void onFilterChanged(){
		//remove all items in the prefabsPane 
		prefabsPane.removeAll();
		
		for(Prefab P : GOBase.prefabsBase){
			if(P.getCategory().getName().equals(selectCategoryJCB.getSelectedItem().toString()))
				prefabsPane.add(new PrefabPreviewItem(P));
		}
		
		//resize prefabsPane to fit the new number of items 
		if(prefabsPane.getComponentCount()!=0)
			prefabsPane.setPreferredSize(new Dimension(285,prefabsPane.getComponentCount()*(prefabsPane.getComponent(0).getPreferredSize().height/2+20)));
		prefabsPane.setSize(prefabsPane.getPreferredSize());
		prefabsPane.repaint();
	}
	
	
	public void removeSelection(){
		Globals.toolBox.insertionTool.disable();
		selectedItem.setBorder(BorderFactory.createEmptyBorder());
		selectedItem = null;
		//ConstructorWindow.instance.workspace.repaintLevel();
		previewPanel.hideImage();
	}
	
	public void selectItem(PrefabPreviewItem ppi){
		ppi.setBorder(BorderFactory.createLineBorder(Color.GREEN,3));
		Globals.toolBox.insertionTool.invoke(ppi.prefab);
		selectedItem = ppi;
		ppi.repaint();
	}
	 
	public class PreviewPanel extends JPanel{
		
		//original image of selected tile
		private Image image;
		
		public PreviewPanel(){
			this.setPreferredSize(new Dimension(300,300));
			this.setSize(this.getPreferredSize());
			this.setBackground(new Color(100,100,100));
			//at start, preview panel is empty
			this.hideImage();
		}
		
		//set image to preview
		public void setImage(Image image){
			this.image = image;
			repaint();
		}
		
		//hide image from preview
		public void hideImage(){
			image = null;
			repaint();
		}
		
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			//if no image is attached, do nothing
			if(image==null) return;
			//elsewise draw image
			g.drawImage(image,0,0,null); 
		} 
		
		@Override
		public void setEnabled(boolean arg0){
			for(int i=0;i<getComponentCount();i++)
				getComponent(i).setEnabled(arg0);
		}
	}
	
	//a class for an item in the prefabsPane 
	public class PrefabPreviewItem extends JButton{
		
		//tile, to which it linked to
		private Prefab prefab;
		
		//minimized image, which will fit to the size of item
		private BufferedImage resizedImage;
		
		public PrefabPreviewItem(Prefab prefab){
			this.prefab = prefab;
			setPreferredSize(new Dimension(130,150));
			setContentAreaFilled(false);
			//when the filter is changed, prefabsPane generates new array TilePreviewItem which will suit the filter
			//in this case, selected tile must be created with border and a link must change
			
			if(Globals.toolBox.insertionTool.isActive()&&this.prefab==Globals.toolBox.insertionTool.getPrefab()){
				setBorder(BorderFactory.createLineBorder(new Color(0,255,0),3));
				selectedItem=this;
			}
			else
				this.setBorder(BorderFactory.createEmptyBorder());
			this.addMouseListener(new MouseAdapter()
	        {
				public void mousePressed(MouseEvent evt) {
					ConstructorWindow.instance.mouseClicked(evt);
					onMouseClick();
	        	}
	            public void mouseEntered(MouseEvent evt)
	            {
	            	onMouseOver();
	            }
	            public void mouseExited(MouseEvent evt)
	            {
	            	onMouseOut();
	            }
	        });
			
			BufferedImage originalImage = prefab.getTexture();
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			resizedImage = resizeImage(originalImage, type);
			
			this.setIcon(new ImageIcon(resizedImage));
		}
		
		public void onMouseOver(){
			if(this.prefab!=Globals.toolBox.insertionTool.getPrefab())
				this.setBorder(BorderFactory.createLineBorder(new Color(0,255,0)));
			previewPanel.setImage(prefab.getTexture());
		}
		
		public void onMouseOut(){
			if(this.prefab!=Globals.toolBox.insertionTool.getPrefab())
				this.setBorder(BorderFactory.createEmptyBorder());
			if(Globals.toolBox.insertionTool.isActive())
				previewPanel.setImage(Globals.toolBox.insertionTool.getPrefab().getTexture());
			else 
				previewPanel.hideImage();
		}
		
		public void onMouseClick(){
			if(this.prefab==Globals.toolBox.insertionTool.getPrefab()){
				removeSelection();
				setBorder(BorderFactory.createLineBorder(Color.GREEN,1));
			}
			else{
				if(selectedItem!=null)
					selectedItem.setBorder(BorderFactory.createEmptyBorder());
				selectItem(this);
			}
		}
		
		private BufferedImage resizeImage(BufferedImage originalImage, int type){
			BufferedImage resizedImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, type);
			Graphics2D g = resizedImage.createGraphics();
			
			float scaleFactor = (float)((this.getPreferredSize().width+ this.getPreferredSize().height)/2)/Math.max(originalImage.getWidth(), originalImage.getHeight());
			int resizedWidth = (int)(originalImage.getWidth()*scaleFactor);
			int resizedHeight = (int)(originalImage.getHeight()*scaleFactor);
			int posX = this.getPreferredSize().width/2 - resizedWidth/2;
			int posY = this.getPreferredSize().height/2 - resizedHeight/2;
			
			g.drawImage(originalImage, posX, posY, resizedWidth, resizedHeight, null);
			g.dispose();
		 
			return resizedImage;
		    }
	}
	
	
	@Override
	public void setEnabled(boolean arg0){
		super.setEnabled(arg0);
		selectCategoryJCB.setEnabled(arg0);
		if(!arg0){
			vbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			prefabsPane.removeAll();
			previewPanel.hideImage();
		}
		else{
			vbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			prefabsPane.removeAll();
			onFilterChanged();
		}
	}
}
