import java.util.ArrayList;

public class Atlas{
	
	private String name;
	
	private ArrayList<Asset> assets;
	
	public Atlas(String atlasName){
		this.setName(atlasName);
		assets = new ArrayList<Asset>();
	}

	public ArrayList<Asset> getAssets() {
		return assets;
	}

	public void setAssets(ArrayList<Asset> assets) {
		this.assets = assets;
	}

	public String getName() {
		return name;
	}

	public void setName(String atlasName) {
		this.name = atlasName;
	}
	
	
}
