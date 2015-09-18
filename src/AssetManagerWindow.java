import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	
	private JComboBox<String> selectedAtlasJCB;
	private JPanel previewsJP;
	private JPanel atlasManagementJP;
	private JPanel assetManagementJP;
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
	
	//Contains the name of selected texture, which will be passed back to PrefabManager
	private String selectedTexture;
	
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
		if(selectedAtlasJCB.getItemCount()!=0)
			selectedAtlasJCB.setSelectedIndex(0);
		
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
		if(selectedAtlasJCB.getItemCount()!=0)
			selectedAtlasJCB.setSelectedItem(categoryName);
		selectedAtlasJCB.setEnabled(false);
		
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
		
		JScrollPane previewsJSP = new JScrollPane(previewsJP);
		previewsJSP.setPreferredSize(new Dimension(180,398));
		previewsJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		previewsJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		selectedAtlasJCB = new JComboBox<String>(GOBase.prefabCategoryBase.getNameCollection());
		selectedAtlasJCB.setFont(Globals.DEFAULT_FONT);
		selectedAtlasJCB.setPreferredSize(new Dimension(previewsJSP.getPreferredSize().width,25));
		selectedAtlasJCB.addActionListener(new ActionListener(){
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
		
		JTextField atlasName = new JTextField();
		atlasName.setToolTipText("Name of atlas to create");
		atlasName.setFont(Globals.DEFAULT_FONT);
		atlasName.setPreferredSize(new Dimension(150,22));
		atlasManagementJP.add(atlasName);
		
		JButton addAtlasJB = new JButton("Create atlas");
		addAtlasJB.setToolTipText("Creates an atlas with provided name");
		addAtlasJB.setFont(Globals.DEFAULT_FONT);
		addAtlasJB.setPreferredSize(new Dimension(150,34));
		addAtlasJB.setContentAreaFilled(false);
		atlasManagementJP.add(addAtlasJB);
		
		atlasManagementJP.add(new JLabel("  "));
		
		JButton rebuildAtlasJB = new JButton("Rebuild atlas");
		rebuildAtlasJB.setToolTipText("Rebuilds selected atlas");
		rebuildAtlasJB.setFont(Globals.DEFAULT_FONT);
		rebuildAtlasJB.setPreferredSize(new Dimension(150,34));
		rebuildAtlasJB.setContentAreaFilled(false);
		atlasManagementJP.add(rebuildAtlasJB);
		
		JButton removeAtlasJB = new JButton("Remove atlas");
		removeAtlasJB.setToolTipText("Removes selected atlas and all textures it contains");
		removeAtlasJB.setFont(Globals.DEFAULT_FONT);
		removeAtlasJB.setPreferredSize(new Dimension(150,34));
		removeAtlasJB.setContentAreaFilled(false);
		atlasManagementJP.add(removeAtlasJB);
		
		assetManagementJP = new JPanel();
		assetManagementJP.setPreferredSize(new Dimension(170,104));
		assetManagementJP.setBorder(BorderFactory.createTitledBorder("Asset management"));
		
		JButton selectAssetJB = new JButton("✔     Select asset");
		selectAssetJB.setToolTipText("Select current asset as prefab texture");
		selectAssetJB.setFont(Globals.DEFAULT_FONT);
		selectAssetJB.setPreferredSize(new Dimension(150,34));
		selectAssetJB.setContentAreaFilled(false);
		selectAssetJB.setEnabled(chooseMode);
		assetManagementJP.add(selectAssetJB);
		
		JButton removeAssetJB = new JButton("✗     Remove asset");
		removeAssetJB.setToolTipText("Remove current asset from atlas");
		removeAssetJB.setFont(Globals.DEFAULT_FONT);
		removeAssetJB.setPreferredSize(new Dimension(150,34));
		removeAssetJB.setContentAreaFilled(false);
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
		
		panel.add(selectedAtlasJCB);
		slayout.putConstraint(SpringLayout.NORTH, previewsJSP, 0, SpringLayout.SOUTH, selectedAtlasJCB);
		panel.add(previewsJSP);
		slayout.putConstraint(SpringLayout.WEST, detailedPreviewJP, 0, SpringLayout.EAST, previewsJSP);
		panel.add(detailedPreviewJP);
		slayout.putConstraint(SpringLayout.WEST, lastSectionJP, 0, SpringLayout.EAST, detailedPreviewJP);
		panel.add(lastSectionJP);
		
		return panel;
	}
	
	public String showDialog(){
		setVisible(true);
		return selectedTexture;
	}
	
	private void setInsertionMode(boolean flag){
		insertionMode = flag;
		atlasManagementJP.setVisible(!insertionMode);
		for(Component c:atlasManagementJP.getComponents())
			c.setVisible(!insertionMode);
		assetManagementJP.setVisible(!insertionMode);
		for(Component c:assetManagementJP.getComponents())
			c.setVisible(!insertionMode);
		addAssetJP.setVisible(insertionMode);
		for(Component c:addAssetJP.getComponents())
			c.setVisible(insertionMode);
		
		if(insertionMode){
			detailedMainPreview.setBackground(Color.DARK_GRAY);
			assetPartsJP.setBackground(Color.GRAY);
		}
		else{
			detailedMainPreview.setBackground(Color.BLACK);
			assetPartsJP.setBackground(Color.BLACK);
		}
	}
	
	private void onAtlasChanged(){
		previewsJP.removeAll();
		//checking the folder with textures
		File dir = new File(Globals.TEXTURES_FOLDER+selectedAtlasJCB.getSelectedItem().toString());
		File[] directoryListing = dir.listFiles();
		int newHeight=0;
		if (directoryListing != null) 
			for (File child : directoryListing) {
				if(child.getName().endsWith(".png")){
					previewsJP.add(new AssetPreview(child));
					newHeight+=AssetPreview.HEIGHT+15;
				}
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
				setInsertionMode(true);
				
			}
		});
		
		newHeight+=addAssetJB.getPreferredSize().height+20;
		previewsJP.add(addAssetJB);
		previewsJP.setPreferredSize(new Dimension(100,newHeight));
		previewsJP.revalidate();
		previewsJP.repaint();
	}
	
	private void rebuildAtlas(){
		Settings settings = new Settings();
		//settings.maxWidth=1024;
		//settings.maxHeight=1024;
		TexturePacker.process(settings, 
			Globals.TEXTURES_FOLDER+selectedAtlasJCB.getSelectedItem().toString(), 
			Globals.ATLASES_FOLDER, selectedAtlasJCB.getSelectedItem().toString());
	}
	
	private void onConfirmClicked(){
		
		//copy texture image to the textures folder
		String textureAddress = textureAddressJTF.getText();
		
		String categoryName = selectedAtlasJCB.getSelectedItem().toString();
		//generating texture name in form "categoryindex-number.png"
		String categoryIndex = GOBase.prefabCategoryBase.get(categoryName).getID();
		
		int lastIndex = 0;
		File dir = new File(Globals.TEXTURES_FOLDER+categoryName);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) 
			for (File child : directoryListing) {
				if(child.getName().endsWith(".png")){
					//System.out.println(child.getName().toString().substring(child.getName().length()-6, child.getName().length()-4));
					int indx = Integer.valueOf(child.getName().toString().substring(child.getName().length()-6, child.getName().length()-4));
					if(indx>lastIndex)
						lastIndex=indx;
				}
			}
		
		String generatedTextureName = categoryIndex+"-"+String.format("%02d", lastIndex+1);
		
		String textureAddress2 = Globals.TEXTURES_FOLDER+categoryName+"/"+generatedTextureName+textureAddress.substring(textureAddress.lastIndexOf('.'));
		
		if(!textureAddress.equals(textureAddress2))
			try{
				Files.copy(Paths.get(textureAddress),Paths.get(textureAddress2), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		
		onAtlasChanged();
		onDiscardClicked();
	}
	
	private void onDiscardClicked(){
		texturePreviewJP.hideImage();
		textureAddressJTF.setText("choose texture...");;
		setInsertionMode(false);
	}
	
	private class AssetPreview extends PreviewPanel implements MouseListener{
		private static final long serialVersionUID = -701096237525672995L;
		private JButton removeJB;
		private JButton chooseJB;
		
		public static final int WIDTH = 140;
		public static final int HEIGHT = 120;
		
		private BufferedImage texture;
		private String textureAddress;
		private String textureName;
		
		public AssetPreview(File file){
			super();
			setPreferredSize(new Dimension(WIDTH,HEIGHT));
			setSize(getPreferredSize());
			
			try{
				texture = ImageIO.read(file);
				textureName = file.getName();
				textureAddress = file.getPath();
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
			setImage(texture);
			addMouseListener(this);
			
			SpringLayout slayout = new SpringLayout();
			setLayout(slayout);
			
			removeJB = new JButton("✗");
			removeJB.setPreferredSize(new Dimension(20,20));
			removeJB.setMargin(new Insets(0,0,0,0));
			removeJB.setFont(Globals.PARAMETER_FONT);
			removeJB.setForeground(Color.WHITE);
			removeJB.setBackground(Color.BLACK);
			removeJB.setToolTipText("Delete Asset");
			removeJB.setVisible(false);
			removeJB.addMouseListener(this);
			removeJB.addMouseListener(new MouseListener(){
				@Override public void mouseClicked(MouseEvent arg0) {}

				@Override public void mouseEntered(MouseEvent arg0) {}

				@Override public void mouseExited(MouseEvent arg0) {}

				@Override
				public void mousePressed(MouseEvent arg0) {
					new JOptionPane();
					int n = JOptionPane.showConfirmDialog(
						  	ConstructorWindow.instance,
						    "Are you sure you want to delete this asset? There may be objects that use it",
						    "Message",
						    JOptionPane.YES_NO_OPTION);
					if(n==0)
						try{
							Files.delete(Paths.get(textureAddress));
							rebuildAtlas();
							onAtlasChanged();
						}
						catch(IOException ex){
							System.out.println(ex.getStackTrace());
						}
				}

				@Override public void mouseReleased(MouseEvent arg0) {}
			});
			
			chooseJB = new JButton("✔");
			chooseJB.setPreferredSize(new Dimension(20,20));
			chooseJB.setMargin(new Insets(0,0,0,0));
			chooseJB.setFont(Globals.PARAMETER_FONT);
			chooseJB.setForeground(Color.WHITE);
			chooseJB.setBackground(Color.BLACK);
			chooseJB.setVisible(false);
			chooseJB.addMouseListener(this);
			chooseJB.addMouseListener(new MouseListener(){
				@Override public void mouseClicked(MouseEvent arg0) {}
				@Override public void mouseEntered(MouseEvent arg0) {}
				@Override public void mouseExited(MouseEvent arg0) {}
				@Override public void mouseReleased(MouseEvent arg0) {}
				@Override
				public void mousePressed(MouseEvent arg0) {
					linkToAssetManager.selectedTexture = textureName;
					linkToAssetManager.dispose();
				}
			});
			
			slayout.putConstraint(SpringLayout.SOUTH, chooseJB, 0, SpringLayout.SOUTH, this);
			slayout.putConstraint(SpringLayout.EAST, chooseJB, 0, SpringLayout.EAST, this);
			add(chooseJB);
			slayout.putConstraint(SpringLayout.EAST, removeJB, 0, SpringLayout.EAST, this);
			add(removeJB);
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			chooseJB.setVisible(chooseMode);
			removeJB.setVisible(true);
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			chooseJB.setVisible(false);
			removeJB.setVisible(false);
			setBorder(BorderFactory.createEmptyBorder());
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if(chooseMode&&arg0.getClickCount()==2){
				linkToAssetManager.selectedTexture = textureName;
				linkToAssetManager.dispose();
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {}

	}
	
}
