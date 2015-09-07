import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PreviewPanel extends JPanel{

	private static final long serialVersionUID = 6806883799216206977L;
	
	//original image of selected tile
	private BufferedImage image;

	public PreviewPanel(){
		this.setBackground(Color.BLACK);
		this.setSize(getPreferredSize());
		//at start, preview panel is empty
		this.hideImage();
	}
	
	//set image to preview
	public void setImage(BufferedImage image){
		this.image=resizeImage(image);
		repaint();
	}
	
	public void setImage(String address){
		try{
			setImage(ImageIO.read(new File(address)));
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage){
		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage resizedImage = new BufferedImage(this.getWidth(), this.getHeight(), type);
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
	
}
