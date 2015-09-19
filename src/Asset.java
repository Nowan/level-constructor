import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Asset {

	private Atlas atlas;
	private String assetName;
	private ArrayList<String> frameNames;
	private ArrayList<BufferedImage> frameTextures;
	private BufferedImage assetTexture;
	
	public Asset(Atlas atlas, String assetName){
		this.atlas = atlas;
		this.assetName = assetName;
		this.frameTextures = new ArrayList<BufferedImage>();
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

	public ArrayList<String> getFrameNames() {
		return frameNames;
	}
	
	public void setFrames(ArrayList<String> animationFrames) {
		setFrameNames(animationFrames);
		for(int i=0;i<animationFrames.size();i++){
			try{
				String textureAddress = Globals.TEXTURES_FOLDER+atlas.getName()+"/"+animationFrames.get(i)+".png";
				System.out.println("!"+textureAddress);
				frameTextures.add(ImageIO.read(new File(textureAddress)));
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
		}
	}

	public void setFrameNames(ArrayList<String> frameNames) {
		this.frameNames = frameNames;
	}
	
	public ArrayList<BufferedImage> getFrameTextures() {
		return frameTextures;
	}
	
	public boolean hasAnimation(){
		return frameNames.size()>0;
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
