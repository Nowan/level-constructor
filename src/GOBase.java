import java.util.ArrayList;

public class GOBase{
	
	private XMLConverter xmlConverter = Globals.xmlConverter;

	public static PrefabsBase prefabsBase;
	
	public static PrefabCategoryBase prefabCategoryBase;
	
	public static AssetsBase assetsBase;
	
	public static AtlasesBase atlasesBase;
	
	public GOBase(){
		atlasesBase = new AtlasesBase();
		assetsBase = new AssetsBase();
		prefabCategoryBase = new PrefabCategoryBase();
		prefabsBase = new PrefabsBase();
	}
	
	protected class PrefabsBase extends ArrayList<Prefab>{
		
		private static final long serialVersionUID = 3101554695555133405L;

		protected PrefabsBase(){
			super();
			fill();
		}
		
		//fill the list with data from xml file
		public void fill(){
			for(Prefab p : xmlConverter.loadPrefabBase())
				this.add(p);
		}
		
		//refresh the list
		public void refresh(){
			this.clear();
			this.fill();
		}
		
		public Prefab get(String prefabID){
			for(Prefab p : this){
				if(p.getPrefabID().equals(prefabID))
					return p;
			}
			return null;
		}
		
		//Returns collection of strings to use in the JList
		public String[] getIDCollection(){
			String [] IDcollection = new String[this.size()];
			for(int i=0; i<this.size(); i++)
				IDcollection[i] = this.get(i).getPrefabID();
			return IDcollection;
		}
	}
	
	protected class PrefabCategoryBase extends ArrayList<PrefabCategory>{
		
		private static final long serialVersionUID = 2337950279372944792L;

		protected PrefabCategoryBase(){
			super();
			fill();
		}
		
		//fill the list with data from xml file
		public void fill(){
			for(PrefabCategory p : xmlConverter.loadPrefabCategoryBase())
				this.add(p);
		}
		
		//refresh the list
		public void refresh(){
			this.clear();
			this.fill();
		}
		
		public PrefabCategory get(String name){
			for(PrefabCategory p : this)
				if(p.getName()==name)
					return p;
			return null;
		}
		//Returns collection of strings to use in the JList
		public String[] getNameCollection(){
			String [] IDcollection = new String[this.size()];
			for(int i=0; i<this.size(); i++)
				IDcollection[i] = this.get(i).getName();
			return IDcollection;
		}
		
		public PrefabCategory getCategoryByID(String ID){
			for(PrefabCategory p : this)
				if(p.getID().equals(ID))
					return p;
			return null;
		}
	}

	protected class AtlasesBase extends ArrayList<Atlas>{

		private static final long serialVersionUID = -4537400841401163897L;
		
		protected AtlasesBase(){
			super();
			fill();
		}
		
		//fill the list with data from xml file
		public void fill(){
			for(Atlas a : xmlConverter.loadAtlasesBase())
				this.add(a);
		}
		
		//refresh the list
		public void refresh(){
			this.clear();
			this.fill();
		}
		
		public Atlas get(String atlasName){
			for(Atlas a : this){
				if(a.getName().equals(atlasName))
					return a;
			}
			return null;
		}
		
		public String[] getNamesArray(){
			String[] array = new String [this.size()];
			for(int i=0;i<this.size();i++)
				array[i] = this.get(i).getName();
			return array;
		}
		
		@Override 
		public boolean remove(Object o){
			boolean returnBit = super.remove(o);
			Atlas atlas = (Atlas)o;
			for(Asset a : atlas.getAssets())
				GOBase.assetsBase.remove(a);
			return returnBit;
		}
	}
	
	protected class AssetsBase extends ArrayList<Asset>{

		private static final long serialVersionUID = -4537400841401163897L;
		
		protected AssetsBase(){
			super();
			fill();
		}
		
		//fill the list with data from xml file
		public void fill(){
			for(Atlas atl : atlasesBase)
				for(Asset ast : atl.getAssets())
					this.add(ast);
			
		}
		
		//refresh the list
		public void refresh(){
			this.clear();
			this.fill();
		}
		
		public Asset get(String assetName){
			for(Asset a : this){
				if(a.getAssetName().equals(assetName))
					return a;
			}
			return null;
		}
	}
}
