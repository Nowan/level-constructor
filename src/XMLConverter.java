import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLConverter {
	
	private static final String PREFAB_BASE_ADDRESS = "src/resourses/prefabsbase.xml";
	
	private static final String PREFAB_CATEGORY_BASE_ADDRESS = "src/resourses/prefabcategorybase.xml";
	
	private GOBase goBase = ConstructorWindow.instance.globals.goBase;
	
	private File fXmlFile;
	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private Document doc;
	
	public XMLConverter(){
		try{
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
		}
		catch(Exception ex){
			System.out.println(ex.toString());
		}
	}
	
	//Returns list of prefabs currently existing in prafabsbase.xml file
	public ArrayList<Prefab> loadPrefabBase(){
		try {
			fXmlFile = new File(PREFAB_BASE_ADDRESS);
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			ArrayList<Prefab> prefabs = new ArrayList<Prefab>();

			NodeList nList = doc.getElementsByTagName("prefab");
					
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String ID = eElement.getAttribute("id");
					String categoryID = eElement.getAttribute("category");
					int tiledWidth = Integer.parseInt(eElement.getAttribute("tiledwidth"));
					int tiledHeight = Integer.parseInt(eElement.getAttribute("tiledheight"));
					String textureAddress= eElement.getElementsByTagName("texture").item(0).getTextContent();
					String description= eElement.getElementsByTagName("description").item(0).getTextContent();
					
					ArrayList<AdditiveAttribute> additiveAttributes = new ArrayList<AdditiveAttribute>();
					NodeList nList2 = eElement.getElementsByTagName("additiveattribute");
					for (int temp2 = 0; temp2 < nList2.getLength(); temp2++) {
						Node nNode2 = nList2.item(temp2);
						if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement2 = (Element) nNode2;
							String aaName = eElement2.getAttribute("name");
							String aaType = eElement2.getAttribute("type");
							String aaValue = eElement2.getAttribute("value");
							additiveAttributes.add(new AdditiveAttribute(aaName,aaType,aaValue));
						}
					}
					
					prefabs.add(new Prefab(ID,categoryID,tiledWidth,tiledHeight,textureAddress,description,additiveAttributes));
				}
			}
			return prefabs;
		    } catch (Exception e) {
			e.printStackTrace();
			return null;
		    }
	}
	
	public boolean savePrefabBase(){
		try {
			// root elements
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("prefabsbase");
			doc.appendChild(rootElement);

			for(Prefab P : goBase.prefabsBase){;
				Element prefab = doc.createElement("prefab");
				rootElement.appendChild(prefab);

				// set attribute to staff element
				prefab.setAttribute("id", P.getPrefabID());
				prefab.setAttribute("category", P.getCategoryID());
				prefab.setAttribute("tiledwidth", String.valueOf(P.getTiledWidth()));
				prefab.setAttribute("tiledheight", String.valueOf(P.getTiledHeight()));
				
				Element texture = doc.createElement("texture");
				texture.appendChild(doc.createTextNode(P.getTextureAddress()));
				prefab.appendChild(texture);
				
				Element description = doc.createElement("description");
				description.appendChild(doc.createTextNode(P.getDesctiption()));
				prefab.appendChild(description);
				
				for(AdditiveAttribute a : P.getAdditiveAttributes()){
					Element additiveAttribute = doc.createElement("additiveattribute");
					additiveAttribute.setAttribute("name", a.getAttributeName().toLowerCase());
					additiveAttribute.setAttribute("type", a.getAttributeType());
					additiveAttribute.setAttribute("value", a.getAttributeValue());
					prefab.appendChild(additiveAttribute);
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(PREFAB_BASE_ADDRESS));
			
			transformer.transform(source, result);
			
			return true;
		} 
		catch (Exception ex) {
			  ex.printStackTrace();
			  
			  return false;
		}
	}

	public ArrayList<PrefabCategory> loadPrefabCategoryBase(){
		try {
			fXmlFile = new File(PREFAB_CATEGORY_BASE_ADDRESS);
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			ArrayList<PrefabCategory> prefabCategoryBase = new ArrayList<PrefabCategory>();

			NodeList nList = doc.getElementsByTagName("prefabcategory");
					
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
					String ID = eElement.getAttribute("id");
					String name = eElement.getAttribute("name");
					boolean isObstacle = Boolean.valueOf(eElement.getAttribute("isobstacle"));
					
					ArrayList<AdditiveAttribute> aaList = new ArrayList<AdditiveAttribute>();
					NodeList nList2 = eElement.getElementsByTagName("additiveattribute");
					for(int i=0; i<nList2.getLength();i++){
						Node nNode2 = nList2.item(i);
						if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement2 = (Element) nNode2;
							String aaName = eElement2.getAttribute("name");
							String aaType = eElement2.getAttribute("type");
							String aaValue = eElement2.getAttribute("defaultvalue");
							
							aaList.add(new AdditiveAttribute(aaName,aaType,aaValue));
						}
					}
					
					prefabCategoryBase.add(new PrefabCategory(ID,name,isObstacle,aaList));
				}
			}
			return prefabCategoryBase;
		    } catch (Exception e) {
			e.printStackTrace();
			return null;
		    }
	}
	
	public boolean savePrefabCategoryBase(){
		try {
			// root elements
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("prefabcategorybase");
			doc.appendChild(rootElement);

			for(PrefabCategory P : ConstructorWindow.instance.globals.goBase.prefabCategoryBase){
				Element prefabCategory = doc.createElement("prefabcategory");
				rootElement.appendChild(prefabCategory);

				prefabCategory.setAttribute("id", P.getID());
				prefabCategory.setAttribute("name", P.getName());
				prefabCategory.setAttribute("isObstacle", String.valueOf(P.getObstacleBit()));
				for(AdditiveAttribute a : P.getAdditiveAttributes()){
					Element additiveAttribute = doc.createElement("additiveattribute");
					additiveAttribute.setAttribute("name", a.getAttributeName().toLowerCase());
					additiveAttribute.setAttribute("type", a.getAttributeType());
					additiveAttribute.setAttribute("defaultvalue", a.getAttributeValue());
					prefabCategory.appendChild(additiveAttribute);
				}
			}
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(PREFAB_CATEGORY_BASE_ADDRESS));

			transformer.transform(source, result);

			return true;
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
