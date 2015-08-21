import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class GOManagerWindow extends JDialog{
	
	private final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN,11);
	private final Font INDEX_FONT = new Font("Calibri", Font.BOLD, 26);
	private final Font PARAMETER_FONT = new Font("Calibri", Font.PLAIN,13);
	
	private GOBase goBase = ConstructorWindow.instance.globals.goBase;
	private XMLConverter xmlConverter = ConstructorWindow.instance.globals.xmlConverter;
	
	//if true, all the textboxes are editable, Cancel and Save button appears, Edit button is hided
	private boolean editMode = false;
	
	//if true, there is more than one selection in prefabs list. Prefab attributes are hidden
	private boolean multiplySelection = false;
	
	private Prefab currentlyShowedPrefab;
	
	private JList prefabsList;
	
	private JPanel prefabParametersJP;
	private JTextField indexJTF;
	private JButton editJB;
	private JButton deleteJB;
	private JLabel selectedItemsJL;
	
	private PreviewPanel displayPrefabJP;
	
	public GOManagerWindow(){
		super(ConstructorWindow.instance, "Game Objects Manager");
		//blocks access to the main window
		this.setModal(true);
		setSize(500,500);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		add(generateContent());
		setJMenuBar(generateMenuBar());
		if(goBase.prefabsBase.size()!=0)
			showPrefabAttributes(goBase.prefabsBase.get(0).getPrefabID());
		
		setVisible(true);
	}
	
	private JPanel generateContent(){
		JPanel panel = new JPanel();
		SpringLayout slayout1 = new SpringLayout();
		
		panel.setLayout(slayout1);
		
		prefabsList = new JList<String>(goBase.prefabsBase.getIDCollection());
		prefabsList.setSelectedIndex(0);
		prefabsList.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(!multiplySelection && prefabsList.getSelectedValue().toString()!=currentlyShowedPrefab.getPrefabID())
					showPrefabAttributes(prefabsList.getSelectedValue().toString());
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(!editMode){
					if(prefabsList.getSelectedIndices().length>1)
						setMultiplySelection(true);
					else
						setMultiplySelection(false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		prefabsList.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				onMouseMoved(e);
			}
			
		});
		
		JScrollPane prefabsScrollPane = new JScrollPane(prefabsList);
		prefabsScrollPane.setPreferredSize(new Dimension(180, 448));
		prefabsScrollPane.setHorizontalScrollBarPolicy(prefabsScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//Generating panel of chosen prefab parameters
		prefabParametersJP = new JPanel();
		prefabParametersJP.setPreferredSize(new Dimension(this.getWidth()-185,448));
		prefabParametersJP.setBorder(BorderFactory.createBevelBorder(1));
		SpringLayout slayout2 = new SpringLayout();
		prefabParametersJP.setLayout(slayout2);
		
		//components, displayed when only one item is selected 
		indexJTF = new JTextField("Index");
		indexJTF.setFont(INDEX_FONT);
		indexJTF.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,60));
		indexJTF.setHorizontalAlignment(JTextField.CENTER);
		indexJTF.setEnabled(false);
		
		//creating display panel
		displayPrefabJP = new PreviewPanel();
		displayPrefabJP.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,358));
		
		editJB = new JButton("Edit");
		editJB.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width/2,25));
		editJB.setFont(DEFAULT_FONT);
		editJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onEditClicked();
			}
		});
		
		deleteJB = new JButton("Delete");
		deleteJB.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width/2,25));
		deleteJB.setFont(DEFAULT_FONT);
		deleteJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onDeleteClicked();
			}
		});
		
		selectedItemsJL = new JLabel("Selected items");
		selectedItemsJL.setFont(DEFAULT_FONT);
		
		//adding components to the panel
		
		prefabParametersJP.add(indexJTF);
		slayout2.putConstraint(SpringLayout.NORTH, displayPrefabJP, 
				0,SpringLayout.SOUTH, indexJTF);
		prefabParametersJP.add(displayPrefabJP);
		slayout2.putConstraint(SpringLayout.NORTH, deleteJB, 
				1,SpringLayout.SOUTH, displayPrefabJP);
		prefabParametersJP.add(deleteJB);
		slayout2.putConstraint(SpringLayout.NORTH, editJB, 
				1,SpringLayout.SOUTH, displayPrefabJP);
		slayout2.putConstraint(SpringLayout.EAST, editJB, 
				0,SpringLayout.EAST, displayPrefabJP);
		prefabParametersJP.add(editJB);
		
		prefabParametersJP.add(selectedItemsJL);
		
		//Adding components to the main panel, positioning them in relation to each other  
		panel.add(prefabsScrollPane);
		slayout1.putConstraint(SpringLayout.WEST, prefabParametersJP,
                0, SpringLayout.EAST, prefabsScrollPane);
		panel.add(prefabParametersJP);
		
		return panel;
	}
	
	private JMenuBar generateMenuBar(){
		JMenuBar menu = new JMenuBar();
		
		JMenu newJM = new JMenu("New");
		newJM.setFont(DEFAULT_FONT);
		
		JMenuItem prefabJMI = new JMenuItem("Prefab");
		prefabJMI.setFont(DEFAULT_FONT);
		prefabJMI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PrefabManagerWindow();
			}
		});
		
		JMenuItem particleJMI = new JMenuItem("Particle");
		particleJMI.setFont(DEFAULT_FONT);
		
		JMenuItem backgroundJMI = new JMenuItem("Background");
		backgroundJMI.setFont(DEFAULT_FONT);
		
		JMenuItem categoryManagerJMI = new JMenuItem("Category manager");
		categoryManagerJMI.setFont(DEFAULT_FONT);
		
		
		JSeparator separator = new JSeparator();
		JLabel showCategoryJL = new JLabel("Show category: ");
		showCategoryJL.setFont(DEFAULT_FONT);
		JComboBox categoryJCB = new JComboBox();
		categoryJCB.setFont(DEFAULT_FONT);
		
		newJM.add(prefabJMI);
		newJM.add(particleJMI);
		newJM.add(backgroundJMI);
		menu.add(newJM);
		menu.add(categoryManagerJMI);
		menu.add(separator);
		menu.add(showCategoryJL);
		menu.add(categoryJCB);
		
		return menu;
	}
	
	private void showPrefabAttributes(String prefabID){
		Prefab prefab = goBase.prefabsBase.get(prefabID);
		currentlyShowedPrefab=prefab;
		indexJTF.setText(prefab.getPrefabID());
		displayPrefabJP.setImage(prefab.getTexture());
		
		if(prefabsList.getSelectedValue()==prefab.getPrefabID()){
			if(!deleteJB.isEnabled())deleteJB.setEnabled(true);
			if(!editJB.isEnabled())editJB.setEnabled(true);
		}
		else{
			if(deleteJB.isEnabled())deleteJB.setEnabled(false);
			if(editJB.isEnabled())editJB.setEnabled(false);
		}
	}
	
	public void setMultiplySelection(boolean state){
		multiplySelection = state;
		if(multiplySelection){
			for(Component c : prefabParametersJP.getComponents())
				c.setVisible(false);
			selectedItemsJL.setVisible(true);
			selectedItemsJL.setText("Selected "+prefabsList.getSelectedIndices().length+" items");
		}
		else{
			for(Component c : prefabParametersJP.getComponents())
				c.setVisible(true);
			selectedItemsJL.setVisible(false);
			String selectedID = prefabsList.getSelectedValue().toString();
			if(selectedID!=currentlyShowedPrefab.getPrefabID())
				showPrefabAttributes(selectedID);
			
			if(!deleteJB.isEnabled())deleteJB.setEnabled(true);
			if(!editJB.isEnabled())editJB.setEnabled(true);
		}
	}
	
	public void onEditClicked(){
		
	}
	
	public void onDeleteClicked(){
		
	}
	
	public void onMouseMoved(MouseEvent e){
		if(!editMode && !multiplySelection){
			String[] collection = goBase.prefabsBase.getIDCollection();
			int index = prefabsList.locationToIndex(e.getPoint());
			if(collection[index]!=currentlyShowedPrefab.getPrefabID())
				showPrefabAttributes(collection[index]);
		}
	}
	
	public class PreviewPanel extends JPanel{
		
		//original image of selected tile
		private BufferedImage image;
		
		private BufferedImage resizedImage;
		
		public PreviewPanel(){
			this.setBackground(Color.BLACK);
			//at start, preview panel is empty
			this.hideImage();
		}
		
		//set image to preview
		public void setImage(BufferedImage image){
			this.image=resizeImage(image);
			repaint();
		}
		
		private BufferedImage resizeImage(BufferedImage originalImage){
			int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
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
	
}
