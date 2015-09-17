import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class NewAssetDialog extends JDialog{
	
	private PreviewPanel texturePreviewJP;
	private JButton confirmJB;
	private JButton discardJB;
	private JTextField textureAddressJTF;
	private JFileChooser fileChooser;
	
	private String categoryName;
	private String categoryIndex;
	
	private boolean returnBit;
	
	public NewAssetDialog(String categoryName){
		super(ConstructorWindow.instance, "New Asset");
		this.setModal(true);
		setSize(200,250);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ConstructorWindow.instance);
		setResizable(false);
		
		setContentPane(generateContent());
		
		this.categoryName = categoryName;
		this.categoryIndex = GOBase.prefabCategoryBase.get(categoryName).getID();
		
		fileChooser=new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("image", new String[] {"png","jpeg","jpg","gif"});
		fileChooser.setFileFilter(filter);
		
		returnBit=false;
		
		setVisible(false);
	}
	
	private JPanel generateContent(){
		JPanel addAssetJP = new JPanel();
		addAssetJP.setPreferredSize(getPreferredSize());
		SpringLayout addAssetSL = new SpringLayout();
		addAssetJP.setLayout(addAssetSL);
		
		textureAddressJTF = new JTextField("choose texture...");
		textureAddressJTF.setFont(Globals.DEFAULT_FONT);
		textureAddressJTF.setEnabled(false);
		textureAddressJTF.setPreferredSize(new Dimension(156,22));
		
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
		texturePreviewJP.setPreferredSize(new Dimension(196,175));
		
		confirmJB = new JButton("Confirm");
		confirmJB.setFont(Globals.PARAMETER_FONT);
		confirmJB.setPreferredSize(new Dimension(98,25));
		confirmJB.setEnabled(false);
		confirmJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onConfirmClicked();
			}
		});
		
		discardJB = new JButton("Discard");
		discardJB.setFont(Globals.PARAMETER_FONT);
		discardJB.setPreferredSize(new Dimension(98,25));
		discardJB.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onDiscardClicked();
			}
		});
		
		addAssetJP.add(textureAddressJTF);
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
		return addAssetJP;
	}
	
	public boolean showDialog(){
		setVisible(true);
		
		return returnBit;
	}
	
	private void onConfirmClicked(){
		
		//copy texture image to the textures folder
		String textureAddress = textureAddressJTF.getText();
		
		
		//generating texture name in form "categoryindex-number.png"
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
		
		returnBit = true;
		dispose();
	}
	
	private void onDiscardClicked(){
		returnBit = false;
		dispose();
	}
	
}
