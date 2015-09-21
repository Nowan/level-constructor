import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.math.MathUtils;

public class PrefabManagerWindow  extends JDialog{
	
	private static final long serialVersionUID = 836695465983611801L;

	private PrefabManagerWindow mainLink = this;
	
	private XMLConverter xmlConverter = Globals.xmlConverter;
	
	private String assetName;
	
	private JTextField indexJTF;
	private JComboBox<String> categoryJCB;
	private JFormattedTextField tiledWidthJTF;
	private JFormattedTextField tiledHeightJTF;
	private PreviewPanel assetPP;
	private JScrollPane descriptionJSP;
	private JTextArea descriptionJTA;
	private JPanel additiveAttributesPanel;
	
	//Components of TextureMaker panel
	private TMCanvas tmCanvas;

	
	//if null, manager adds new Prefab to the base 
	//if not null, manager saves all changes to this object
	private Prefab editPrefab;
	
	public PrefabManagerWindow(){
		super(ConstructorWindow.instance, "Prefab manager");
		editPrefab=null;
		this.setModal(true);
		setSize(973,570);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		setContentPane(generateContent());
		
		indexJTF.setText(generatePrefabIndex());
		setVisible(true);
	}
	
	public PrefabManagerWindow(Prefab P){
		super(ConstructorWindow.instance, "Prefab manager");
		editPrefab=P;
		setModal(true);
		setSize(303,570);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setLayout(new BorderLayout());
		setResizable(false);
		
		setContentPane(generateContent());
		
		//filling form with data taken from the paremeter
		for(int i=0; i<GOBase.prefabCategoryBase.size();i++)
			if(GOBase.prefabCategoryBase.get(i).getID().equals(editPrefab.getCategoryID())){
				categoryJCB.setSelectedIndex(i);
			}
		categoryJCB.setEnabled(false);
		indexJTF.setText(editPrefab.getPrefabID());
		tiledWidthJTF.setText(String.valueOf(editPrefab.getTiledWidth()));
		tiledHeightJTF.setText(String.valueOf(editPrefab.getTiledHeight()));
		assetPP.setImage(editPrefab.getTexture());
		assetName = editPrefab.getAssetName();
		descriptionJTA.setText(editPrefab.getDesctiption());
		setAdditiveAttributes(P.getCategory().getName());
		for(int i=0;i<editPrefab.getCategory().getAdditiveAttributes().size();i++){
			JPanel panel = (JPanel)additiveAttributesPanel.getComponent(i);
			switch(editPrefab.getAdditiveAttributes().get(i).getAttributeType()){
			case "boolean":
				((JCheckBox)panel.getComponent(1)).setSelected((boolean)editPrefab.getAdditiveAttributes().get(i).getConvertedValue());;
				break;
			default:
				((JTextField)panel.getComponent(1)).setText(editPrefab.getAdditiveAttributes().get(i).getAttributeValue());;
				break;
			}
		}
		setVisible(true);
	}
	
	private JPanel generateContent(){
		JPanel panel = new JPanel();
		SpringLayout slayout = new SpringLayout();
		panel.setLayout(slayout);
		
		JPanel parametersForm = generateParametersForm();
		JPanel textureMakerForm = generateTextureMakerForm();
		panel.add(parametersForm);
		slayout.putConstraint(SpringLayout.WEST, textureMakerForm, 0, SpringLayout.EAST, parametersForm);
		panel.add(textureMakerForm);
		return panel;
	}
	
