import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;

public class GOManagerWindow extends JDialog{
	
	private static final long serialVersionUID = 7508522724431290929L;

	private XMLConverter xmlConverter = Globals.xmlConverter;
	
	//if true, all the textboxes are editable, Cancel and Save button appears, Edit button is hided
	private boolean editMode = false;
	
	//if true, there is more than one selection in prefabs list. Prefab attributes are hidden
	private boolean multiplySelection = false;
	
	private Prefab currentlyShowedPrefab;
	
	private DefaultListModel<String> listModel;
	private JList<String> prefabsList;
	
	private JPanel prefabParametersJP;
	private JTextField indexJTF;
	private JButton editJB;
	private JButton deleteJB;
	
	private JPanel relationJP;
	private JLabel selectedItemsJL;
	private DefaultComboBoxModel<String> selectedPrefabModel;
	JComboBox<String> masterJCB;
	private JButton linkJB;
	
	private PreviewPanel displayPrefabJP;
	
	public GOManagerWindow(){
		super(ConstructorWindow.instance, "Game Objects Manager");
		//blocks access to the main window
		this.setModal(true);
		setSize(500,500);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		add(generateContent());
		setJMenuBar(generateMenuBar());
		showPrefabAttributes(null);
		
		this.addWindowListener(new WindowListener(){
			@Override public void windowActivated(WindowEvent e) {}
			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowOpened(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				prefabsList.clearSelection();
				showPrefabAttributes(null);
				if(multiplySelection) setMultiplySelection(false);
			}
		});
			
		setVisible(false);
	}
	
