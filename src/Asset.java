import java.util.ArrayList;

public class Asset {

	private String atlasName;
	private String assetName;
	private ArrayList<String> animationFrames;
	
	public Asset(String atlasName, String assetName){
		this.atlasName = atlasName;
		this.assetName = assetName;
	}

	public String getAtlasName() {
		return atlasName;
	}

	public void setAtlasName(String atlasName) {
		this.atlasName = atlasName;
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
}
