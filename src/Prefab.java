import java.awt.image.BufferedImage;
import java.io.File;
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
		
		//in workspace, after setting the position of current, "master" prefab, user must set of the slave prefab
		private Prefab slavePrefab;
		
		//prefab, to which current prefab is linked to
		private Prefab masterPrefab;
		
		public Prefab(String ID, String categoryID, int tiledWidth, int tiledHeight, 
				String prefabTexture, String description, ArrayList<AdditiveAttribute> additiveAttributes){
			this.setPrefabID(ID);  
			this.setCategory(categoryID);
			this.setTiledWidth(tiledWidth);
			this.setTiledHeight(tiledHeight);
			this.setTextureAddress(prefabTexture);
			this.setDesctiption(description);
			this.setAdditiveAttributes(additiveAttributes);
		}

		public String getTextureAddress() {
			return textureAddress;
		}
		
		public String getTextureName() {
			return textureAddress.substring(textureAddress.lastIndexOf('/')+1);
		}
		
		public void setTextureName(String textureName) {
			setTextureAddress(Globals.TEXTURES_FOLDER+getCategory().getName()+"/"+textureName);
		}
		
		public void setTextureAddress(String textureAddress) {
			this.textureAddress = textureAddress;
			try{
				this.setTexture(ImageIO.read(new File(textureAddress)));
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
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
			return GOBase.prefabCategoryBase.getCategoryByID(categoryID);
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
		
		public boolean isComplex(){return isMaster()||isSlave();}
		
		public boolean isMaster(){return slavePrefab!=null;}
		
		public boolean isSlave(){return masterPrefab!=null;}
		
		public boolean isMaster(Prefab prefab){return slavePrefab==prefab;}
		
		public boolean isSlave(Prefab prefab){return masterPrefab==prefab;}
		
		//If relation is complicated, true means that this is the first prefab in sequence master-slave
		//Master->slave/master->slave/master->slave
		public boolean isRelationStart(){return isMaster()&&!isSlave(); }
		
		//If relation is complicated, true means that this is the last prefab of whole relation
		public boolean isRelationEnd(){return isSlave()&&!isMaster(); }
		
		public void setSlavePrefab(Prefab slavePrefab){
			this.slavePrefab = slavePrefab;
		}
		
		public Prefab getSlavePrefab(){return this.slavePrefab;}
		
		public Prefab getMasterPrefab(){return this.masterPrefab;}
		
		//returns the first master of relation
		public Prefab getRelationMaster(){
			Prefab prefab = this;
			while(!prefab.isRelationStart())
				prefab = prefab.getMasterPrefab();
			return prefab;
		}
		
		public void setMasterPrefab(Prefab masterPrefab){this.masterPrefab = masterPrefab;}
		
		public void removeSlave(){ slavePrefab.setMasterPrefab(null); this.slavePrefab = null; }
		
		public boolean isRelatedTo(Prefab relationPrefab){
			Prefab prefab = this;
			while(prefab!=null){
				if(prefab==relationPrefab)
					return true;
				prefab = prefab.getMasterPrefab();
			}
			prefab = this;
			while(prefab!=null){
				if(prefab==relationPrefab)
					return true;
				prefab = prefab.getSlavePrefab();
			}
			return false;
		}
}
