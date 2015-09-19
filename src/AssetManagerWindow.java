import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class AssetManagerWindow extends JDialog{

	private static final long serialVersionUID = 2853864923023776954L;

	private final AssetManagerWindow linkToAssetManager = this;
	
	private DefaultComboBoxModel<String> atlasBaseModel;
	private JComboBox<String> atlasBaseJCB;
	private JPanel previewsJP;
	private JPanel atlasManagementJP;
	private JTextField atlasName;
	private JButton createAtlasJB;
	private JPanel assetManagementJP;
	JButton selectAssetJB, removeAssetJB;
	private JPanel addAssetJP;
	private PreviewPanel detailedMainPreview;
	private JPanel assetPartsJP;
	
	private PreviewPanel texturePreviewJP;
	private JButton confirmJB;
	private JButton discardJB;
	private JTextField textureAddressJTF;
	
	//true means that window was created from GOManager, which means user can add/remove assets from all categories
	//false means that window was created from ParticleManager, which means user must select a texture from selected category
	private boolean chooseMode;
	
	//true means that "add asset" button was pressed, and Asset Manager now has a form to add new asset on the right
	//false - Asset Manager has forms "Atlas management" and "Asset management" on the right
	private boolean insertionMode;
	
	//asset, selected in AssetManager and which is displayed in detailed preview panel
	private Asset selectedAsset;
	private AssetPreview selectedAssetPreview;
	
	private JFileChooser fileChooser;
	
	
	public AssetManagerWindow(){
		super(ConstructorWindow.instance, "Asset manager");
		//blocks access to the main window
		this.setModal(true);
		setSize(760,450);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setResizable(false);
		
		chooseMode=false;
		setContentPane(generateContent());
		setInsertionMode(false);
		if(atlasBaseJCB.getItemCount()!=0)
			atlasBaseJCB.setSelectedIndex(0);
		setDetailedPreview(null);
		
		fileChooser=new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("image", new String[] {"png","jpeg","jpg","gif"});
		fileChooser.setFileFilter(filter);
		
		setVisible(true);
	}
	
	public AssetManagerWindow(String categoryName){
		super(ConstructorWindow.instance, "Asset manager");
		//blocks access to the main window
		this.setModal(true);
		setSize(600,350);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setResizable(false);
		
		setContentPane(generateContent());
		setInsertionMode(false);
		
		chooseMode=true;
		if(atlasBaseJCB.getItemCount()!=0)
			atlasBaseJCB.setSelectedItem(categoryName);
		atlasBaseJCB.setEnabled(false);
		
		setVisible(false);
	}
	
	private JPanel generateContent(){
		JPanel panel = new JPanel();
		panel.setPreferredSize(this.getSize());
		SpringLayout slayout = new SpringLayout();
		panel.setLayout(slayout);
		
		previewsJP = new JPanel();
		previewsJP.setBackground(Color.BLACK);
		previewsJP.setAutoscrolls(true);
		previewsJP.setPreferredSize(new Dimension(100,600));
		previewsJP.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		
		JScrollPane previewsJSP = new JScrollPane(previewsJP);
		previewsJSP.setPreferredSize(new Dimension(180,398));
		previewsJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		previewsJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		atlasBaseModel = new DefaultComboBoxModel(GOBase.atlasesBase.getNamesArray());
		
		atlasBaseJCB = new JComboBox<String>(atlasBaseModel);
		atlasBaseJCB.setFont(Globals.DEFAULT_FONT);
		atlasBaseJCB.setPreferredSize(new Dimension(previewsJSP.getPreferredSize().width,25));
		atlasBaseJCB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onAtlasChanged();
			}
		});
		
		//setting up detailed asset preview
		JPanel detailedPreviewJP = new JPanel();
		detailedPreviewJP.setPreferredSize(new Dimension(400,getHeight()-27));
		detailedPreviewJP.setBorder(BorderFactory.createEtchedBorder());
		SpringLayout detailedPreviewSL = new SpringLayout();
		detailedPreviewJP.setLayout(detailedPreviewSL);
		
		detailedMainPreview = new PreviewPanel();
		detailedMainPreview.setPreferredSize(new Dimension(detailedPreviewJP.getPreferredSize().width-5,300));
		
		assetPartsJP = new JPanel();
		assetPartsJP.setPreferredSize(new Dimension(600,100));
		assetPartsJP.setBackground(Color.BLACK);
		
		JScrollPane assetPartsJSP = new JScrollPane(assetPartsJP);
		assetPartsJSP.setPreferredSize(new Dimension(detailedPreviewJP.getPreferredSize().width-4,120));
		assetPartsJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		assetPartsJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		detailedPreviewSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, detailedMainPreview, 0, SpringLayout.HORIZONTAL_CENTER, detailedPreviewJP);
		detailedPreviewJP.add(detailedMainPreview);
		
		detailedPreviewSL.putConstraint(SpringLayout.NORTH, assetPartsJSP, 0, SpringLayout.SOUTH, detailedMainPreview);
		detailedPreviewSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, assetPartsJSP, 0, SpringLayout.HORIZONTAL_CENTER, detailedPreviewJP);
		detailedPreviewJP.add(assetPartsJSP);
		
		JPanel lastSectionJP = new JPanel();
		lastSectionJP.setPreferredSize(new Dimension(173,getHeight()-27));
		SpringLayout lastSectionSL = new SpringLayout();
		lastSectionJP.setLayout(lastSectionSL);
		
		atlasManagementJP = new JPanel();
		atlasManagementJP.setPreferredSize(new Dimension(170,191));
		atlasManagementJP.setBorder(BorderFactory.createTitledBorder("Atlas management"));
		
		atlasName = new JTextField();
		atlasName.setToolTipText("Name of atlas to create");
		atlasName.setFont(Globals.DEFAULT_FONT);
		atlasName.setPreferredSize(new Dimension(150,22));
		atlasName.addKeyListener(new KeyListener(){
			@Override public void keyPressed(KeyEvent arg0) {}
			@Override public void keyReleased(KeyEvent arg0) {
				createAtlasJB.setEnabled(!atlasName.getText().isEmpty());
			}
			@Override public void keyTyped(KeyEvent arg0) {}
			
		});
		atlasManagementJP.add(atlasName);
		
		createAtlasJB = new JButton("Create atlas");
		createAtlasJB.setToolTipText("Creates an atlas with provided name");
		createAtlasJB.setFont(Globals.DEFAULT_FONT);
		createAtlasJB.setPreferredSize(new Dimension(150,34));
		createAtlasJB.setContentAreaFilled(false);
		createAtlasJB.setEnabled(false);
		createAtlasJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onCreateAtlasClicked();
			}
		});
		atlasManagementJP.add(createAtlasJB);
		
		atlasManagementJP.add(new JLabel("  "));
		
		JButton rebuildAtlasJB = new JButton("Rebuild atlas");
		rebuildAtlasJB.setToolTipText("Rebuilds selected atlas");
		rebuildAtlasJB.setFont(Globals.DEFAULT_FONT);
		rebuildAtlasJB.setPreferredSize(new Dimension(150,34));
		rebuildAtlasJB.setContentAreaFilled(false);
		rebuildAtlasJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rebuildAtlas();
			}
		});
		atlasManagementJP.add(rebuildAtlasJB);
		
		JButton removeAtlasJB = new JButton("Remove atlas");
		removeAtlasJB.setToolTipText("Removes selected atlas and all textures it contains");
		removeAtlasJB.setFont(Globals.DEFAULT_FONT);
		removeAtlasJB.setPreferredSize(new Dimension(150,34));
		removeAtlasJB.setContentAreaFilled(false);
		removeAtlasJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onRemoveAtlasClicked();
			}
		});
		atlasManagementJP.add(removeAtlasJB);
		
		assetManagementJP = new JPanel();
		assetManagementJP.setPreferredSize(new Dimension(170,104));
		assetManagementJP.setBorder(BorderFactory.createTitledBorder("Asset management"));
		
		selectAssetJB = new JButton("✔     Select asset");
		selectAssetJB.setToolTipText("Select current asset as prefab texture");
		selectAssetJB.setFont(Globals.DEFAULT_FONT);
		selectAssetJB.setPreferredSize(new Dimension(150,34));
		selectAssetJB.setContentAreaFilled(false);
		selectAssetJB.setEnabled(chooseMode);
		assetManagementJP.add(selectAssetJB);
		
		removeAssetJB = new JButton("✗     Remove asset");
		removeAssetJB.setToolTipText("Remove current asset from atlas");
		removeAssetJB.setFont(Globals.DEFAULT_FONT);
		removeAssetJB.setPreferredSize(new Dimension(150,34));
		removeAssetJB.setContentAreaFilled(false);
		removeAssetJB.setEnabled(false);
		removeAssetJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onRemoveAssetClicked();
			}
		});
		assetManagementJP.add(removeAssetJB);
		
		addAssetJP = new JPanel();
		addAssetJP.setBorder(BorderFactory.createTitledBorder("Create asset"));
		addAssetJP.setPreferredSize(new Dimension(170,300));
		SpringLayout addAssetSL = new SpringLayout();
		addAssetJP.setLayout(addAssetSL);
		
		textureAddressJTF = new JTextField("choose texture...");
		textureAddressJTF.setFont(Globals.DEFAULT_FONT);
		textureAddressJTF.setEnabled(false);
		textureAddressJTF.setPreferredSize(new Dimension(120,22));
		
		JButton textureAddressJB = new JButton("...");
		textureAddressJB.setFont(Globals.PARAMETER_FONT);
		textureAddressJB.setPreferredSize(new Dimension(40,22));
		textureAddressJB.setContentAreaFilled(false);
		textureAddressJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnValue = fileChooser.showOpenDialog(ConstructorWindow.instance);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
			          File selectedFile = fileChooser.getSelectedFile();
			          String fileAddress = selectedFile.getAbsolutePath();
			          try{
							BufferedImage texture = ImageIO.read(new File(fileAddress));
							texturePreviewJP.setImage(texture);
							textureAddressJTF.setText(fileAddress);
							confirmJB.setEnabled(true);
						}
						catch(IOException ex){
							System.out.println(ex.getMessage());
						}
			    }
			}
		});
		
		texturePreviewJP = new PreviewPanel();
		texturePreviewJP.setPreferredSize(new Dimension(160,220));
		
		confirmJB = new JButton("Confirm");
		confirmJB.setFont(Globals.PARAMETER_FONT);
		confirmJB.setPreferredSize(new Dimension(80,30));
		confirmJB.setEnabled(false);
		confirmJB.setMargin(new Insets(0,0,0,0));
		confirmJB.setContentAreaFilled(false);
		confirmJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onConfirmClicked();
			}
		});
		
		discardJB = new JButton("Discard");
		discardJB.setFont(Globals.PARAMETER_FONT);
		discardJB.setPreferredSize(new Dimension(80,30));
		discardJB.setMargin(new Insets(0,0,0,0));
		discardJB.setContentAreaFilled(false);
		discardJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onDiscardClicked();
			}
		});
		
		addAssetSL.putConstraint(SpringLayout.NORTH, textureAddressJTF, 5, SpringLayout.NORTH, addAssetJP);
		addAssetJP.add(textureAddressJTF);
		addAssetSL.putConstraint(SpringLayout.VERTICAL_CENTER, textureAddressJB, 0, SpringLayout.VERTICAL_CENTER, textureAddressJTF);
		addAssetSL.putConstraint(SpringLayout.WEST, textureAddressJB, 0, SpringLayout.EAST, textureAddressJTF);
		addAssetJP.add(textureAddressJB);
		addAssetSL.putConstraint(SpringLayout.NORTH, texturePreviewJP, 0, SpringLayout.SOUTH, textureAddressJTF);
		addAssetJP.add(texturePreviewJP);
		addAssetSL.putConstraint(SpringLayout.NORTH, confirmJB, 0, SpringLayout.SOUTH, texturePreviewJP);
		addAssetSL.putConstraint(SpringLayout.WEST, confirmJB, 0, SpringLayout.HORIZONTAL_CENTER, addAssetJP);
		addAssetJP.add(confirmJB);
		addAssetSL.putConstraint(SpringLayout.NORTH, discardJB, 0, SpringLayout.SOUTH, texturePreviewJP);
		addAssetSL.putConstraint(SpringLayout.EAST, discardJB, 0, SpringLayout.HORIZONTAL_CENTER, addAssetJP);
		addAssetJP.add(discardJB);
		
		lastSectionSL.putConstraint(SpringLayout.NORTH, atlasManagementJP, 10, SpringLayout.NORTH, lastSectionJP);
		lastSectionSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, atlasManagementJP, 0, SpringLayout.HORIZONTAL_CENTER, lastSectionJP);
		lastSectionJP.add(atlasManagementJP);
		
		lastSectionSL.putConstraint(SpringLayout.NORTH, assetManagementJP, 25, SpringLayout.SOUTH, atlasManagementJP);
		lastSectionSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, assetManagementJP, 0, SpringLayout.HORIZONTAL_CENTER, lastSectionJP);
		lastSectionJP.add(assetManagementJP);
		
		lastSectionSL.putConstraint(SpringLayout.VERTICAL_CENTER, addAssetJP, 0, SpringLayout.VERTICAL_CENTER, lastSectionJP);
		lastSectionSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, addAssetJP, 0, SpringLayout.HORIZONTAL_CENTER, lastSectionJP);
		lastSectionJP.add(addAssetJP);
		
		panel.add(atlasBaseJCB);
		slayout.putConstraint(SpringLayout.NORTH, previewsJSP, 0, SpringLayout.SOUTH, atlasBaseJCB);
		panel.add(previewsJSP);
		slayout.putConstraint(SpringLayout.WEST, detailedPreviewJP, 0, SpringLayout.EAST, previewsJSP);
		panel.add(detailedPreviewJP);
		slayout.putConstraint(SpringLayout.WEST, lastSectionJP, 0, SpringLayout.EAST, detailedPreviewJP);
		panel.add(lastSectionJP);
		
		return panel;
	}
	
	public Asset showDialog(){
		setVisible(true);
		return selectedAsset;
	}
	
	private void setInsertionMode(boolean flag){
		insertionMode = flag;
		
		atlasBaseJCB.setEnabled(!insertionMode);
		atlasManagementJP.setVisible(!insertionMode);
		for(Component c:atlasManagementJP.getComponents())
			c.setVisible(!insertionMode);
		assetManagementJP.setVisible(!insertionMode);
		for(Component c:assetManagementJP.getComponents())
			c.setVisible(!insertionMode);
		addAssetJP.setVisible(insertionMode);
		for(Component c:addAssetJP.getComponents())
			c.setVisible(insertionMode);
		
	}
	
	private void onAtlasChanged(){
		previewsJP.removeAll();
		int newHeight=0;
		Atlas selectedAtlas = GOBase.atlasesBase.get(atlasBaseJCB.getSelectedItem().toString());
		for(Asset a:selectedAtlas.getAssets()){
			AssetPreview assetPreview = new AssetPreview(a);
			previewsJP.add(assetPreview);
			newHeight+=assetPreview.getPreferredSize().height;
		}
		
		JButton addAssetJB = new JButton("+");
		addAssetJB.setFont(Globals.INDEX_FONT);
		addAssetJB.setMargin(new Insets(0,0,0,0));
		addAssetJB.setPreferredSize(new Dimension(60,60));
		addAssetJB.setContentAreaFilled(false);
		addAssetJB.setForeground(Color.WHITE);
		addAssetJB.setToolTipText("Add new Asset");
		addAssetJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedAsset=null;
				if(selectedAssetPreview!=null)
				selectedAssetPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				selectedAssetPreview=null;
				setDetailedPreview(null);
				detailedMainPreview.setBackground(Color.DARK_GRAY);
				assetPartsJP.setBackground(Color.GRAY);
				setInsertionMode(true);
			}
		});
		
		newHeight+=addAssetJB.getPreferredSize().height+20;
		previewsJP.add(addAssetJB);
		previewsJP.setPreferredSize(new Dimension(100,newHeight));
		previewsJP.revalidate();
		previewsJP.repaint();
	}
	
	private void setDetailedPreview(Asset asset){
		//TODO
		assetPartsJP.removeAll();
		if(asset==null){
			detailedMainPreview.hideImage();
			assetPartsJP.revalidate();
			assetPartsJP.repaint();
			return;
		}
		detailedMainPreview.setImage(asset.getAssetTexture());
		for(BufferedImage bi : asset.getFrameTextures()){
			PreviewPanel framePreview = new PreviewPanel();
			framePreview.setPreferredSize(new Dimension(100,100));
			framePreview.setSize(framePreview.getPreferredSize());
			framePreview.setImage(bi);
			assetPartsJP.add(framePreview);
		}
		assetPartsJP.revalidate();
		assetPartsJP.repaint();
	}
	
	private void rebuildAtlas(){
		Settings settings = new Settings();
		//settings.maxWidth=1024;
		//settings.maxHeight=1024;
		TexturePacker.process(settings, 
			Globals.TEXTURES_FOLDER+atlasBaseJCB.getSelectedItem().toString(), 
			Globals.ATLASES_FOLDER, atlasBaseJCB.getSelectedItem().toString());
	}
	
	private void onCreateAtlasClicked(){
		String newAtlasName =  atlasName.getText();
		boolean atlasInBase = false;
		for(Atlas a : GOBase.atlasesBase)
			if(newAtlasName.equals(a.getName())){
				JOptionPane.showMessageDialog(this, "Atlas already exists");
				atlasInBase=true;
			}
		if(atlasInBase) return;
		//create texture folder for new atlas
		new File(Globals.TEXTURES_FOLDER+atlasName.getText()).mkdirs();
		
		GOBase.atlasesBase.add(new Atlas(newAtlasName));
		Globals.xmlConverter.saveAtlasesBase();
		
		JOptionPane.showMessageDialog(this, "Atlas successfully created!");
		
		atlasName.setText("");
		createAtlasJB.setEnabled(false);
		refreshAtlasList();
	}
	
	private void onRemoveAtlasClicked(){
		Atlas selectedAtlas = GOBase.atlasesBase.get(atlasBaseJCB.getSelectedItem().toString());
		//delete texture folder of selected category
		new File(Globals.TEXTURES_FOLDER+selectedAtlas.getName()).delete();
		GOBase.atlasesBase.remove(selectedAtlas);
		Globals.xmlConverter.saveAtlasesBase();
		refreshAtlasList();
	}
	
	private void refreshAtlasList(){
		atlasBaseModel.removeAllElements();
		for(Atlas a : GOBase.atlasesBase)
			atlasBaseModel.addElement(a.getName());
	}
	
	private void onRemoveAssetClicked(){
		new JOptionPane();
		int n = JOptionPane.showConfirmDialog(
			  	ConstructorWindow.instance,
			    "Are you sure you want to delete this asset? There may be objects that use it",
			    "Message",
			    JOptionPane.YES_NO_OPTION);
		if(n==0)
			try{
				//TODO getting address
				Atlas atlas = selectedAsset.getAtlas();
				String textureAddress =Globals.TEXTURES_FOLDER+atlas.getName()+"/"+selectedAsset.getAssetName()+".png";
				Files.delete(Paths.get(textureAddress));
				//save changes to assetsbase.xml
				atlas.getAssets().remove(selectedAsset);
				GOBase.assetsBase.refresh();
				Globals.xmlConverter.saveAtlasesBase();
				rebuildAtlas();
				onAtlasChanged();
			}
			catch(IOException ex){
				System.out.println(ex.getStackTrace());
			}
	}
	
	private void onConfirmClicked(){
		
		//copy texture image to the textures folder
		String textureAddress = textureAddressJTF.getText();
		
		Atlas atlas = GOBase.atlasesBase.get(atlasBaseJCB.getSelectedItem().toString());
		
		//generating texture name in form "atlasname-assetindex_frameindex.png"
		int freeIndex = 0;
		
		for(Asset a : atlas.getAssets()){
			int assetIndex = Integer.valueOf(a.getAssetName().substring(atlas.getName().length()+1, atlas.getName().length()+3));
			if(freeIndex!=assetIndex)
				break;
			else
				freeIndex++;
		}
		
		if(this.selectedAsset!=null){
			
		}
		
		String generatedAssetName = atlas.getName()+"-"+String.format("%02d", freeIndex);
		String targetAddress = Globals.TEXTURES_FOLDER+atlas.getName()+"/"+generatedAssetName+textureAddress.substring(textureAddress.lastIndexOf('.'));
		
		if(!textureAddress.equals(targetAddress))
			try{
				Files.copy(Paths.get(textureAddress),Paths.get(targetAddress), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		
		//save changes to assetsbase.xml
		atlas.getAssets().add(new Asset(atlas,generatedAssetName));
		GOBase.assetsBase.refresh();
		Globals.xmlConverter.saveAtlasesBase();
		onAtlasChanged();
		rebuildAtlas();
		onDiscardClicked();
	}
	
	private void onDiscardClicked(){
		texturePreviewJP.hideImage();
		textureAddressJTF.setText("choose texture...");
		detailedMainPreview.setBackground(Color.BLACK);
		assetPartsJP.setBackground(Color.BLACK);
		setInsertionMode(false);
	}
	
	private class AssetPreview extends JPanel implements MouseListener{
		private static final long serialVersionUID = -701096237525672995L;
		
		private PreviewPanel mainPreview;
		
		private Asset asset;
		
		public AssetPreview(Asset asset){
			super();
			this.asset = asset;
			int extendedHeight = asset.hasAnimation() ? 50 : 0;
			setPreferredSize(new Dimension(144,124+extendedHeight));
			setBackground(Color.BLACK);
			if(linkToAssetManager.selectedAsset!=asset)
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
			else{
				setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
				selectedAssetPreview = this;
			}
			SpringLayout slayout = new SpringLayout();
			setLayout(slayout);
			
			mainPreview = new PreviewPanel();
			mainPreview.setPreferredSize(new Dimension(140,120));
			mainPreview.setSize(getPreferredSize());
			mainPreview.setImage(asset.getAssetTexture());
			addMouseListener(this);
			
			slayout.putConstraint(SpringLayout.NORTH, mainPreview, 0, SpringLayout.NORTH, this);
			slayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, mainPreview, 0, SpringLayout.HORIZONTAL_CENTER, this);
			add(mainPreview);
			if(asset.hasAnimation()){
				
				JPanel framePreviewJP = new JPanel();
				framePreviewJP.setPreferredSize(new Dimension(200,50));
				framePreviewJP.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
				framePreviewJP.setBackground(Color.BLACK);
				framePreviewJP.addMouseListener(this);
				
				JScrollPane framePreviewJSP = new JScrollPane(framePreviewJP);
				framePreviewJSP.setPreferredSize(new Dimension(140,50));
				framePreviewJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				framePreviewJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				framePreviewJSP.setBorder(BorderFactory.createEmptyBorder());
				for(BufferedImage bi : asset.getFrameTextures()){
					PreviewPanel framePreview = new PreviewPanel();
					framePreview.setPreferredSize(new Dimension(50,50));
					framePreview.setSize(getPreferredSize());
					framePreview.setImage(bi);
					framePreviewJP.add(framePreview);
				}
				
				slayout.putConstraint(SpringLayout.NORTH, framePreviewJSP, 0, SpringLayout.SOUTH, mainPreview);
				slayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, framePreviewJSP, 0, SpringLayout.HORIZONTAL_CENTER, this);
				add(framePreviewJSP);
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			if(insertionMode) return;
			if(linkToAssetManager.selectedAsset!=asset)
				setBorder(BorderFactory.createLineBorder(Color.GRAY));
			setDetailedPreview(asset);
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			if(insertionMode) return;
			if(linkToAssetManager.selectedAsset!=asset){
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
				setDetailedPreview(linkToAssetManager.selectedAsset);
			}
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if(insertionMode) return;
			if(!chooseMode || arg0.getClickCount()<2){
				//if some asset is checked in list
				if(selectedAssetPreview!=null){
					selectedAssetPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					//if selected asset is checked, remove selection
					if(selectedAssetPreview==this){
						setSelected(false);
					}//else set this asset as selected
					else
						setSelected(true);
				}
				else
					setSelected(true);
			}
			else if(chooseMode)
				linkToAssetManager.dispose();
		}

		public void setSelected(boolean flag){
			if(flag){
				linkToAssetManager.removeAssetJB.setEnabled(true);
				linkToAssetManager.selectAssetJB.setEnabled(chooseMode);
				selectedAssetPreview = this;
				linkToAssetManager.selectedAsset = asset;
				setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
			}
			else{
				selectedAssetPreview = null;
				linkToAssetManager.selectedAsset = null;
				linkToAssetManager.removeAssetJB.setEnabled(false);
				linkToAssetManager.selectAssetJB.setEnabled(false);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent arg0) {}

	}
	
}
