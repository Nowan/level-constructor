import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Prefab {
		
		private String PREFAB_ID;
		
		//How many tiles object takes for a width 
		private int tiledWidth;
		
		//How many tiles object takes for a height
		private int tiledHeight;
		
		private String textureAddress;
		
		private BufferedImage texture;
		
		private String categoryID;
		
		private ArrayList<AdditiveAttribute> additiveAttributes;
		
		private String desctiption;
		
		public Prefab(String ID, String categoryID, int tiledWidth, int tiledHeight, 
				String prefabTexture, String description, ArrayList<AdditiveAttribute> additiveAttributes){
			this.setPrefabID(ID);  
			this.setCategory(categoryID);
			this.setTiledWidth(tiledWidth);
			this.setTiledHeight(tiledHeight);
			this.setTextureAddress(prefabTexture);
			this.setDesctiption(description);
			this.setAdditiveAttributes(additiveAttributes);
			try{
				this.setTexture(ImageIO.read(getClass().getResource(prefabTexture)));
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
		}

		public String getTextureAddress() {
			return textureAddress;
		}
		public void setTextureAddress(String textureAddress) {
			this.textureAddress = textureAddress;
		}
		public BufferedImage getTexture() {
			return texture;
		}
		public void setTexture(BufferedImage texture) {
			this.texture = texture;
		}
		public String getCategoryID() {
			return categoryID;
		}
		public PrefabCategory getCategory() {
			return ConstructorWindow.globals.goBase.prefabCategoryBase.getCategoryByID(categoryID);
		}
		public void setCategory(String categoryID) {
			this.categoryID = categoryID;
		}
		public String getDesctiption() {
			return desctiption;
		}
		public void setDesctiption(String desctiption) {
			this.desctiption = desctiption;
		}
		public int getTiledHeight() {
			return tiledHeight;
		}
		public void setTiledHeight(int tiledHeight) {
			this.tiledHeight = tiledHeight;
		}
		public int getTiledWidth() {
			return tiledWidth;
		}
		public void setTiledWidth(int tiledWidth) {
			this.tiledWidth = tiledWidth;
		}
		public String getPrefabID() {
			return PREFAB_ID;
		}
		public void setPrefabID(String pREFAB_ID) {
			PREFAB_ID = pREFAB_ID;
		}
		public ArrayList<AdditiveAttribute> getAdditiveAttributes() {
			return additiveAttributes;
		}
		public void setAdditiveAttributes(ArrayList<AdditiveAttribute> additiveAttributes) {
			this.additiveAttributes = additiveAttributes;
		}

}
