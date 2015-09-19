import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Asset {

	private Atlas atlas;
	private String assetName;
	private ArrayList<String> animationFrames;
	private BufferedImage assetTexture;
	
	public Asset(Atlas atlas, String assetName){
		this.atlas = atlas;
		this.assetName = assetName;
		String textureAddress = Globals.TEXTURES_FOLDER+atlas.getName()+"/"+assetName+".png";
		try{
			this.setAssetTexture(ImageIO.read(new File(textureAddress)));
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public ArrayList<String> getAnimationFrames() {
		return animationFrames;
	}

	public void setAnimationFrames(ArrayList<String> animationFrames) {
		this.animationFrames = animationFrames;
	}
	
	public boolean isAnimated(){
		return animationFrames.size()>0;
	}

	public BufferedImage getAssetTexture() {
		return assetTexture;
	}

	public void setAssetTexture(BufferedImage assetTexture) {
		this.assetTexture = assetTexture;
	}

	public Atlas getAtlas() {
		return atlas;
	}

}
