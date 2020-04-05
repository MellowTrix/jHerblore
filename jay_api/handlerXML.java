package scripts.jay_api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.tribot.api.General;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import scripts.dax_api.api_lib.models.RunescapeBank;

public class handlerXML {

    private handlerXML() {}
    private static final handlerXML HANDLER = new handlerXML();
    public static handlerXML get() {
        return HANDLER;
    }
    
    private boolean setup_depositing = false;
	private boolean setup_depositing_equipment = false;
	private boolean setup_depositing_noted = false;
	private boolean setup_withdrawing = false;
	private boolean walking_walktobank = false;
	private boolean GE_restocking = false;
	
	private int GE_restocking_amount = 0, herb_method = 0;
	private float GE_mult_buy = 1.0f, GE_mult_sell = 1.0f;

	private RunescapeBank bank = null;
	
	private List<Integer> setup_withdrawing_items = new ArrayList<Integer>();
	private List<Integer> setup_withdrawing_amount = new ArrayList<Integer>();
	private List<Integer> setup_depositing_exceptions = new ArrayList<Integer>();
	
	private long START_TIME = System.currentTimeMillis();
	
    public boolean isDepositing() {
        return setup_depositing;
    }
	
    public boolean isDepositingEquipment() {
        return setup_depositing_equipment;
    }
    
    public boolean isDepositingNoted() {
    	return setup_depositing_noted;
    }
    
    public boolean isWithdrawing () {
    	return setup_withdrawing;
    }
    
    public boolean isWalkingToBank() {
    	return walking_walktobank;
    }
    
    public boolean isRestocking() {
    	return GE_restocking;
    }
 
    public int getRestockingAmount() {
    	return GE_restocking_amount;
    }
    
    public void setRestockingAmount(int val) {
    	GE_restocking_amount = val;
    }
    
    public int getHerbMethod() {
    	return herb_method;
    }
    
    public float getGE_mult_buy() {
    	return GE_mult_buy;
    }
    
    public float getGE_mult_sell() {
    	return GE_mult_sell;
    }
    
    public RunescapeBank getBank() {
    	return bank;
    }
    
    public List<Integer> getWithdrawingItems() {
    	return setup_withdrawing_items;
    }
    
    public List<Integer> getWithdrawingAmount() {
    	return setup_withdrawing_amount;
    }
    
    public List<Integer> getDepositingExceptions() {
    	return setup_depositing_exceptions;
    }
    
    public long getTime() {
    	return START_TIME;
    }
    
    public void setTime(long time) {
    	START_TIME = time;
    }
    
