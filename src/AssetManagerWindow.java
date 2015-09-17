import java.awt.Color;
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
	
	//true means that window was created from GOManager, which means user can add/remove assets from all categories
	//false means that window was created from ParticleManager, which means user must select a texture from selected category
	private boolean chooseMode;
	
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
		
		setContentPane(generateContent());
		if(selectedAtlasJCB.getItemCount()!=0)
			selectedAtlasJCB.setSelectedIndex(0);
		chooseMode=false;
		
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
		
		PreviewPanel mainPreview = new PreviewPanel();
		mainPreview.setPreferredSize(new Dimension(detailedPreviewJP.getPreferredSize().width-5,300));
		
		JPanel assetPartsJP = new JPanel();
		assetPartsJP.setPreferredSize(new Dimension(600,100));
		assetPartsJP.setBackground(Color.BLACK);
		
		JScrollPane assetPartsJSP = new JScrollPane(assetPartsJP);
		assetPartsJSP.setPreferredSize(new Dimension(detailedPreviewJP.getPreferredSize().width-4,120));
		assetPartsJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		assetPartsJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		detailedPreviewSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, mainPreview, 0, SpringLayout.HORIZONTAL_CENTER, detailedPreviewJP);
		detailedPreviewJP.add(mainPreview);
		
		detailedPreviewSL.putConstraint(SpringLayout.NORTH, assetPartsJSP, 0, SpringLayout.SOUTH, mainPreview);
		detailedPreviewSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, assetPartsJSP, 0, SpringLayout.HORIZONTAL_CENTER, detailedPreviewJP);
		detailedPreviewJP.add(assetPartsJSP);
		
		JPanel lastSectionJP = new JPanel();
		lastSectionJP.setPreferredSize(new Dimension(173,getHeight()-27));
		SpringLayout lastSectionSL = new SpringLayout();
		lastSectionJP.setLayout(lastSectionSL);
		
		JPanel atlasManagementJP = new JPanel();
		atlasManagementJP.setPreferredSize(new Dimension(170,145));
		atlasManagementJP.setBorder(BorderFactory.createTitledBorder("Atlas management"));
		
		JButton rebuildAtlasJB = new JButton("Rebuild atlas");
		rebuildAtlasJB.setFont(Globals.DEFAULT_FONT);
		rebuildAtlasJB.setPreferredSize(new Dimension(150,34));
		rebuildAtlasJB.setContentAreaFilled(false);
		atlasManagementJP.add(rebuildAtlasJB);
		
		JButton removeAtlasJB = new JButton("Remove atlas");
		removeAtlasJB.setFont(Globals.DEFAULT_FONT);
		removeAtlasJB.setPreferredSize(new Dimension(150,34));
		removeAtlasJB.setContentAreaFilled(false);
		atlasManagementJP.add(removeAtlasJB);
		
		JButton addAtlasJB = new JButton("Create new atlas");
		addAtlasJB.setFont(Globals.DEFAULT_FONT);
		addAtlasJB.setPreferredSize(new Dimension(150,34));
		addAtlasJB.setContentAreaFilled(false);
		atlasManagementJP.add(addAtlasJB);
		
		lastSectionSL.putConstraint(SpringLayout.NORTH, atlasManagementJP, 10, SpringLayout.NORTH, lastSectionJP);
		lastSectionSL.putConstraint(SpringLayout.HORIZONTAL_CENTER, atlasManagementJP, 0, SpringLayout.HORIZONTAL_CENTER, lastSectionJP);
		lastSectionJP.add(atlasManagementJP);
		
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
				NewAssetDialog newAssetDialog = new NewAssetDialog(selectedAtlasJCB.getSelectedItem().toString());
				boolean operationSuccess = newAssetDialog.showDialog();
				if(operationSuccess){
					rebuildAtlas();
					onAtlasChanged();
				}
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
