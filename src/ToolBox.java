public class ToolBox {
	
	public InsertionTool insertionTool;
	
	public ToolBox(){
		insertionTool = new InsertionTool();
	}
	
	class InsertionTool {
		
		private Prefab prefab;
		private boolean active;
	
		private InsertionTool(){
			active = false;
		}
	
		public void invoke(Prefab prefab){
			this.prefab = prefab;
			active = true;
			//ConstructorWindow.instance.toolsPanel.setITButtonActive(active);
		}
	
		public void disable(){
			prefab = null;
			active = false;
			//ConstructorWindow.instance.toolsPanel.setITButtonActive(active);
		}
	
		public Prefab getPrefab(){
			return prefab;
		}
		
		public boolean isActive(){
			return active;
		}
		
	}
}
