public class AdditiveAttribute {
	
		private int inListPosition;
	
		private String attributeName;
		
		private String attributeType;
		
		private String attributeValue;
		
		public AdditiveAttribute(String name, String type){
			this.setAttributeName(name);
			this.setAttributeType(type);
			this.setDefaultValue();
		}
		
		public AdditiveAttribute(String name, String type, String value){
			this.setAttributeName(name);
			this.setAttributeType(type);
			this.setAttributeValue(value);
		}

		public String getAttributeName() {
			return attributeName;
		}

		public void setAttributeName(String attributeName) {
			this.attributeName = attributeName;
		}

		public String getAttributeType() {
			return attributeType;
		}

		public void setAttributeType(String attributeType) {
			this.attributeType = attributeType;
			this.setDefaultValue();
		}

		public String getAttributeValue() {
			return attributeValue;
		}
		
		public void setInListPosition(int index) {
			this.inListPosition = index;
		}

		public int getInListPosition() {
			return inListPosition;
		}
		
		public Object getConvertedValue() {
			switch(getAttributeType()){
			case "boolean":
				return Boolean.valueOf(getAttributeName());
			case "integer":
				return Integer.valueOf(getAttributeName());
			case "double":
				return Double.valueOf(getAttributeName());
			case "String":
				return getAttributeName();
			default:
				return getAttributeName();
			}
		}

		public void setAttributeValue(String attributeValue) {
			this.attributeValue = attributeValue;
		}
		
		public void setDefaultValue(){
			switch(attributeType){
			case "boolean":
				this.setAttributeValue("false");
				break;
			case "integer":
				this.setAttributeValue("1");
				break;
			case "double":
				this.setAttributeValue("0.5");
				break;
			case "sString":
				this.setAttributeValue("Short text");
				break;
			case "lString":
				this.setAttributeValue("Long text");
				break;
			}
		}
		
	}