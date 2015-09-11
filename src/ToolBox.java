import java.util.ArrayList;

public class ToolBox {
	
	public InsertionTool insertionTool;
	
	public ToolBox(){
		insertionTool = new InsertionTool();
	}
	
	class InsertionTool {
		
		private Prefab prefab;
		private boolean active;
		
		//if the "slave" prefab is currently selected, this object is used to track the position
		//of "master", to which the new game object will be attached
		public ArrayList<GameObject> relationObjects;
	
		private InsertionTool(){
			relationObjects = new ArrayList<GameObject>();
			active = false;
		}
	
		public void invoke(Prefab prefab){
			relationObjects.clear();
			this.prefab = prefab;
			active = true;
		}
		
		public void invoke(Prefab prefab, GameObject masterObject){
			this.prefab = prefab;
			relationObjects.add(masterObject);
			active = true;
		}
	
		public void disable(){
			prefab = null;
			relationObjects.clear();
			active = false;
		}
	
		public Prefab getPrefab(){
			return prefab;
		}
		
		public GameObject getMasterObject(){
			return relationObjects.get(relationObjects.size()-1);
		}
		
		public boolean hasMasterObject(){
			return relationObjects.size()!=0;
		}
		
		public boolean hasMasterObject(GameObject object){
			return hasMasterObject()&&relationObjects.get(relationObjects.size()-1)==object;
		}
		
		public boolean isActive(){
			return active;
		}
		
	}
}
