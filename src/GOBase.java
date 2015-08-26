import java.util.ArrayList;

public class GOBase{
	
	private XMLConverter xmlConverter = ConstructorWindow.instance.globals.xmlConverter;

	public static PrefabsBase prefabsBase;
	
	public static PrefabCategoryBase prefabCategoryBase;
	
	public GOBase(){
		prefabCategoryBase=new PrefabCategoryBase();
		prefabsBase=new PrefabsBase();
	}
	
	protected class PrefabsBase extends ArrayList<Prefab>{
		
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
}
