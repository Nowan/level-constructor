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
	private PreviewPanel texturePreviewJP;
	private JButton rebuildAtlasJB;
	private JTextField textureAddressJTF;
	
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
		setSize(600,300);
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
		setSize(600,300);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setResizable(false);
		
		setContentPane(generateContent());
		
		if(selectedAtlasJCB.getItemCount()!=0)
			selectedAtlasJCB.setSelectedItem(categoryName);
		selectedAtlasJCB.setEnabled(false);
		
		fileChooser=new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("image", new String[] {"png","jpeg","jpg","gif"});
		fileChooser.setFileFilter(filter);
		chooseMode=true;
		
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
		previewsJP.setPreferredSize(new Dimension(400,panel.getPreferredSize().height));
		
		JScrollPane previewsJSP = new JScrollPane(previewsJP);
		previewsJSP.setPreferredSize(new Dimension(400,panel.getPreferredSize().height));
		previewsJSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		previewsJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel optionsJP = new JPanel();
		optionsJP.setPreferredSize(new Dimension(panel.getPreferredSize().width-previewsJSP.getPreferredSize().width-5,panel.getPreferredSize().height-30));
		SpringLayout optionsSL = new SpringLayout();
		optionsJP.setLayout(optionsSL);
		
		JLabel selectedAtlasJL = new JLabel("Selected atlas:");
		selectedAtlasJL.setFont(Globals.PARAMETER_FONT);
		selectedAtlasJL.setPreferredSize(new Dimension(optionsJP.getPreferredSize().width,15));
		selectedAtlasJL.setHorizontalAlignment(JLabel.CENTER);
		
		selectedAtlasJCB = new JComboBox<String>(GOBase.prefabCategoryBase.getNameCollection());
		selectedAtlasJCB.setFont(Globals.DEFAULT_FONT);
		selectedAtlasJCB.setPreferredSize(new Dimension(optionsJP.getPreferredSize().width,20));
		selectedAtlasJCB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onAtlasChanged();
			}
		});
		
		JPanel addAssetJP = new JPanel();
		addAssetJP.setPreferredSize(new Dimension(optionsJP.getPreferredSize().width,210));
		addAssetJP.setBorder(BorderFactory.createTitledBorder("Add new asset"));
		SpringLayout addAssetSL = new SpringLayout();
		addAssetJP.setLayout(addAssetSL);
		
		textureAddressJTF = new JTextField("choose texture...");
		textureAddressJTF.setFont(Globals.DEFAULT_FONT);
		textureAddressJTF.setEnabled(false);
		textureAddressJTF.setPreferredSize(new Dimension(145,22));
		
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
							rebuildAtlasJB.setEnabled(true);
						}
						catch(IOException ex){
							System.out.println(ex.getMessage());
						}
			    }
			}
		});
		
		texturePreviewJP = new PreviewPanel();
		texturePreviewJP.setPreferredSize(new Dimension(185,145));
		
		rebuildAtlasJB = new JButton("Rebuild atlas");
		rebuildAtlasJB.setFont(Globals.PARAMETER_FONT);
		rebuildAtlasJB.setPreferredSize(new Dimension(185,22));
		rebuildAtlasJB.setContentAreaFilled(false);
		rebuildAtlasJB.setEnabled(false);
		rebuildAtlasJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onRebuildAtlasClicked();
			}
		});
		
		addAssetJP.add(textureAddressJTF);
		addAssetSL.putConstraint(SpringLayout.WEST, textureAddressJB, 0, SpringLayout.EAST, textureAddressJTF);
		addAssetJP.add(textureAddressJB);
		addAssetSL.putConstraint(SpringLayout.NORTH, texturePreviewJP, 0, SpringLayout.SOUTH, textureAddressJTF);
		addAssetJP.add(texturePreviewJP);
		addAssetSL.putConstraint(SpringLayout.NORTH, rebuildAtlasJB, 0, SpringLayout.SOUTH, texturePreviewJP);
		addAssetJP.add(rebuildAtlasJB);
		
		optionsSL.putConstraint(SpringLayout.NORTH, selectedAtlasJL, 5, SpringLayout.NORTH, optionsJP);
		optionsJP.add(selectedAtlasJL);
		optionsSL.putConstraint(SpringLayout.NORTH, selectedAtlasJCB, 5, SpringLayout.SOUTH, selectedAtlasJL);
		optionsJP.add(selectedAtlasJCB);
		optionsSL.putConstraint(SpringLayout.SOUTH, addAssetJP, 0, SpringLayout.SOUTH, optionsJP);
		optionsJP.add(addAssetJP);
		
		panel.add(previewsJSP);
		slayout.putConstraint(SpringLayout.EAST, optionsJP, 0, SpringLayout.EAST, panel);
		panel.add(optionsJP);
		
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
		if (directoryListing != null) 
			for (File child : directoryListing) {
				if(child.getName().endsWith(".png"))
					previewsJP.add(new AssetPreview(child));
			}
		previewsJP.revalidate();
		previewsJP.repaint();
	}
	
	private void onRebuildAtlasClicked(){
		//copy texture image to the textures folder
		String textureAddress = textureAddressJTF.getText();
		
		//generating texture name in form "categoryindex-number.png"
		String categoryIndex=GOBase.prefabCategoryBase.get(selectedAtlasJCB.getSelectedItem().toString()).getID();
		int lastIndex = 0;
		File dir = new File(Globals.TEXTURES_FOLDER+selectedAtlasJCB.getSelectedItem().toString());
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
		
		String textureAddress2 = Globals.TEXTURES_FOLDER+selectedAtlasJCB.getSelectedItem().toString()+"/"+generatedTextureName+textureAddress.substring(textureAddress.lastIndexOf('.'));
		
		if(!textureAddress.equals(textureAddress2))
			try{
				Files.copy(Paths.get(textureAddress),Paths.get(textureAddress2), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(Exception ex){
				System.out.println(ex.getMessage());
			}
		
		rebuildAtlas();
		
		texturePreviewJP.hideImage();
		textureAddressJTF.setText("choose texture...");
		rebuildAtlasJB.setEnabled(false);
		
		onAtlasChanged();
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
		
		private BufferedImage texture;
		private String textureAddress;
		private String textureName;
		
		public AssetPreview(File file){
			super();
			setPreferredSize(new Dimension(100,120));
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
