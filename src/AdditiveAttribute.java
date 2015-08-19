public class AdditiveAttribute {
	
		private int inListPosition;
	
		private String attributeName;
		
		private String attributeType;
		
		private String attributeValue;
		
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
		
	}