	private JPanel generateContent(){
		JPanel panel = new JPanel();
		SpringLayout slayout1 = new SpringLayout();
		
		panel.setLayout(slayout1);
		
		listModel = new DefaultListModel<String>();
		for(Prefab p : GOBase.prefabsBase)
			listModel.addElement(p.getPrefabID());
		prefabsList = new JList<String>(listModel);
		prefabsList.addMouseListener(new MouseListener(){
			@Override public void mouseClicked(MouseEvent arg0) {}

			@Override public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if(!multiplySelection && currentlyShowedPrefab!=null){
					if(!prefabsList.isSelectionEmpty()&&prefabsList.getSelectedValue().toString()!=currentlyShowedPrefab.getPrefabID())
						showPrefabAttributes(prefabsList.getSelectedValue().toString());
					else if(prefabsList.isSelectionEmpty())
						showPrefabAttributes(null);
					}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON1&&!editMode){
					if(prefabsList.getSelectedIndices().length>1)
						setMultiplySelection(true);
					else
						setMultiplySelection(false);
				}
				else if(arg0.getButton()==MouseEvent.BUTTON3){
					prefabsList.clearSelection();
					showPrefabAttributes(null);
					if(multiplySelection) setMultiplySelection(false);
				}
			}
			
			@Override public void mouseReleased(MouseEvent arg0) {}
		});
		prefabsList.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent e) {}

			@Override
			public void mouseMoved(MouseEvent e) { onMouseMoved(e); }
		});
		
		JScrollPane prefabsScrollPane = new JScrollPane(prefabsList);
		prefabsScrollPane.setPreferredSize(new Dimension(180, 448));
		prefabsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		//Generating panel, which will display prefab parameters
		prefabParametersJP = new JPanel();
		prefabParametersJP.setPreferredSize(new Dimension(this.getWidth()-185,448));
		prefabParametersJP.setBorder(BorderFactory.createBevelBorder(1));
		SpringLayout slayout2 = new SpringLayout();
		prefabParametersJP.setLayout(slayout2);
		
		//components, displayed when only one item is selected 
		indexJTF = new JTextField("Index");
		indexJTF.setFont(Globals.INDEX_FONT);
		indexJTF.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,60));
		indexJTF.setHorizontalAlignment(JTextField.CENTER);
		indexJTF.setEnabled(false);
		
		//creating display panel
		displayPrefabJP = new PreviewPanel();
		displayPrefabJP.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,358));
		
		editJB = new JButton("Edit");
		editJB.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width/2,25));
		editJB.setFont(Globals.DEFAULT_FONT);
		editJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onEditClicked();
			}
		});
		
		deleteJB = new JButton("Delete");
		deleteJB.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width/2,25));
		deleteJB.setFont(Globals.DEFAULT_FONT);
		deleteJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onDeleteClicked();
			}
		});
		
		//Generating panel, which will enable prefab grouping
		//It will be displayed only when more than one prefab is selected
		//(that's it, because it will group only the selected objects)
		relationJP = new JPanel();
		relationJP.setPreferredSize(new Dimension(228,120));
		relationJP.setBorder(BorderFactory.createEtchedBorder());
		SpringLayout relationSL = new SpringLayout();
		relationJP.setLayout(relationSL);
		
		selectedItemsJL = new JLabel("N items selected");
		//selectedItemsJL.setFont(Globals.PARAMETER_FONT);
		
		JLabel masterJL = new JLabel("Master: ");
		masterJL.setFont(Globals.DEFAULT_FONT);
		
		selectedPrefabModel = new DefaultComboBoxModel<String>();
		masterJCB = new JComboBox<String>(selectedPrefabModel);
		masterJCB.setPreferredSize(new Dimension(150,20));
		masterJCB.setFont(Globals.DEFAULT_FONT);
		
		linkJB = new JButton("Link");
		linkJB.setPreferredSize(new Dimension(221,60));
		linkJB.setFont(Globals.INDEX_FONT);
		linkJB.setContentAreaFilled(false);
		linkJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onLinkClicked();
			}
		});
		
		relationSL.putConstraint(SpringLayout.NORTH, selectedItemsJL, 
				6,SpringLayout.NORTH, relationJP);
		relationSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, selectedItemsJL, 
				0,SpringLayout.HORIZONTAL_CENTER, relationJP);
		relationJP.add(selectedItemsJL);
		
		relationSL.putConstraint(SpringLayout.NORTH, masterJL, 
				30,SpringLayout.NORTH, relationJP);
		relationSL.putConstraint(SpringLayout.WEST, masterJL, 
				12,SpringLayout.WEST, relationJP);
		relationJP.add(masterJL);
		
		relationSL.putConstraint(SpringLayout.VERTICAL_CENTER, masterJCB, 
				0,SpringLayout.VERTICAL_CENTER, masterJL);
		relationSL.putConstraint(SpringLayout.WEST, masterJCB, 
				0,SpringLayout.EAST, masterJL);
		relationJP.add(masterJCB);
		
		relationSL.putConstraint(SpringLayout.SOUTH, linkJB, 
				-2,SpringLayout.SOUTH, relationJP);
		relationSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, linkJB, 
				0,SpringLayout.HORIZONTAL_CENTER, relationJP);
		relationJP.add(linkJB);
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
		
		//prefabParametersJP.add(selectedItemsJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, relationJP, 
				-20, SpringLayout.VERTICAL_CENTER, prefabParametersJP);
		slayout2.putConstraint(SpringLayout.HORIZONTAL_CENTER, relationJP, 
				0, SpringLayout.HORIZONTAL_CENTER, prefabParametersJP);
		prefabParametersJP.add(relationJP);
		
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
		newJM.setFont(Globals.DEFAULT_FONT);
		
		JMenuItem prefabJMI = new JMenuItem("Prefab");
		prefabJMI.setFont(Globals.DEFAULT_FONT);
		prefabJMI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PrefabManagerWindow();
			}
		});

		JMenuItem particleJMI = new JMenuItem("Particle");
		particleJMI.setFont(Globals.DEFAULT_FONT);
		
		JMenuItem backgroundJMI = new JMenuItem("Background");
		backgroundJMI.setFont(Globals.DEFAULT_FONT);
		
		JMenu manageJM = new JMenu("Manage");
		manageJM.setFont(Globals.DEFAULT_FONT);
		
		JMenuItem categoryManagerJMI = new JMenuItem("Categories");
		categoryManagerJMI.setFont(Globals.DEFAULT_FONT);
		categoryManagerJMI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new CategoryManagerWindow(CategoryManagerWindow.PREFAB_TYPE);
			}
		});
		
		JMenuItem assetManagerJMI = new JMenuItem("Assets");
		assetManagerJMI.setFont(Globals.DEFAULT_FONT);
		assetManagerJMI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AssetManagerWindow();
			}
		});
		
		JSeparator separator = new JSeparator();
		JLabel showCategoryJL = new JLabel("Show category: ");
		showCategoryJL.setFont(Globals.DEFAULT_FONT);
		JComboBox<String> categoryJCB = new JComboBox<String>();
		categoryJCB.setFont(Globals.DEFAULT_FONT);
		
		newJM.add(prefabJMI);
		newJM.add(particleJMI);
		newJM.add(backgroundJMI);
		menu.add(newJM);
		manageJM.add(categoryManagerJMI);
		manageJM.add(assetManagerJMI);
		menu.add(manageJM);
		menu.add(separator);
		menu.add(showCategoryJL);
		menu.add(categoryJCB);
		
		return menu;
	}
	
	private void showPrefabAttributes(String prefabID){
		if(prefabID==null){
			currentlyShowedPrefab=null;
			indexJTF.setText("index");
			displayPrefabJP.hideImage();
			if(deleteJB.isEnabled())deleteJB.setEnabled(false);
			if(editJB.isEnabled())editJB.setEnabled(false);
		}
		else{
			currentlyShowedPrefab=GOBase.prefabsBase.get(prefabID);
			indexJTF.setText(currentlyShowedPrefab.getPrefabID());
			displayPrefabJP.setImage(currentlyShowedPrefab.getTexture());
			
			if(prefabsList.getSelectedValue()==currentlyShowedPrefab.getPrefabID()){
				if(!deleteJB.isEnabled())deleteJB.setEnabled(true);
				if(!editJB.isEnabled())editJB.setEnabled(true);
			}
			else{
				if(deleteJB.isEnabled())deleteJB.setEnabled(false);
				if(editJB.isEnabled())editJB.setEnabled(false);
			}
		}
	}
	
	private void setMultiplySelection(boolean state){
		multiplySelection = state;
		if(multiplySelection){
			for(Component c : prefabParametersJP.getComponents())
				c.setVisible(false);
			relationJP.setVisible(true);
			for(Component c : relationJP.getComponents())
				c.setVisible(true);
			selectedItemsJL.setText(prefabsList.getSelectedIndices().length+" prefabs selected");
			selectedPrefabModel.removeAllElements();
			int linkedPrefabsCount = 0;
			for(String s : prefabsList.getSelectedValuesList()){
				selectedPrefabModel.addElement(s);
				if(GOBase.prefabsBase.get(s).isComplex())
					linkedPrefabsCount++;
			}
			if(linkedPrefabsCount==selectedPrefabModel.getSize()){
				masterJCB.setEnabled(false);
				linkJB.setText("Unlink");
				
			}
			else{
				masterJCB.setEnabled(true);
				linkJB.setText("Link");
			}
		}
		else{
			for(Component c : prefabParametersJP.getComponents())
				c.setVisible(true);
			relationJP.setVisible(false);
			for(Component c : relationJP.getComponents())
				c.setVisible(false);
			if(!prefabsList.isSelectionEmpty()){
				String selectedID = prefabsList.getSelectedValue().toString();
			if(currentlyShowedPrefab!=null && selectedID!=currentlyShowedPrefab.getPrefabID())
				showPrefabAttributes(selectedID);
			if(!deleteJB.isEnabled())deleteJB.setEnabled(true);
			if(!editJB.isEnabled())editJB.setEnabled(true);
			}
			else
				showPrefabAttributes(null);
			
		}
	}
	
	private void onLinkClicked(){
		if(linkJB.getText().equals("Link")){
			//Starting from master element
			Prefab masterPrefab = GOBase.prefabsBase.get(masterJCB.getSelectedItem().toString());
			for(int i=0; i<masterJCB.getItemCount();i++)
				if(i!=masterJCB.getSelectedIndex()){
					Prefab slavePrefab = GOBase.prefabsBase.get(masterJCB.getItemAt(i));
					masterPrefab.setSlavePrefab(slavePrefab);
					slavePrefab.setMasterPrefab(masterPrefab);
					masterPrefab = slavePrefab;
				}
			
			linkJB.setText("Unlink");
			masterJCB.setEnabled(false);
		}
		else if(linkJB.getText().equals("Unlink")){
			//Starting from master element
			Prefab prefab = GOBase.prefabsBase.get(masterJCB.getSelectedItem().toString());
			//moving to the slave elements and "setting them free" from their masters
			while(prefab.isComplex()){
				prefab = prefab.getSlavePrefab();
				prefab.getMasterPrefab().setSlavePrefab(null);
				prefab.setMasterPrefab(null);
			}
			
			linkJB.setText("Link");
			masterJCB.setEnabled(true);
		}
	}
	
	private void onEditClicked(){
		new PrefabManagerWindow(currentlyShowedPrefab);
	}
	
	private void onDeleteClicked(){
		new JOptionPane();
		int n = JOptionPane.showConfirmDialog(
			  	ConstructorWindow.instance,
			    "Are you sure you want to delete this object? All data will be lost",
			    "Message",
			    JOptionPane.YES_NO_OPTION);
		if(n==0){
			GOBase.prefabsBase.remove(currentlyShowedPrefab);
			xmlConverter.savePrefabBase();
			ConstructorWindow.instance.collectionsPanel.tilesTab.refreshPrefabPanel();
			refresh();
		}
	}
	
	private void onMouseMoved(MouseEvent e){
		if(!editMode && !multiplySelection && GOBase.prefabsBase.size()!=0){
			String[] collection = GOBase.prefabsBase.getIDCollection();
			int index = prefabsList.locationToIndex(e.getPoint());
			if(currentlyShowedPrefab!=null&&collection[index]==currentlyShowedPrefab.getPrefabID())
				return;
			showPrefabAttributes(collection[index]);
		}
	}
	
	public void refresh(){
		if(GOBase.prefabsBase.size()>0){
			if(prefabsList.isSelectionEmpty()){
				listModel.removeAllElements();
				for(Prefab p : GOBase.prefabsBase)
					listModel.addElement(p.getPrefabID());
			}
			else{
				int selectedIndex = prefabsList.getSelectedIndex();
				listModel.removeAllElements();
				for(Prefab p : GOBase.prefabsBase)
					listModel.addElement(p.getPrefabID());
				if(selectedIndex<listModel.size())
					prefabsList.setSelectedIndex(selectedIndex);
				else
					prefabsList.setSelectedIndex(listModel.size()-1);
				showPrefabAttributes((GOBase.prefabsBase.get(prefabsList.getSelectedValue().toString()).getPrefabID()));
			}
		}
	}
	
}
