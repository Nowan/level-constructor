import java.util.ArrayList;

public class PrefabCategory { 
	
	private String name;
	
	private String id;
	
	private boolean isObstacle;
	
	private AdditiveAttributesList additiveAttributes;
	
	public PrefabCategory(String id, String name, boolean isObstacle, ArrayList<AdditiveAttribute> additiveAttributes){
		this.setName(name);
		this.setID(id);
		this.isObstacle = isObstacle;
		this.setAdditiveAttributes(additiveAttributes);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
	
	public boolean getObstacleBit() {
		return this.isObstacle;
	}
	
	public void setObstacleBit(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}

	public ArrayList<AdditiveAttribute> getAdditiveAttributes() {
		return additiveAttributes;
	}
	
	public int getAdditiveAttributeInList(AdditiveAttribute attribute) {
		for(int i=0;i<additiveAttributes.size();i++)
			if(additiveAttributes.get(i)==attribute)
				return i;
		return 0;
	}

	public void setAdditiveAttributes(ArrayList<AdditiveAttribute> additiveAttributes) {
		this.additiveAttributes = new AdditiveAttributesList(additiveAttributes);
	}

	public class AdditiveAttributesList extends ArrayList<AdditiveAttribute>{
		
		public AdditiveAttributesList(){
			super();
		}
		
		public AdditiveAttributesList(ArrayList<AdditiveAttribute> arg){
			super();
			if(arg!=null)
			for(AdditiveAttribute a : arg)
				this.add(a);
		}
		
		@Override
		public boolean add(AdditiveAttribute e){
			boolean rb = super.add(e);
			e.setInListPosition(this.size()-1);
			return rb;
		}
		
		@Override
		public AdditiveAttribute remove(int index){
			AdditiveAttribute ra = super.remove(index);
			for(int i=0;i<this.size();i++){
				this.get(i).setInListPosition(i);
			}
			return ra;
		}
	}
}