	private JPanel generateParametersForm(){
		JPanel prefabParametersJP = new JPanel();
		prefabParametersJP.setPreferredSize(new Dimension(300,getHeight()-28));
		SpringLayout slayout2 = new SpringLayout();
		prefabParametersJP.setLayout(slayout2);
		
		//components, displayed when only one item is selected 
		indexJTF = new JTextField("Index");
		indexJTF.setFont(Globals.INDEX_FONT);
		indexJTF.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,70));
		indexJTF.setHorizontalAlignment(JTextField.CENTER);
		indexJTF.setEnabled(false);
		
		JLabel categoryJL = new JLabel("Category: ");
		categoryJL.setPreferredSize(new Dimension(100,25));
		categoryJL.setHorizontalAlignment(JLabel.RIGHT);
		categoryJL.setFont(Globals.PARAMETER_FONT);
		
		categoryJCB = new JComboBox<String>(GOBase.prefabCategoryBase.getNameCollection());
		categoryJCB.setSelectedIndex(0);
		categoryJCB.setFont(Globals.DEFAULT_FONT);
		categoryJCB.setPreferredSize(new Dimension(170,22));
		categoryJCB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setAdditiveAttributes(categoryJCB.getSelectedItem().toString());
				additiveAttributesPanel.revalidate();
				additiveAttributesPanel.repaint();
				indexJTF.setText(generatePrefabIndex());
				assetPP.hideImage();
				assetName = null;
			}
		});
		
		JButton manageCategoriesJB = new JButton("Manage categories");
		manageCategoriesJB.setPreferredSize(new Dimension(250, 23));
		manageCategoriesJB.setContentAreaFilled(false);
		manageCategoriesJB.setFont(Globals.DEFAULT_FONT);
		manageCategoriesJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainLink.dispose();
				new CategoryManagerWindow(CategoryManagerWindow.PREFAB_TYPE);
			}
		});
		
		JLabel tiledSizeJL = new JLabel("Tiled size: ");
		tiledSizeJL.setPreferredSize(new Dimension(100,25));
		tiledSizeJL.setHorizontalAlignment(JLabel.RIGHT);
		tiledSizeJL.setFont(Globals.PARAMETER_FONT);

		tiledWidthJTF = new JFormattedTextField(new Integer(1));
		tiledWidthJTF.setPreferredSize(new Dimension(70,22));
		tiledWidthJTF.setHorizontalAlignment(JTextField.CENTER);
		tiledWidthJTF.setFont(Globals.DEFAULT_FONT);
		tiledWidthJTF.addKeyListener(new KeyListener(){
			@Override public void keyPressed(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {
				try  {  
					Integer.parseInt(tiledWidthJTF.getText());
					tmCanvas.repaint();
				}  
				catch(NumberFormatException nfe){}  
					
			}
			@Override public void keyTyped(KeyEvent arg0) {}
		});
		
		JLabel xJL = new JLabel("x");
		xJL.setFont(Globals.PARAMETER_FONT);
		
		tiledHeightJTF = new JFormattedTextField(new Integer(1));
		tiledHeightJTF.setPreferredSize(new Dimension(70,22));
		tiledHeightJTF.setHorizontalAlignment(JTextField.CENTER);
		tiledHeightJTF.setFont(Globals.DEFAULT_FONT);
		tiledHeightJTF.addKeyListener(new KeyListener(){
			@Override public void keyPressed(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {
				try  {  
					Integer.parseInt(tiledHeightJTF.getText());
					tmCanvas.repaint();
				}  
				catch(NumberFormatException nfe){}  
					
			}
			@Override public void keyTyped(KeyEvent arg0) {}
		});
		
		JLabel textureJL = new JLabel("Texture: ");
		textureJL.setPreferredSize(new Dimension(100,25));
		textureJL.setHorizontalAlignment(JLabel.RIGHT);
		textureJL.setFont(Globals.PARAMETER_FONT);
		
		assetPP = new PreviewPanel();
		assetPP.setPreferredSize(new Dimension(170,100));
		assetPP.setFont(Globals.DEFAULT_FONT);
		assetPP.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// get selected texture from AssetManager
				AssetManagerWindow assetManagerWindow = new AssetManagerWindow(categoryJCB.getSelectedItem().toString());
				Asset asset = assetManagerWindow.showDialog();
				if(asset!=null){
					String textureAddress = Globals.TEXTURES_FOLDER+asset.getAtlas().getName()+"/"+asset.getAssetName();
					assetPP.setImage(textureAddress);
				}
			}

			@Override public void mouseEntered(MouseEvent e) { assetPP.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,4));}

			@Override public void mouseExited(MouseEvent e) { assetPP.setBorder(BorderFactory.createEmptyBorder()); }

			@Override public void mousePressed(MouseEvent e) {}

			@Override public void mouseReleased(MouseEvent e) {}
			
		});
		
		JLabel descriptionJL = new JLabel("Description:" );
		descriptionJL.setPreferredSize(new Dimension(100,25));
		descriptionJL.setHorizontalAlignment(JLabel.RIGHT);
		descriptionJL.setFont(Globals.PARAMETER_FONT);
		
		descriptionJTA = new JTextArea();
		descriptionJTA.setMargin(new Insets(5,5,5,5));
		descriptionJTA.setLineWrap(true);
		
		descriptionJSP = new JScrollPane(descriptionJTA);
		descriptionJSP.setPreferredSize(new Dimension(170,80));
		
		JButton createJB = new JButton("Confirm");
		createJB.setPreferredSize(new Dimension(prefabParametersJP.getPreferredSize().width,35));
		createJB.setContentAreaFilled(false);
		createJB.setFont(Globals.DEFAULT_FONT);
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
		
		slayout2.putConstraint(SpringLayout.NORTH, assetPP, 
				0,SpringLayout.NORTH, textureJL);
		slayout2.putConstraint(SpringLayout.EAST, assetPP, 
				0,SpringLayout.EAST, tiledHeightJTF);
		prefabParametersJP.add(assetPP);
		
		slayout2.putConstraint(SpringLayout.NORTH, descriptionJL, 
				5,SpringLayout.SOUTH, assetPP);
		slayout2.putConstraint(SpringLayout.WEST, descriptionJL, 
				0,SpringLayout.WEST, textureJL);
		prefabParametersJP.add(descriptionJL);
		
		slayout2.putConstraint(SpringLayout.NORTH, descriptionJSP, 
				0,SpringLayout.NORTH, descriptionJL);
		slayout2.putConstraint(SpringLayout.EAST, descriptionJSP, 
				0,SpringLayout.EAST, assetPP);
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
	
	private JPanel generateTextureMakerForm(){
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(669,getHeight()-28));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		SpringLayout slayout = new SpringLayout();
		panel.setLayout(slayout);
		
		tmCanvas = new TMCanvas();
		tmCanvas.setPreferredSize(new Dimension(495,panel.getPreferredSize().height));
		
		JPanel texturePartsPreviewJP = new JPanel();
		texturePartsPreviewJP.setPreferredSize(new Dimension(150,300));
		texturePartsPreviewJP.setBackground(Color.BLACK);
		
		JScrollPane texturePartsPreviewJSP = new JScrollPane(texturePartsPreviewJP);
		texturePartsPreviewJSP.setPreferredSize(new Dimension(170,475));
		texturePartsPreviewJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		texturePartsPreviewJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel textureOptionsJP = new JPanel();
		textureOptionsJP.setPreferredSize(new Dimension(170,67));
		textureOptionsJP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textureOptionsJP.setBackground(Color.BLACK);
		textureOptionsJP.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
		
		JCheckBox verticalReflectionJChB = new JCheckBox("  vertical reflection");
		verticalReflectionJChB.setFont(Globals.PARAMETER_FONT);
		verticalReflectionJChB.setForeground(Color.WHITE);
		verticalReflectionJChB.setContentAreaFilled(false);
		verticalReflectionJChB.setEnabled(false);
		textureOptionsJP.add(verticalReflectionJChB);
		
		JCheckBox horisontalReflectionJChB = new JCheckBox("  horisontal reflection");
		horisontalReflectionJChB.setFont(Globals.PARAMETER_FONT);
		horisontalReflectionJChB.setForeground(Color.WHITE);
		horisontalReflectionJChB.setContentAreaFilled(false);
		horisontalReflectionJChB.setEnabled(false);
		textureOptionsJP.add(horisontalReflectionJChB);
		
		panel.add(tmCanvas);
		slayout.putConstraint(SpringLayout.EAST, texturePartsPreviewJSP, 0, SpringLayout.EAST, panel);
		panel.add(texturePartsPreviewJSP);
		slayout.putConstraint(SpringLayout.NORTH, textureOptionsJP, -1, SpringLayout.SOUTH, texturePartsPreviewJSP);
		slayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, textureOptionsJP, 0, SpringLayout.HORIZONTAL_CENTER, texturePartsPreviewJSP);
		panel.add(textureOptionsJP);
		
		return panel;
	}
	
	private void setAdditiveAttributes(String categoryName){
		additiveAttributesPanel.removeAll();
		
		PrefabCategory selectedCategory = GOBase.prefabCategoryBase.get(categoryName);
		ArrayList<AdditiveAttribute> attributes = selectedCategory.getAdditiveAttributes();
		
		if(!attributes.isEmpty())
			for(AdditiveAttribute a : attributes){
				JPanel attributeJP = new JPanel();
				attributeJP.setPreferredSize(new Dimension(240,25));
				attributeJP.setMaximumSize(attributeJP.getPreferredSize());
				attributeJP.setMinimumSize(attributeJP.getPreferredSize());
				SpringLayout slayout = new SpringLayout();
				attributeJP.setLayout(slayout);
				
				JLabel attributeNameJL = new JLabel(a.getAttributeName()+": ");
				attributeNameJL.setFont(Globals.PARAMETER_FONT);
				attributeNameJL.setPreferredSize(new Dimension(100,20));
				attributeNameJL.setHorizontalAlignment(JLabel.RIGHT);
				
				slayout.putConstraint(SpringLayout.SOUTH, attributeNameJL, 
						0,SpringLayout.SOUTH, attributeJP);
				slayout.putConstraint(SpringLayout.WEST, attributeNameJL, 
						5,SpringLayout.WEST, attributeJP);
				attributeJP.add(attributeNameJL);
				switch(a.getAttributeType()){
				case "boolean":
					JCheckBox aaJCB = new JCheckBox();
					aaJCB.setPreferredSize(new Dimension(50, 20));
					aaJCB.setFont(Globals.DEFAULT_FONT);
					
					slayout.putConstraint(SpringLayout.VERTICAL_CENTER, aaJCB, 
							0,SpringLayout.VERTICAL_CENTER, attributeNameJL);
					slayout.putConstraint(SpringLayout.WEST, aaJCB, 
							6, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaJCB);
					break;
				case "integer":
					JFormattedTextField aaIJFTF = new JFormattedTextField(new Integer(a.getAttributeValue()));
					aaIJFTF.setPreferredSize(new Dimension(50, 20));
					aaIJFTF.setFont(Globals.DEFAULT_FONT);
					aaIJFTF.setHorizontalAlignment(JFormattedTextField.CENTER);
					
					slayout.putConstraint(SpringLayout.VERTICAL_CENTER, aaIJFTF, 
							0,SpringLayout.VERTICAL_CENTER, attributeNameJL);
					slayout.putConstraint(SpringLayout.WEST, aaIJFTF, 
							10, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaIJFTF);
					break;
				case "double":
					JFormattedTextField aaDJFTF = new JFormattedTextField(new Double(a.getAttributeValue()));
					aaDJFTF.setPreferredSize(new Dimension(50, 20));
					aaDJFTF.setFont(Globals.DEFAULT_FONT);
					aaDJFTF.setHorizontalAlignment(JFormattedTextField.CENTER);
					
					slayout.putConstraint(SpringLayout.VERTICAL_CENTER, aaDJFTF, 
							0,SpringLayout.VERTICAL_CENTER, attributeNameJL);
					slayout.putConstraint(SpringLayout.WEST, aaDJFTF, 
							10, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaDJFTF);
					break;
				case "sString":
					JTextField aaJTF = new JTextField();
					aaJTF.setPreferredSize(new Dimension(100, 20));
					aaJTF.setFont(Globals.DEFAULT_FONT);
					aaJTF.setHorizontalAlignment(JFormattedTextField.CENTER);
					
					slayout.putConstraint(SpringLayout.VERTICAL_CENTER, aaJTF, 
							0,SpringLayout.VERTICAL_CENTER, attributeNameJL);
					slayout.putConstraint(SpringLayout.WEST, aaJTF, 
							10, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaJTF);
					break;
				case "lString":
					JTextArea aaJTA = new JTextArea();
					aaJTA.setFont(Globals.DEFAULT_FONT);
					aaJTA.setMargin(new Insets(5,5,5,5));
					aaJTA.setLineWrap(true);
					
					JScrollPane aaJSP = new JScrollPane(aaJTA);
					aaJSP.setPreferredSize(new Dimension(100,80));
					
					attributeJP.setPreferredSize(new Dimension(240,85));
					attributeJP.setMaximumSize(attributeJP.getPreferredSize());
					attributeJP.setMinimumSize(attributeJP.getPreferredSize());
					
					slayout.putConstraint(SpringLayout.NORTH, attributeNameJL, 
							2,SpringLayout.NORTH, attributeJP);
					
					slayout.putConstraint(SpringLayout.NORTH, aaJSP, 
							0,SpringLayout.NORTH, attributeJP);
					slayout.putConstraint(SpringLayout.WEST, aaJSP, 
							10, SpringLayout.EAST, attributeNameJL);
					attributeJP.add(aaJSP);
					break;
				}
				additiveAttributesPanel.add(attributeJP);
			}
		else{
			JLabel msgJL = new JLabel("  No addditive attributes required");
			msgJL.setFont(Globals.DEFAULT_FONT);
			additiveAttributesPanel.add(msgJL);
			}
	}
	
	private String generatePrefabIndex(){
		String categoryIndex=GOBase.prefabCategoryBase.get(categoryJCB.getSelectedItem().toString()).getID();
		int lastIndex = 0;
		for(Prefab p : GOBase.prefabsBase)
			if(p.getCategoryID().equals(categoryIndex)){
				int ind = Integer.valueOf(p.getPrefabID().substring(p.getPrefabID().length()-2));
				if(ind>lastIndex)
					lastIndex=ind;
			}
		return categoryIndex+"_"+String.format("%02d", lastIndex+1);
	}

	public void onCreateClicked(){

			//saving all the values into variables
			int tw = Integer.valueOf(tiledWidthJTF.getText());
			int th = Integer.valueOf(tiledHeightJTF.getText());
			//if necessary information missed, throw an exception
			if(tw==0 || th==0 || assetName==null){
				JOptionPane.showMessageDialog(this,"Some fields are missing","ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String description = descriptionJTA.getText();
			String id = indexJTF.getText();
			String categoryName = categoryJCB.getSelectedItem().toString();
			PrefabCategory category = GOBase.prefabCategoryBase.get(categoryName);
			ArrayList<AdditiveAttribute> additiveAttributes = new ArrayList<AdditiveAttribute>();
			for(int i=0;i<category.getAdditiveAttributes().size();i++){
				additiveAttributes.add(category.getAdditiveAttributes().get(i));
				JPanel panel = (JPanel)additiveAttributesPanel.getComponent(i);
				switch(additiveAttributes.get(i).getAttributeType()){
				case "boolean":
					boolean bln = ((JCheckBox)panel.getComponent(1)).isSelected();
					additiveAttributes.get(i).setAttributeValue(String.valueOf(bln));
					break;
				default:
					String dflt = ((JTextField)panel.getComponent(1)).getText();
					additiveAttributes.get(i).setAttributeValue(String.valueOf(dflt));
					break;
				}
			}
			
			//if there is no prefab to edit, add new Prefab to the base
			if(editPrefab==null)
				GOBase.prefabsBase.add(new Prefab(id,category.getID(),tw,th,GOBase.assetsBase.get(assetName),description,additiveAttributes));
			//else - save the parameters to editPrefab
			else{
				editPrefab.setPrefabID(id);
				editPrefab.setCategory(GOBase.prefabCategoryBase.get(categoryName).getID());
				editPrefab.setTiledWidth(tw);
				editPrefab.setTiledHeight(th);
				editPrefab.setAsset(assetName);
				editPrefab.setDesctiption(description);
				editPrefab.setAdditiveAttributes(additiveAttributes);
			}

			xmlConverter.savePrefabBase();
			
			//refresh Game Object Manager Window
			ConstructorWindow.goManager.refresh();
			ConstructorWindow.instance.collectionsPanel.tilesTab.refreshPrefabPanel();
			mainLink.dispose();
		
	}
	
	private class TMCanvas extends JPanel implements MouseMotionListener, MouseListener,MouseWheelListener{
		
		//Shifting values of viewport
		private int viewportShiftX,viewportShiftY;
		
		private float scaleFactor;
		
		private int scaledTileSize;
		
		//Contains position of where the dragging started
		//Used to calculate shifting of viewport
		private Point dragStart;
		
		public TMCanvas(){
			super();
			setBackground(Color.BLACK);
			setScaleFactor(1.0f);
			setSize(getPreferredSize());
			addMouseMotionListener(this);
			addMouseListener(this);
			addMouseWheelListener(this);
			viewportShiftX = 0;
			viewportShiftY = 0;
			
		}
		
		private void setScaleFactor(float scaleFactor){
			this.scaleFactor = scaleFactor;
			scaledTileSize = (int)(Globals.TILE_SIZE*scaleFactor);
		}
		
		@Override
		public void paint(Graphics g){
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			//draw object's body border
			if(!tiledWidthJTF.getText().isEmpty()&&!tiledHeightJTF.getText().isEmpty()){
				int bodyWidth = Integer.valueOf(tiledWidthJTF.getText())*scaledTileSize;
				int bodyHeight = Integer.valueOf(tiledHeightJTF.getText())*scaledTileSize;
				
				int posX = this.getWidth()/2-bodyWidth/2-viewportShiftX;
				int posY = this.getHeight()/2-bodyHeight/2-viewportShiftY;
				
				g2d.setStroke(new BasicStroke(2*scaleFactor));
				g2d.setColor(Color.BLUE);
				
				g2d.drawRect(posX, posY, bodyWidth, bodyHeight);
			}
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			//Move viewport
			viewportShiftX += dragStart.x-arg0.getX();
			viewportShiftY += dragStart.y-arg0.getY();
			System.out.println(viewportShiftX+" "+viewportShiftY);
			dragStart = arg0.getPoint();
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

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
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			//save the position of drag start
			dragStart = arg0.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			dragStart = null;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			setScaleFactor(MathUtils.clamp(scaleFactor-(float)(arg0.getWheelRotation())*0.05f, 0.5f, 2.0f));
			repaint();
		}
	}

}
