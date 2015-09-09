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
		private GameObject masterGameObject;
	
		private InsertionTool(){
			active = false;
		}
	
		public void invoke(Prefab prefab){
			masterGameObject=null;
			this.prefab = prefab;
			active = true;
		}
		
		public void invoke(Prefab prefab, GameObject masterObject){
			this.prefab = prefab;
			this.masterGameObject=masterObject;
			active = true;
		}
	
		public void disable(){
			prefab = null;
			masterGameObject=null;
			active = false;
		}
	
		public Prefab getPrefab(){
			return prefab;
		}
		
		public GameObject getMasterObject(){
			return masterGameObject;
		}
		
		public boolean hasMasterObject(){
			return masterGameObject!=null;
		}
		
		public boolean hasMasterObject(GameObject object){
			return hasMasterObject()&&masterGameObject==object;
		}
		
		public boolean isActive(){
			return active;
		}
		
	}
}
