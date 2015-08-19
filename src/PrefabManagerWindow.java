import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class PrefabManagerWindow  extends JDialog{
	
	private PrefabManagerWindow mainLink = this;
	private final Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN,11);
	private final Font INDEX_FONT = new Font("Calibri", Font.BOLD, 26);
	private final Font PARAMETER_FONT = new Font("Calibri", Font.PLAIN,13);
	
	private GOBase goBase = ConstructorWindow.instance.globals.goBase;
	private XMLConverter xmlConverter = ConstructorWindow.instance.globals.xmlConverter;
	
	private JTextField indexJTF;
	private JComboBox categoryJCB;
	private INTTextField tiledWidthJTF;
	private INTTextField tiledHeightJTF;
	private JTextField textureJTF;
	private JScrollPane descriptionJSP;
	private JTextArea descriptionJTA;
	private JButton browseLocationJB;
	private JPanel additiveAttributesPanel;
	
	public PrefabManagerWindow(){
		super(ConstructorWindow.instance, "Prefab manager");
		//blocks access to the main window
		this.setModal(true);
		setSize(303,490);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		add(generateNewPrefabContent());
		setVisible(true);
	}
	
	private JPanel generateNewPrefabContent(){
		JPanel prefabParametersJP = new JPanel();
		prefabParametersJP.setPreferredSize(new Dimension(this.getWidth(),270));
		SpringLayout slayout2 = new SpringLayout();
		prefabParametersJP.setLayout(slayout2);
		
		//components, displayed when only one item is selected 
		indexJTF = new JTextField("Index");
		indexJTF.setFont(INDEX_FONT);
		indexJTF.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,70));
		indexJTF.setHorizontalAlignment(JTextField.CENTER);
		indexJTF.setEnabled(false);
		
		JLabel categoryJL = new JLabel("Category: ");
		categoryJL.setPreferredSize(new Dimension(100,25));
		categoryJL.setHorizontalAlignment(JLabel.RIGHT);
		categoryJL.setFont(PARAMETER_FONT);
		
		categoryJCB = new JComboBox(goBase.prefabCategoryBase.getNameCollection());
		categoryJCB.setSelectedIndex(0);
		categoryJCB.setFont(DEFAULT_FONT);
		categoryJCB.setPreferredSize(new Dimension(170,22));
		categoryJCB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				onSelectedCategoryChanged();
			}
		});
		
		JButton manageCategoriesJB = new JButton("Manage categories");
		manageCategoriesJB.setPreferredSize(new Dimension(250, 23));
		manageCategoriesJB.setContentAreaFilled(false);
		manageCategoriesJB.setFont(DEFAULT_FONT);
		manageCategoriesJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainLink.dispose();
				new CategoryManagerWindow(CategoryManagerWindow.PREFAB_TYPE);
				//mainLink.dispose();
			}
		});
		
		JLabel tiledSizeJL = new JLabel("Tiled size: ");
		tiledSizeJL.setPreferredSize(new Dimension(100,25));
		tiledSizeJL.setHorizontalAlignment(JLabel.RIGHT);
		tiledSizeJL.setFont(PARAMETER_FONT);

		tiledWidthJTF = new INTTextField("1");
		tiledWidthJTF.setPreferredSize(new Dimension(70,22));
		tiledWidthJTF.setHorizontalAlignment(JTextField.CENTER);
		tiledWidthJTF.setFont(DEFAULT_FONT);
		
		JLabel xJL = new JLabel("x");
		xJL.setFont(PARAMETER_FONT);
		
		tiledHeightJTF = new INTTextField("1");
		tiledHeightJTF.setPreferredSize(new Dimension(70,22));
		tiledHeightJTF.setHorizontalAlignment(JTextField.CENTER);
		tiledHeightJTF.setFont(DEFAULT_FONT);
		
		JLabel textureJL = new JLabel("Texture: ");
		textureJL.setPreferredSize(new Dimension(100,25));
		textureJL.setHorizontalAlignment(JLabel.RIGHT);
		textureJL.setFont(PARAMETER_FONT);
		
		textureJTF = new JTextField();
		textureJTF.setPreferredSize(new Dimension(135,22));
		textureJTF.setFont(DEFAULT_FONT);
		
		browseLocationJB = new JButton("...");
		browseLocationJB.setPreferredSize(new Dimension(35,22));
		browseLocationJB.setFont(DEFAULT_FONT);
		browseLocationJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		JLabel descriptionJL = new JLabel("Description:" );
		descriptionJL.setPreferredSize(new Dimension(100,25));
		descriptionJL.setHorizontalAlignment(JLabel.RIGHT);
		descriptionJL.setFont(PARAMETER_FONT);
		
		descriptionJTA = new JTextArea();
		descriptionJTA.setMargin(new Insets(5,5,5,5));
		descriptionJTA.setLineWrap(true);
		
		descriptionJSP = new JScrollPane(descriptionJTA);
		descriptionJSP.setPreferredSize(new Dimension(170,80));
		
		JButton createJB = new JButton("Confirm");
		createJB.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,35));
		createJB.setContentAreaFilled(false);
		createJB.setFont(DEFAULT_FONT);
		createJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onCreateClicked();
			}
		});
		
		//Additive attributes panel. It's content changes whenever another category selected
		additiveAttributesPanel = new JPanel();
		additiveAttributesPanel.setLayout(new BoxLayout(additiveAttributesPanel, BoxLayout.Y_AXIS));
		setAdditiveAttributes(categoryJCB.getSelectedItem().toString());
		
		JScrollPane additiveAttributesJSP = new JScrollPane(additiveAttributesPanel);
		additiveAttributesJSP.setBorder(BorderFactory.createEmptyBorder());
		additiveAttributesJSP.setPreferredSize(new Dimension(250,130));
		additiveAttributesJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		additiveAttributesJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		additiveAttributesJSP.setBorder(BorderFactory.createTitledBorder("Additive attributes"));
		
		//Adding components to the parameters panel
		prefabParametersJP.add(indexJTF);
		
		slayout2.putConstraint(SpringLayout.NORTH, categoryJL, 
				15,SpringLayout.SOUTH, indexJTF);
		slayout2.putConstraint(SpringLayout.WEST, categoryJL, 
				0,SpringLayout.WEST, prefabParametersJP);
		prefabParametersJP.add(categoryJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, categoryJCB, 
				0,SpringLayout.VERTICAL_CENTER, categoryJL);
		slayout2.putConstraint(SpringLayout.WEST, categoryJCB, 
				5,SpringLayout.EAST, categoryJL);
		prefabParametersJP.add(categoryJCB);
		
		slayout2.putConstraint(SpringLayout.NORTH, manageCategoriesJB, 
				5,SpringLayout.SOUTH, categoryJL);
		slayout2.putConstraint(SpringLayout.EAST, manageCategoriesJB, 
				0,SpringLayout.EAST, categoryJCB);
		prefabParametersJP.add(manageCategoriesJB);
		
		slayout2.putConstraint(SpringLayout.NORTH, tiledSizeJL, 
				5,SpringLayout.SOUTH, manageCategoriesJB);
		slayout2.putConstraint(SpringLayout.WEST, tiledSizeJL, 
				0,SpringLayout.WEST, categoryJL);
		prefabParametersJP.add(tiledSizeJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, tiledWidthJTF, 
				0,SpringLayout.VERTICAL_CENTER, tiledSizeJL);
		slayout2.putConstraint(SpringLayout.WEST, tiledWidthJTF, 
				5,SpringLayout.EAST, tiledSizeJL);
		prefabParametersJP.add(tiledWidthJTF);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, xJL, 
				0,SpringLayout.VERTICAL_CENTER, tiledWidthJTF);
		slayout2.putConstraint(SpringLayout.WEST, xJL, 
				12,SpringLayout.EAST, tiledWidthJTF);
		prefabParametersJP.add(xJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, tiledHeightJTF, 
				0,SpringLayout.VERTICAL_CENTER, tiledSizeJL);
		slayout2.putConstraint(SpringLayout.EAST, tiledHeightJTF, 
				0,SpringLayout.EAST, categoryJCB);
		prefabParametersJP.add(tiledHeightJTF);
		
		slayout2.putConstraint(SpringLayout.NORTH, textureJL, 
				5,SpringLayout.SOUTH, tiledSizeJL);
		slayout2.putConstraint(SpringLayout.WEST, textureJL, 
				0,SpringLayout.WEST, tiledSizeJL);
		prefabParametersJP.add(textureJL);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, textureJTF, 
				0,SpringLayout.VERTICAL_CENTER, textureJL);
		slayout2.putConstraint(SpringLayout.WEST, textureJTF, 
				5,SpringLayout.EAST, textureJL);
		prefabParametersJP.add(textureJTF);
		
		slayout2.putConstraint(SpringLayout.VERTICAL_CENTER, browseLocationJB, 
				0,SpringLayout.VERTICAL_CENTER, textureJL);
		slayout2.putConstraint(SpringLayout.WEST, browseLocationJB, 
				0,SpringLayout.EAST, textureJTF);
		prefabParametersJP.add(browseLocationJB);
		
		slayout2.putConstraint(SpringLayout.NORTH, descriptionJL, 
				5,SpringLayout.SOUTH, textureJL);
		slayout2.putConstraint(SpringLayout.WEST, descriptionJL, 
				0,SpringLayout.WEST, textureJL);
		prefabParametersJP.add(descriptionJL);
		
		slayout2.putConstraint(SpringLayout.NORTH, descriptionJSP, 
				0,SpringLayout.NORTH, descriptionJL);
		slayout2.putConstraint(SpringLayout.EAST, descriptionJSP, 
				0,SpringLayout.EAST, browseLocationJB);
		prefabParametersJP.add(descriptionJSP);
		
		slayout2.putConstraint(SpringLayout.SOUTH, additiveAttributesJSP, 
				-5,SpringLayout.NORTH, createJB);
		slayout2.putConstraint(SpringLayout.HORIZONTAL_CENTER, additiveAttributesJSP, 
				0,SpringLayout.HORIZONTAL_CENTER, prefabParametersJP);
		prefabParametersJP.add(additiveAttributesJSP);
		
		slayout2.putConstraint(SpringLayout.SOUTH, createJB, 
				0,SpringLayout.SOUTH, prefabParametersJP);
		prefabParametersJP.add(createJB);
		
		return prefabParametersJP;
	}
	
	private void setAdditiveAttributes(String categoryName){
		additiveAttributesPanel.removeAll();
		
		PrefabCategory selectedCategory = goBase.prefabCategoryBase.get(categoryName);
		ArrayList<AdditiveAttribute> attributes = selectedCategory.getAdditiveAttributes();
		
		//fake jlabel
		//additiveAttributesPanel.add(new JLabel("   "));
		if(!attributes.isEmpty())
			for(AdditiveAttribute a : attributes){
				JPanel attributeJP = new JPanel();
				attributeJP.setPreferredSize(new Dimension(240,25));
				attributeJP.setMaximumSize(attributeJP.getPreferredSize());
				attributeJP.setMinimumSize(attributeJP.getPreferredSize());
				SpringLayout slayout = new SpringLayout();
				attributeJP.setLayout(slayout);
				
				JLabel attributeNameJL = new JLabel(a.getAttributeName()+": ");
				attributeNameJL.setFont(PARAMETER_FONT);
				attributeNameJL.setPreferredSize(new Dimension(100,20));
				attributeNameJL.setHorizontalAlignment(JLabel.RIGHT);
				
				slayout.putConstraint(SpringLayout.SOUTH, attributeNameJL, 
						0,SpringLayout.SOUTH, attributeJP);
				slayout.putConstraint(SpringLayout.WEST, attributeNameJL, 
						25,SpringLayout.WEST, attributeJP);
				attributeJP.add(attributeNameJL);
				switch(a.getAttributeType()){
				case "boolean":
					JCheckBox aaJCB = new JCheckBox();
					aaJCB.setPreferredSize(new Dimension(50, 20));
					aaJCB.setFont(DEFAULT_FONT);
					
					slayout.putConstraint(SpringLayout.VERTICAL_CENTER, aaJCB, 
							0,SpringLayout.VERTICAL_CENTER, attributeNameJL);
					slayout.putConstraint(SpringLayout.WEST, aaJCB, 
							6, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaJCB);
					break;
				default:
					JTextField aaJTF = new JTextField();
					aaJTF.setPreferredSize(new Dimension(50, 20));
					aaJTF.setFont(DEFAULT_FONT);
					
					slayout.putConstraint(SpringLayout.VERTICAL_CENTER, aaJTF, 
							0,SpringLayout.VERTICAL_CENTER, attributeNameJL);
					slayout.putConstraint(SpringLayout.WEST, aaJTF, 
							10, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaJTF);
					break;
				}
				additiveAttributesPanel.add(attributeJP);
			}
		else{
			JLabel msgJL = new JLabel("  No addditive attributes required");
			msgJL.setFont(DEFAULT_FONT);
			additiveAttributesPanel.add(msgJL);
			}
	}
	
	private void onSelectedCategoryChanged(){
		setAdditiveAttributes(categoryJCB.getSelectedItem().toString());
		additiveAttributesPanel.revalidate();
		additiveAttributesPanel.repaint();
	}
	
	public void onCreateClicked(){
		
	}
	
	private class INTTextField extends JTextField implements KeyListener{
		
		public INTTextField(String s){
			super(s);
			addKeyListener(this);
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()>=48 && e.getKeyCode()<=57){
				e.consume();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}
	}
}
