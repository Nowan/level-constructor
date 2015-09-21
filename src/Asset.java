import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Asset {

	private Atlas atlas;
	private String assetName;
	private ArrayList<Frame> frames;
	private BufferedImage assetTexture;
	
	public Asset(Atlas atlas, String assetName){
		this.atlas = atlas;
		this.assetName = assetName;
		this.frames = new ArrayList<Frame>();
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

	
	public void setFrames(ArrayList<String> animationFrames) {
		for(int i=0;i<animationFrames.size();i++){
			frames.add(new Frame(animationFrames.get(i)));
		}
	}
	
	public ArrayList<Frame> getFrames() {
		return this.frames;
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
	
	public void addFrame(String frameName){
		frames.add(new Frame(frameName));
	}

	public BufferedImage getFrameTexture(int index) {
		return frames.get(index).getFrameTexture();
	}
	
	public String getFrameName(int index) {
		return frames.get(index).getFrameName();
	}
	
	public boolean hasAnimation() {
		return frames.size()>0;
	}

	public class Frame{
	
		private String frameName;
		
		private BufferedImage frameTexture;
		
		public Frame(String frameName){
			this.setFrameName(frameName);
			try{
				String textureAddress = Globals.TEXTURES_FOLDER+atlas.getName()+"/"+frameName+".png";
				this.setFrameTexture(ImageIO.read(new File(textureAddress)));
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
		}

		public String getFrameName() {
			return frameName;
		}

		public void setFrameName(String frameName) {
			this.frameName = frameName;
		}

		public BufferedImage getFrameTexture() {
			return frameTexture;
		}

		public void setFrameTexture(BufferedImage frameTexture) {
			this.frameTexture = frameTexture;
		}
	}
	
}