    public void createFile(File file) {
		
		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		
		try {
			// Create a new settings.xml file if one already doesn't exist.
			if (!file.exists() && !file.isDirectory())
			{
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document docs = db.newDocument();
				
				// SETTINGS STRUCTURE FOR SETTINGS --- START
				Element rootElement = docs.createElement("SETTINGS");
				docs.appendChild(rootElement);
				
				// APPENDING SKIP ELEMENT TO SETTINGS
				Element skip = docs.createElement("SKIP");
				skip.appendChild(docs.createTextNode("false"));
				rootElement.appendChild(skip);
               
				// APPENDING BANKING ELEMENT TO SETTINGS
				Element banking = docs.createElement("BANKING");
				rootElement.appendChild(banking);
				
				// APPENDING SETUP ELEMENT TO BANKING
				Element setup = docs.createElement("SETUP");
				banking.appendChild(setup);
               
				// APPENDING SETTINGS ELEMENTS TO SETUP
				Element setup_depositing = docs.createElement("setup_depositing");
				setup_depositing.appendChild(docs.createTextNode("false"));
				setup.appendChild(setup_depositing);
                
				Element setup_depositing_exceptions = docs.createElement("setup_depositing_exceptions");
				setup_depositing_exceptions.appendChild(docs.createTextNode("-1"));
				setup.appendChild(setup_depositing_exceptions);
               
				Element setup_depositing_equipment = docs.createElement("setup_depositing_equipment");
				setup_depositing_equipment.appendChild(docs.createTextNode("false"));
				setup.appendChild(setup_depositing_equipment);
                
				Element setup_depositing_noted = docs.createElement("setup_depositing_noted");
				setup_depositing_noted.appendChild(docs.createTextNode("true"));
				setup.appendChild(setup_depositing_noted);
                
				Element setup_withdrawing = docs.createElement("setup_withdrawing");
				setup_withdrawing.appendChild(docs.createTextNode("false"));
				setup.appendChild(setup_withdrawing);
                
				Element setup_withdrawing_items = docs.createElement("setup_withdrawing_items");
				setup_withdrawing_items.appendChild(docs.createTextNode("-1"));
				setup.appendChild(setup_withdrawing_items);
                
				Element setup_withdrawing_amount = docs.createElement("setup_withdrawing_amount");
				setup_withdrawing_amount.appendChild(docs.createTextNode("-1"));
				setup.appendChild(setup_withdrawing_amount);
                
				// APPENDING WALKING ELEMENT TO BANKING
				Element walk = docs.createElement("WALKING");
				banking.appendChild(walk);
                
				Element walktobank = docs.createElement("walk_to_bank");
				walktobank.appendChild(docs.createTextNode("false"));
				walk.appendChild(walktobank);
                
				Element bank_loc = docs.createElement("bank");
				bank_loc.appendChild(docs.createTextNode("-1"));
				walk.appendChild(bank_loc);
				
				// APPENDING ELEMENT TO SETTINGS
				Element ge = docs.createElement("GE");
				rootElement.appendChild(ge);
				
				Element restocking = docs.createElement("restocking");
				restocking.appendChild(docs.createTextNode("false"));
				ge.appendChild(restocking);
				
				Element restocking_amount = docs.createElement("restocking_amount");
				restocking_amount.appendChild(docs.createTextNode("-1"));
				ge.appendChild(restocking_amount);
				
				Element restocking_mult_buy = docs.createElement("restocking_mult_buy");
				restocking_mult_buy.appendChild(docs.createTextNode("30"));
				ge.appendChild(restocking_mult_buy);
				
				Element restocking_mult_sell= docs.createElement("restocking_mult_sell");
				restocking_mult_sell.appendChild(docs.createTextNode("-49"));
				ge.appendChild(restocking_mult_sell);
				
				// APPENDING ELEMENT TO SETTINGS
				Element herb = docs.createElement("HERBLORE");
				rootElement.appendChild(herb);
				
				Element herb_method = docs.createElement("herblore_method");
				herb_method.appendChild(docs.createTextNode("0"));
				herb.appendChild(herb_method);
				
				// SETTINGS STRUCTURE FOR SETTINGS --- END
               
				saveFile(file, docs); // Write the content into xml file
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveFile(File file, Document doc) {

		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return;
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public void parseFile(File file, String[] arr, Document doc) {

		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return;
		}
		
		doc.getDocumentElement().normalize();
			
		Element eElement = (Element) doc.getElementsByTagName("SETUP").item(0);

		eElement.getElementsByTagName("setup_depositing").item(0).setTextContent(arr[0]);
		eElement.getElementsByTagName("setup_depositing_exceptions").item(0).setTextContent(arr[1]);
		eElement.getElementsByTagName("setup_depositing_equipment").item(0).setTextContent(arr[2]);
		eElement.getElementsByTagName("setup_depositing_noted").item(0).setTextContent(arr[3]);
		eElement.getElementsByTagName("setup_withdrawing").item(0).setTextContent(arr[4]);
		eElement.getElementsByTagName("setup_withdrawing_items").item(0).setTextContent(arr[5]);
		eElement.getElementsByTagName("setup_withdrawing_amount").item(0).setTextContent(arr[6]);

		eElement = (Element) doc.getElementsByTagName("WALKING").item(0);
		
		eElement.getElementsByTagName("walk_to_bank").item(0).setTextContent(arr[7]);
		eElement.getElementsByTagName("bank").item(0).setTextContent(arr[8]);
		
		eElement = (Element) doc.getElementsByTagName("GE").item(0);
		
		eElement.getElementsByTagName("restocking").item(0).setTextContent(arr[9]);
		eElement.getElementsByTagName("restocking_amount").item(0).setTextContent(arr[10]);
		eElement.getElementsByTagName("restocking_mult_buy").item(0).setTextContent(arr[11]);
		eElement.getElementsByTagName("restocking_mult_sell").item(0).setTextContent(arr[12]);
		
		saveFile(file, doc); // Write the content into xml file
		loadSettings(file);
	}

	public void parseHerblore(File file, String[] arr, String[] herb) {

		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 

		Document doc = null;
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
		
			// Parse our settings file
			doc = db.parse(file);

			doc.getDocumentElement().normalize();
			
			Element eElement = (Element) doc.getElementsByTagName("HERBLORE").item(0);
			
			eElement.getElementsByTagName("herblore_method").item(0).setTextContent(herb[0]);
			
			// Parsing complete ------------------------
        
			saveFile(file, doc); // Write the content into xml file

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		parseFile(file, arr, doc);
	}
	
	public void loadSettings(File file) {

		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 

		Document doc = null;
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
		
			// Parse our settings file
			doc = db.parse(file);

			doc.getDocumentElement().normalize();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		Element eElement = (Element) doc.getElementsByTagName("SETUP").item(0);
		
		if (eElement.getElementsByTagName("setup_depositing").item(0).getTextContent().equals("true")) {
			setup_depositing = true;
			String c = eElement.getElementsByTagName("setup_depositing_exceptions").item(0).getTextContent();
			if (!c.equals("-1")) {
				List<String> list = Arrays.asList(c.split(","));
				setup_depositing_exceptions = list.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
			}
            	
			if (eElement.getElementsByTagName("setup_depositing_noted").item(0).getTextContent().equals("true"))
				setup_depositing_noted = true;
		}

		if (eElement.getElementsByTagName("setup_depositing_equipment").item(0).getTextContent().equals("true"))
			setup_depositing_equipment = true;

		if (eElement.getElementsByTagName("setup_withdrawing").item(0).getTextContent().equals("true")) {
			setup_withdrawing = true;
			String c = eElement.getElementsByTagName("setup_withdrawing_items").item(0).getTextContent();
			String f = eElement.getElementsByTagName("setup_withdrawing_amount").item(0).getTextContent();
			if (!c.equals("-1") && !f.equals("-1")) {	
				List<String> list = Arrays.asList(c.split(","));
				List<String> list1 = Arrays.asList(f.split(","));
				this.setup_withdrawing_items = list.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
				this.setup_withdrawing_amount = list1.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());
			}
		}
		
		eElement = (Element) doc.getElementsByTagName("WALKING").item(0);

		if (eElement.getElementsByTagName("walk_to_bank").item(0).getTextContent().equals("true")) {
			walking_walktobank = true;
			String c = eElement.getElementsByTagName("bank").item(0).getTextContent();
			if (!c.equals("-1")) {
				try {
					bank = RunescapeBank.valueOf(c);
				} catch (IllegalArgumentException ex) {
					General.println("AutoBanker_Error - Invalid bank location.");
				}
			}     
		}
		
		eElement = (Element) doc.getElementsByTagName("GE").item(0);
		
		if (eElement.getElementsByTagName("restocking").item(0).getTextContent().equals("true")) {
			GE_restocking = true;
			String c = eElement.getElementsByTagName("restocking_amount").item(0).getTextContent();
			if (!c.equals("-1")) {
				GE_restocking_amount = Integer.parseInt(c);
			}
			c = eElement.getElementsByTagName("restocking_mult_buy").item(0).getTextContent();
			GE_mult_buy = (100.0f + Float.parseFloat(c))/100.0f;
			c = eElement.getElementsByTagName("restocking_mult_sell").item(0).getTextContent();
			GE_mult_sell = (100.0f + Float.parseFloat(c))/100.0f;
		}
		
		eElement = (Element) doc.getElementsByTagName("HERBLORE").item(0);

		herb_method = Integer.parseInt(eElement.getElementsByTagName("herblore_method").item(0).getTextContent());
	}
	
	public String getSetting(File file, String parent, String setting) {
		
		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return null;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 

		Document doc = null;
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
		
			// Parse our settings file
			doc = db.parse(file);

			doc.getDocumentElement().normalize();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		Element eElement = (Element) doc.getElementsByTagName(parent).item(0);
		
		return eElement.getElementsByTagName(setting).item(0).getTextContent();
	}
	
	public boolean skipGUI(File file) {
		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return false;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
		
			// Parse our settings file
			Document doc = db.parse(file);

			doc.getDocumentElement().normalize();
			
			String skip = (String) doc.getElementsByTagName("SKIP").item(0).getTextContent();
			if (skip.equals("true"))
				return true;

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public void skipGUI(File file, boolean bool) {
		if (file == null) {
			General.println("XMLhandler - Invalid file.");
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
		
			// Parse our settings file
			Document doc = db.parse(file);

			doc.getDocumentElement().normalize();
			
			if (bool)
				doc.getElementsByTagName("SKIP").item(0).setTextContent("true");
			else
				doc.getElementsByTagName("SKIP").item(0).setTextContent("false");
			
			// Write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
		}
	}
}
