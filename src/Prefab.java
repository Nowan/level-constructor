import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Prefab {
		
		public String PREFAB_ID;
		
		protected int prefabCategory;
		
		//How many tiles object takes for a width 
		public int tiledWidth;
		
		//How many tiles object takes for a height
		public int tiledHeight;
		
		protected String textureAddress;
		
		protected BufferedImage texture;
		
		public int category;
		
		public String desctiption;
		
		public Prefab(String ID, int category, int tiledWidth, int tiledHeight, String prefabTexture, String description){
			this.PREFAB_ID = ID;  
			this.category=category;
			this.tiledWidth = tiledWidth;
			this.tiledHeight = tiledHeight;
			this.prefabCategory = 0;
			this.textureAddress = prefabTexture;
			this.desctiption = description;
			try{
				this.texture=ImageIO.read(getClass().getResource(textureAddress));
			}
			catch(IOException ex){
				System.out.println(ex.getMessage());
			}
		}

}
