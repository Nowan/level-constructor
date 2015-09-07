import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TilesTab extends JPanel{
	
	private static final long serialVersionUID = 5410462936467217548L;

	//navigation panel, shows all existing prefabs in the game 
	private JPanel prefabsPane;
	
	//filters for prefabsPane
	private JComboBox<String> selectCategoryJCB;
	private DefaultComboBoxModel<String> comboBoxModel;
	
	//panel, which displays parameters of selected prefab
	private InspectorPanel inspectorPanel;
	
	//panel, which displays texture of selected prefab
	private PreviewPanel previewPanel = new PreviewPanel();
	
	private JScrollPane vbar;
	
	//selected prefab in prefabsPane
	private PrefabPreviewItem selectedItem;
	
	public TilesTab(){
		
		//adding filters for prefabsPane to the content panel
		JPanel FilterPane = new JPanel(new GridLayout(1,2));
		comboBoxModel = new DefaultComboBoxModel<String>();
		for(PrefabCategory p : GOBase.prefabCategoryBase)
			comboBoxModel.addElement(p.getName());
		selectCategoryJCB = new JComboBox<String>(comboBoxModel);
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
		vbar.setPreferredSize(new Dimension(300,220));
		add(vbar);
		
		//adding inspector panel to content panel
		inspectorPanel = new InspectorPanel();
		add(inspectorPanel);
		
		//adding preview panel to content panel
		previewPanel = new PreviewPanel();
		previewPanel.setPreferredSize(new Dimension(300,350));
		add(previewPanel);
	}
	
	public void refreshPrefabPanel(){
		selectCategoryJCB.setSelectedIndex(selectCategoryJCB.getSelectedIndex());
	}
	
	public void refreshCategoryList(){
		comboBoxModel.removeAllElements();
		for(PrefabCategory p : GOBase.prefabCategoryBase)
			comboBoxModel.addElement(p.getName());
		if(selectCategoryJCB.getSelectedIndex()>=comboBoxModel.getSize())
			selectCategoryJCB.setSelectedIndex(0);
	}

	//invokes when value in one of the filter checkboxes changed
	private void onFilterChanged(){
		//remove all items in the prefabsPane 
		prefabsPane.removeAll();
		
		for(Prefab P : GOBase.prefabsBase){
			if(selectCategoryJCB.getItemCount()!=0&&P.getCategory().getName().equals(selectCategoryJCB.getSelectedItem().toString()))
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
		showPrefabInfo(null);
		//ConstructorWindow.instance.workspace.repaintLevel();
	}
	
	public void selectItem(PrefabPreviewItem ppi){
		ppi.setBorder(BorderFactory.createLineBorder(Color.GREEN,3));
		Globals.toolBox.insertionTool.invoke(ppi.prefab);
		showPrefabInfo(ppi.prefab);
		selectedItem = ppi;
		ppi.repaint();
	}
	
	public void showPrefabInfo(Prefab prefab){
		if(prefab==null){
			inspectorPanel.hideInfo();
			previewPanel.hideImage();
		}
		else{
			inspectorPanel.showInfo(prefab);
			previewPanel.setImage(prefab.getTexture());
		}
	}
	
	public class InspectorPanel extends JPanel{

		private static final long serialVersionUID = 7043675609601172072L;
		
		private JTable prefabInfoTable;
		private DefaultTableModel tableModel;
		private JScrollPane tableContainer;
		
		public InspectorPanel(){
			setPreferredSize(new Dimension(300,120));
			setSize(getPreferredSize());
			
			tableModel = new DefaultTableModel();
			tableModel.addColumn("Parameter");
			tableModel.addColumn("Value");
			prefabInfoTable = new JTable(tableModel);
			
			tableContainer= new JScrollPane(prefabInfoTable);
			tableContainer.setPreferredSize(getPreferredSize());
			tableContainer.setBorder(BorderFactory.createEmptyBorder());
			
			this.add(tableContainer);
			//at start, preview panel is empty
			this.hideInfo();
		}
		
		//fill the table model
		public void showInfo(Prefab p){
			hideInfo();
			tableModel.addRow(new Object[]{"Prefab ID ",p.getPrefabID()});
			tableModel.addRow(new Object[]{"Category ID ",p.getCategoryID()});
			tableModel.addRow(new Object[]{"Tiled width ",p.getTiledWidth()});
			tableModel.addRow(new Object[]{"Tiled height ",p.getTiledHeight()});
			tableModel.addRow(new Object[]{"Texture ",p.getTextureAddress()});
			for(AdditiveAttribute a : p.getAdditiveAttributes())
				tableModel.addRow(new Object[]{a.getAttributeName(),a.getAttributeValue()});
		}
	
		//clear the table model
		public void hideInfo(){
			for(int i=tableModel.getRowCount()-1;i>=0;i--)
				tableModel.removeRow(i);
		}
		
	}
	
	//a class for an item in the prefabsPane 
	public class PrefabPreviewItem extends JButton{

		private static final long serialVersionUID = -4678692180864899020L;

		//tile, to which it linked to
		private Prefab prefab;
		
		//minimized image, which will fit to the size of item
		private BufferedImage resizedImage;
		
		public PrefabPreviewItem(Prefab prefab){
			this.prefab = prefab;
			setPreferredSize(new Dimension(88,95));
			setContentAreaFilled(false);
			//when the filter is changed, prefabsPane generates new array TilePreviewItem which will suit the filter
			//in this case, selected tile must be created with border and a link must change
			
			if(Globals.toolBox.insertionTool.isActive()&&this.prefab==Globals.toolBox.insertionTool.getPrefab()){
				setBorder(BorderFactory.createLineBorder(Color.GREEN,3));
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
			inspectorPanel.showInfo(prefab);
			previewPanel.setImage(prefab.getTexture());
		}
		
		public void onMouseOut(){
			if(this.prefab!=Globals.toolBox.insertionTool.getPrefab())
				this.setBorder(BorderFactory.createEmptyBorder());
			if(Globals.toolBox.insertionTool.isActive())
				showPrefabInfo(Globals.toolBox.insertionTool.getPrefab());
			else
				showPrefabInfo(null);
		}
		
		public void onMouseClick(){
			if(this.prefab==Globals.toolBox.insertionTool.getPrefab()){
				removeSelection();
				setBorder(BorderFactory.createLineBorder(Color.GREEN,1));
				inspectorPanel.showInfo(prefab);
				previewPanel.setImage(prefab.getTexture());
				
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
			inspectorPanel.hideInfo();
			previewPanel.hideImage();
		}
		else{
			vbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			prefabsPane.removeAll();
			onFilterChanged();
		}
	}
	
}
