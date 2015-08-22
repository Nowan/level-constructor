import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class CategoryManagerWindow extends JDialog{
	
	public static final int PREFAB_TYPE = 0;
	public static final int PARTICLE_TYPE = 1;
	public static final int BACKGROUND_TYPE = 2;
	
	private final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN,11);
	private final Font PARAMETER_FONT = new Font("Calibri", Font.PLAIN,13);
	
	private GOBase goBase = ConstructorWindow.instance.globals.goBase;
	
	private PrefabCategory currentlySelectedCategory;
	
	//contains category data for editing
	private PrefabCategory sandboxCategory;
	
	private JList<String> categoryList;
	private JButton editJB;
	private JButton removeJB;
	private JButton addJB;
	private JPanel attributesJP;
	private JScrollPane attributesJSP;
	private JTextField idJTF;
	private JTextField nameJTF;
	private JCheckBox isObstacleJChB;
	private JPanel additiveAttributesJP;
	
	private boolean editMode=false;
	
	public CategoryManagerWindow(int type){
		super(ConstructorWindow.instance, "Category manager");
		//blocks access to the main window
		this.setModal(true);
		setSize(450,300);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		switch(type){
		case PREFAB_TYPE:
			add(generatePrefabContent());
			if(goBase.prefabCategoryBase.size()!=0)
				showCategoryInfo(currentlySelectedCategory);
			break;
		case PARTICLE_TYPE:
			break;
		case BACKGROUND_TYPE:
			break;
		}
		
		setEditMode(false);
		setVisible(true);
	}
	
	private JPanel generatePrefabContent(){
		JPanel panel = new JPanel();
		SpringLayout slayout = new SpringLayout();
		panel.setLayout(slayout);
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for(PrefabCategory p : goBase.prefabCategoryBase)
			listModel.addElement(p.getName());
		categoryList = new JList<String>(listModel);
		if(listModel.size()!=0){
		categoryList.setSelectedIndex(0);
		currentlySelectedCategory = goBase.prefabCategoryBase.get(0);
		}
		categoryList.setPreferredSize(new Dimension(165,this.getHeight()-50));
		categoryList.setFont(DEFAULT_FONT);
		categoryList.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				currentlySelectedCategory = goBase.prefabCategoryBase.get(categoryList.getSelectedValue());
				showCategoryInfo(currentlySelectedCategory);
			}

			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		
		removeJB = new JButton("-");
		removeJB.setContentAreaFilled(false);
		removeJB.setFont(DEFAULT_FONT);
		removeJB.setPreferredSize(new Dimension(50,22));
		if(goBase.prefabCategoryBase.size()==0)
			removeJB.setEnabled(false);
		removeJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//if editMode is on, the button "-" changes to "Decline", which removes all
				//the changes made in sandboxCategory and returns to the original PrefabCategory
				if(editMode){
					showCategoryInfo(currentlySelectedCategory);
					sandboxCategory=null;
					setEditMode(false);
				}
				//usual action - remove selected object from the categoryList
				else{
					new JOptionPane();
					int n = JOptionPane.showConfirmDialog(
		    			  	ConstructorWindow.instance,
		    			    "Are you sure you want to delete this category? All objects attached to it will be destroyed",
		    			    "Message",
		    			    JOptionPane.YES_NO_OPTION);
					if(n==0){
					//number of prefabs of the deleted category
					int pcn=0;
					for(int i=0;i<goBase.prefabsBase.size();i++)
						if(goBase.prefabsBase.get(i).getCategoryID().contains(currentlySelectedCategory.getID())){
							goBase.prefabsBase.remove(i);
							pcn++;
						}
					//if any prefabs were deleted - save changes to prefabsbase.xml & refresh goManager
					if(pcn>0){
						System.out.println(pcn);
						ConstructorWindow.instance.globals.xmlConverter.savePrefabBase();
						ConstructorWindow.instance.goManager.refresh();
						}
					//if category list will become empty after deleting this object, disable remove btn and select nothing
					if(listModel.size()==1){
							editJB.setEnabled(false);
							removeJB.setEnabled(false);
							attributesJSP.setVisible(false);
							listModel.remove(categoryList.getSelectedIndex());
							goBase.prefabCategoryBase.remove(currentlySelectedCategory);
						}
					else{
					//making decision, which category select next after deleting current
					if(categoryList.getSelectedIndex()!=0){
						goBase.prefabCategoryBase.remove(currentlySelectedCategory);
						categoryList.setSelectedIndex(categoryList.getSelectedIndex()-1);
						listModel.remove(categoryList.getSelectedIndex()+1);
					}
					else{
						goBase.prefabCategoryBase.remove(currentlySelectedCategory);
						categoryList.setSelectedIndex(categoryList.getSelectedIndex()+1);
						listModel.remove(categoryList.getSelectedIndex()-1);
					}
					currentlySelectedCategory=goBase.prefabCategoryBase.get(categoryList.getSelectedValue());
					showCategoryInfo(currentlySelectedCategory);
					}
					
					//saving changes to prefabcategorybase.xml
					ConstructorWindow.instance.globals.xmlConverter.savePrefabCategoryBase();
					}
				}
				
			}
		});
		
		editJB = new JButton("Edit");
		editJB.setContentAreaFilled(false);
		editJB.setFont(DEFAULT_FONT);
		editJB.setPreferredSize(new Dimension(65,22));
		if(goBase.prefabCategoryBase.size()==0)
			editJB.setEnabled(false);
		editJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//create sandboxCategory, on which all the changes will apply
				sandboxCategory = new PrefabCategory(currentlySelectedCategory.getID(),currentlySelectedCategory.getName(),
						currentlySelectedCategory.getObstacleBit(),currentlySelectedCategory.getAdditiveAttributes());
				//make certain, that the user operates in sandbox
				showCategoryInfo(sandboxCategory);
				//change the state of editMode
				setEditMode(!editMode);
			}
		});
		
		addJB = new JButton("+");
		addJB.setContentAreaFilled(false);
		addJB.setFont(DEFAULT_FONT);
		addJB.setPreferredSize(new Dimension(50,22));
		addJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//if editMode is on, the button "+" changes to "Save", which saves all
				//the changes made in sandboxCategory to the original PrefabCategory and xml file
				if(editMode){
					currentlySelectedCategory.setID(sandboxCategory.getID());
					currentlySelectedCategory.setName(sandboxCategory.getName());
					currentlySelectedCategory.setObstacleBit(sandboxCategory.getObstacleBit());
					currentlySelectedCategory.setAdditiveAttributes(sandboxCategory.getAdditiveAttributes());
					showCategoryInfo(currentlySelectedCategory);
					sandboxCategory=null;
					setEditMode(false);
					
					//saving changes to prefabcategorybase.xml
					ConstructorWindow.instance.globals.xmlConverter.savePrefabCategoryBase();
					
					listModel.removeAllElements();
					for(PrefabCategory p : goBase.prefabCategoryBase)
						listModel.addElement(p.getName());
				}
				//usual action - add new object to the categoryList
				else{
					//generating counter that counts the number of new categories 
					int c = 0;
					for(int i=0;i<listModel.size();i++)
						if(listModel.getElementAt(i).toString().contains("new_category"))
							c++;
					PrefabCategory category = new PrefabCategory("nc"+c,"new_category_"+c,false,null);
					goBase.prefabCategoryBase.add(category);
					listModel.addElement(category.getName());
					categoryList.setSelectedIndex(listModel.getSize()-1);
					currentlySelectedCategory=category;
					showCategoryInfo(currentlySelectedCategory);
					
					if(!removeJB.isEnabled())
						removeJB.setEnabled(true);
					if(!editJB.isEnabled())
						editJB.setEnabled(true);
					if(!attributesJSP.isVisible())
						attributesJSP.setVisible(true);
					
					//saving changes to prefabcategorybase.xml
					ConstructorWindow.instance.globals.xmlConverter.savePrefabCategoryBase();
				}
			}
		});
		
		attributesJP = new JPanel();
		SpringLayout slayout2 = new SpringLayout();
		attributesJP.setLayout(slayout2);
		
		attributesJSP = new JScrollPane(attributesJP);
		attributesJSP.setBorder(BorderFactory.createTitledBorder("Attributes"));
		attributesJSP.setPreferredSize(new Dimension(this.getWidth()-categoryList.getPreferredSize().width - 10,this.getHeight()-30));
		if(goBase.prefabCategoryBase.size()==0)
			attributesJSP.setVisible(false);
		
		//generating attributes panel components
		JLabel idJL = new JLabel("ID:");
		idJL.setFont(PARAMETER_FONT);
		
		idJTF = new JTextField();
		idJTF.setPreferredSize(new Dimension(100,22));
		idJTF.setHorizontalAlignment(JTextField.CENTER);
		idJTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {
				sandboxCategory.setID(idJTF.getText());
			}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		JLabel nameJL = new JLabel("Name:");
		nameJL.setFont(PARAMETER_FONT);
		
		nameJTF = new JTextField();
		nameJTF.setPreferredSize(new Dimension(100,22));
		nameJTF.setHorizontalAlignment(JTextField.CENTER);
		nameJTF.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {
				sandboxCategory.setName(nameJTF.getText());
			}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		
		JLabel isObstacleJL = new JLabel("Is obstacle:");
		isObstacleJL.setFont(PARAMETER_FONT);
		
		isObstacleJChB = new JCheckBox();
		isObstacleJChB.setPreferredSize(new Dimension(100,22));
		isObstacleJChB.setHorizontalAlignment(JCheckBox.CENTER);
		isObstacleJChB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				sandboxCategory.setObstacleBit(isObstacleJChB.isSelected());
			}
		});
		
		additiveAttributesJP = new JPanel();
		additiveAttributesJP.setLayout(new BoxLayout(additiveAttributesJP, BoxLayout.Y_AXIS));
		
		//add components to attributes panel
		slayout2.putConstraint(SpringLayout.NORTH, idJL, 
				5,SpringLayout.NORTH, attributesJP);
		slayout2.putConstraint(SpringLayout.EAST, idJL, 
				-20,SpringLayout.HORIZONTAL_CENTER, attributesJP);
		attributesJP.add(idJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, idJTF, 
				0,SpringLayout.VERTICAL_CENTER, idJL);
		slayout2.putConstraint(SpringLayout.WEST, idJTF, 
				10,SpringLayout.EAST, idJL);
		attributesJP.add(idJTF);
		
		slayout2.putConstraint(SpringLayout.NORTH, nameJL, 
				8,SpringLayout.SOUTH, idJL);
		slayout2.putConstraint(SpringLayout.EAST, nameJL, 
				0,SpringLayout.EAST, idJL);
		attributesJP.add(nameJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, nameJTF, 
				0,SpringLayout.VERTICAL_CENTER, nameJL);
		slayout2.putConstraint(SpringLayout.WEST, nameJTF, 
				10,SpringLayout.EAST, nameJL);
		attributesJP.add(nameJTF);
		
		slayout2.putConstraint(SpringLayout.NORTH, isObstacleJL, 
				8,SpringLayout.SOUTH, nameJL);
		slayout2.putConstraint(SpringLayout.EAST, isObstacleJL, 
				0,SpringLayout.EAST, idJL);
		attributesJP.add(isObstacleJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, isObstacleJChB, 
				0,SpringLayout.VERTICAL_CENTER, isObstacleJL);
		slayout2.putConstraint(SpringLayout.WEST, isObstacleJChB, 
				10,SpringLayout.EAST, isObstacleJL);
		attributesJP.add(isObstacleJChB);
		
		slayout2.putConstraint(SpringLayout.NORTH, additiveAttributesJP, 
				8,SpringLayout.SOUTH, isObstacleJL);
		slayout2.putConstraint(SpringLayout.HORIZONTAL_CENTER, additiveAttributesJP, 
				0,SpringLayout.HORIZONTAL_CENTER, attributesJP);
		attributesJP.add(additiveAttributesJP);
		
		//add components to manager window
		panel.add(categoryList);
		
		slayout.putConstraint(SpringLayout.NORTH, removeJB, 
				0,SpringLayout.SOUTH, categoryList);
		slayout.putConstraint(SpringLayout.WEST, removeJB, 
				0,SpringLayout.WEST, categoryList);
		panel.add(removeJB);
		
		slayout.putConstraint(SpringLayout.NORTH, editJB, 
				0,SpringLayout.SOUTH, categoryList);
		slayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, editJB, 
				0,SpringLayout.HORIZONTAL_CENTER, categoryList);
		panel.add(editJB);

		slayout.putConstraint(SpringLayout.NORTH, addJB, 
				0,SpringLayout.SOUTH, categoryList);
		slayout.putConstraint(SpringLayout.EAST, addJB, 
				0,SpringLayout.EAST, categoryList);
		panel.add(addJB);
		
		slayout.putConstraint(SpringLayout.WEST, attributesJSP, 
				3,SpringLayout.EAST, categoryList);
		panel.add(attributesJSP);
		
		return panel;
	}

	private void showCategoryInfo(PrefabCategory category){
		additiveAttributesJP.removeAll();
		
		idJTF.setText(category.getID());
		nameJTF.setText(category.getName());
		isObstacleJChB.setSelected(category.getObstacleBit());
		
		for(AdditiveAttribute a : category.getAdditiveAttributes()){
			JPanel attributeJP = new JPanel();
			
			attributeJP.setPreferredSize(new Dimension(250,25));
			attributeJP.setMaximumSize(attributeJP.getPreferredSize());
			attributeJP.setMinimumSize(attributeJP.getPreferredSize());
			SpringLayout slayout = new SpringLayout();
			attributeJP.setLayout(slayout);
			attributeJP.setEnabled(editMode);
			
			JTextField attributeNameJTF = new JTextField(a.getAttributeName());
			attributeNameJTF.setFont(DEFAULT_FONT);
			attributeNameJTF.setPreferredSize(new Dimension(100,20));
			attributeNameJTF.setHorizontalAlignment(JLabel.CENTER);
			attributeNameJTF.setEnabled(editMode);
			attributeNameJTF.addKeyListener(new KeyListener(){
				@Override
				public void keyPressed(KeyEvent arg0) {}
				@Override
				public void keyReleased(KeyEvent arg0) {
					a.setAttributeName(attributeNameJTF.getText());
				}
				@Override
				public void keyTyped(KeyEvent arg0) {}
			});

			JComboBox<String> attributeTypeJCB = new JComboBox<String>(new String[]{"boolean","integer","double","sString","lString"});
			attributeTypeJCB.setFont(DEFAULT_FONT);
			attributeTypeJCB.setPreferredSize(new Dimension(100,20));
			attributeTypeJCB.setEnabled(editMode);
			switch(a.getAttributeType()){
			case "boolean":
				attributeTypeJCB.setSelectedIndex(0);
				break;
			case "integer":
				attributeTypeJCB.setSelectedIndex(1);
				break;
			case "double":
				attributeTypeJCB.setSelectedIndex(2);
				break;
			case "sString":
				attributeTypeJCB.setSelectedIndex(3);
				break;
			case "lString":
				attributeTypeJCB.setSelectedIndex(4);
				break;
			}
			attributeTypeJCB.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					a.setAttributeType(attributeTypeJCB.getSelectedItem().toString());
				}
			});
			
			JButton rmaaJB = new JButton("-");
			rmaaJB.setContentAreaFilled(false);
			rmaaJB.setFont(DEFAULT_FONT);
			rmaaJB.setPreferredSize(new Dimension(38,20));
			rmaaJB.setEnabled(editMode);
			rmaaJB.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					category.getAdditiveAttributes().remove(a.getInListPosition());
					showCategoryInfo(category);
				}
			});
			
			//--------------------------------------------------------
			slayout.putConstraint(SpringLayout.SOUTH, attributeNameJTF, 
					0,SpringLayout.SOUTH, attributeJP);
			slayout.putConstraint(SpringLayout.WEST, attributeNameJTF, 
					0,SpringLayout.WEST, attributeJP);
			attributeJP.add(attributeNameJTF);
			
			slayout.putConstraint(SpringLayout.VERTICAL_CENTER, attributeTypeJCB, 
					0,SpringLayout.VERTICAL_CENTER, attributeNameJTF);
			slayout.putConstraint(SpringLayout.WEST, attributeTypeJCB, 
					5,SpringLayout.EAST, attributeNameJTF);
			attributeJP.add(attributeTypeJCB);
			
			slayout.putConstraint(SpringLayout.VERTICAL_CENTER, rmaaJB, 
					0,SpringLayout.VERTICAL_CENTER, attributeNameJTF);
			slayout.putConstraint(SpringLayout.WEST, rmaaJB, 
					5,SpringLayout.EAST, attributeTypeJCB);
			attributeJP.add(rmaaJB);
			
			additiveAttributesJP.add(attributeJP);
		}
		
		JButton addaaJB = new JButton("Add attribute");
		addaaJB.setContentAreaFilled(false);
		addaaJB.setFont(DEFAULT_FONT);
		addaaJB.setPreferredSize(new Dimension(180,30));
		addaaJB.setEnabled(editMode);
		addaaJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				category.getAdditiveAttributes().add(new AdditiveAttribute("Name","sString"));
				showCategoryInfo(category);
			}
		});
		
		JPanel attributeJP = new JPanel();
		attributeJP.setPreferredSize(new Dimension(250,35));
		attributeJP.setMaximumSize(attributeJP.getPreferredSize());
		attributeJP.setMinimumSize(attributeJP.getPreferredSize());
		
		attributeJP.add(addaaJB);
		additiveAttributesJP.add(attributeJP);
		
		additiveAttributesJP.revalidate();
		additiveAttributesJP.repaint();
	}

	private void setEditMode(boolean editMode){
		this.editMode=editMode;
		
		categoryList.setEnabled(!editMode);
		editJB.setVisible(!editMode);
		
		if(editMode){
			addJB.setText("Save");
			addJB.setPreferredSize(new Dimension(categoryList.getPreferredSize().width/2,editJB.getPreferredSize().height));
			addJB.setContentAreaFilled(true);
			
			removeJB.setText("Discard");
			removeJB.setPreferredSize(new Dimension(categoryList.getPreferredSize().width/2,editJB.getPreferredSize().height));
			removeJB.setContentAreaFilled(true);
		}
		else{
			addJB.setText("+");
			addJB.setPreferredSize(new Dimension(50,editJB.getPreferredSize().height));
			addJB.setContentAreaFilled(false);
			
			removeJB.setText("-");
			removeJB.setPreferredSize(new Dimension(50,editJB.getPreferredSize().height));
			removeJB.setContentAreaFilled(false);
		}
		for(Component c : attributesJP.getComponents()){
			c.setEnabled(editMode);
		}
		
		for(Component c : additiveAttributesJP.getComponents()){
			if(c.getClass()==JPanel.class)
				for(Component c2 :((JPanel)c).getComponents()){
					c2.setEnabled(editMode);
			}
		}
		
		
	}
}
