import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class dataOperations {
    //Cudzysłów w ascii
    static int ascii = 34;
    static char asciiChar = (char) ascii;

    static String propertySetType;
    static String propertySetLayer;

    static NodeList nList;
    static NodeList nListProperties;
    static Document doc;

    //Lista, która przechowuje tymczasowo wybrane wartości z jednego tagu
    static List<String> propertySetId = new ArrayList<>();

    //Lista, która przechowuje wszystkie pobrane id
    static List<String> allPropertySetId = new ArrayList<>();

    static String propertySetIdElement1;
    static String propertySetIdElement2;
    static String propertySetIdElement3;

    static File ifcFile = new File("DataOperation/Przykładowy plik IFC.xml");

    static String propertiesId = "";

    //Properties Variable
    // Id list
    static List<String> allPropertiesId = new ArrayList<>();

    //properties function variables
    static String mainProperitesName = "";

    static String currentElement;
    static String id;
    static String name;
    static String objectType;
    static String wallPlacement;
    static String tag;

    static int propertiesTagCounter = 0;
    static Map<String, String> properitesElementStringMap;

    static String propertiesName;
    static String propertiesValue;

    static int counter = 0;

    static JSONObject jsonObject;
    static JSONArray array;
    static JSONObject outer;

    static JSONObject propertySetObject1;
    static JSONObject propertySetObject2;
    static JSONObject propertySetObject3;
    static JSONArray propertySetArray;

    static JSONObject propertiesObject;
    static JSONArray propertiesArray;

    public static void main(String[] args){
        FileWriter fw;
        BufferedWriter bfw;
        counter = 0;
        try {
            String clearFile = "";

            fw = new FileWriter("FullData.json");
            fw.write(clearFile);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            //Dzialanie programu
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(ifcFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: "+doc.getDocumentElement().getNodeName());
            nList = doc.getElementsByTagName("IfcWallStandardCase");
            nListProperties = doc.getElementsByTagName("properties");

            array = new JSONArray();
            outer = new JSONObject();
            propertiesObject = new JSONObject();

            saveFromProperties();

            for (int temp=0; temp<nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Node nNodeProperties = nListProperties.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    //ifcWallStandardCaseChildNodes
                    if (nNode.hasChildNodes()){
                        visitChildNodes(nNode.getChildNodes());
                    }
                    currentElement = nNode.getNodeName();
                    id = eElement.getAttribute("id");
                    name = eElement.getAttribute("Name");
                    objectType = eElement.getAttribute("ObjectType");
                    wallPlacement = eElement.getAttribute("ObjectPlacement");
                    tag = eElement.getAttribute("Tag");

                    counter++;

                    propertySetIdElement1 = propertySetId.get(0);
                    propertySetIdElement2 = propertySetId.get(1);
                    propertySetIdElement3 = propertySetId.get(2);
                    propertySetId.clear();

                    propertySetArray = new JSONArray();
                    propertySetObject1 = new JSONObject();
                    propertySetObject1.put("id", propertySetIdElement1);
                    propertySetObject1.put("Name", mainProperitesName);

                    propertySetObject2 = new JSONObject();
                    propertySetObject2.put("id", propertySetIdElement2);
                    propertySetObject2.put("Name", mainProperitesName);

                    propertySetObject3 = new JSONObject();
                    propertySetObject3.put("id", propertySetIdElement3);
                    propertySetObject3.put("Name", mainProperitesName);

                    propertiesArray = new JSONArray();
                    propertiesObject.put("name", propertiesName);
                    propertiesObject.put("value", propertiesValue);

                    propertiesArray.put(propertiesObject);

                    propertySetObject1.put("propertiesList", propertiesArray);
                    propertySetObject2.put("propertiesList", propertiesArray);
                    propertySetObject3.put("propertiesList", propertiesArray);
                    propertySetArray.put(propertySetObject1);
                    propertySetArray.put(propertySetObject2);
                    propertySetArray.put(propertySetObject3);

                    jsonObject = new JSONObject();
                    jsonObject.put("id", id);
                    jsonObject.put("name", name);
                    jsonObject.put("objectType", objectType);
                    jsonObject.put("tag", tag);
                    jsonObject.put("layer", propertySetLayer);

                    jsonObject.put("propertySet", propertySetArray);

                    array.put(jsonObject);
                }
            }
            saveToFile();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
    private static void saveFromProperties(){
        for (int temp=0; temp<nListProperties.getLength(); temp++){
            Node nNodeProperties = nListProperties.item(temp);
            if (nNodeProperties.hasChildNodes()){
                visitPropertiesChildNodes(nNodeProperties.getChildNodes());
            }
        }
    }
    private static void visitChildNodes(NodeList nList) {
        Node node;
        Node node1;
        String psIdValue = "";
        String nodeName = "";
        for (int temp = 0; temp < nList.getLength(); temp++) {
            node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodeName = "Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent();
                //Check all attributes
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        String test = "Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue();
                        if (node.getNodeName().equals("IfcPropertySet")) {
                            propertySetType = node.getNodeName();
                            psIdValue = tempNode.getNodeValue();
                            allPropertySetId.add(psIdValue.substring(1));
                            propertySetId.add(psIdValue.substring(1));
                        } else if (node.getNodeName().equals("IfcPresentationLayerAssignment")) {
                            propertySetType = node.getNodeName();
                            propertySetLayer = tempNode.getNodeValue();
                            propertySetLayer = propertySetLayer.substring(1);
                        }
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }
                }
            }
        }
    }
    private static void visitPropertiesChildNodes(NodeList nList) {
        Node node;
        propertiesTagCounter = 0;
        for (int temp = 0; temp < nList.getLength(); temp++) {
            node = nList.item(temp);
            if (node.hasAttributes()) {
                // get attributes names and values
                NamedNodeMap nodeMap = node.getAttributes();
                System.out.println("NODE: " + node.getNodeName() + ": ");
                if (node.getNodeName() == "IfcPropertySingleValue"){
                    propertiesTagCounter ++;
                    System.out.println(propertiesTagCounter);
                }
                for (int i = 0; i < nodeMap.getLength(); i++) {
                    Node tempNode = nodeMap.item(i);
                    //System.out.println(tempNode.getNodeValue());
                    propertiesId = tempNode.getNodeValue();
                    if (tempNode.getNodeName() == "id") {
                        System.out.println("id: " + propertiesId);
                        for (int k = 0; k < allPropertySetId.size(); k++) {
                            if (propertiesId.equals(allPropertySetId.get(k))) {
                                System.out.println("To samo id");
                                allPropertiesId.add(propertiesId);
                            }
                        }
                    } else if (tempNode.getNodeName() == "Name" && node.getNodeName() == "IfcPropertySet") {
                        mainProperitesName = tempNode.getNodeValue();
                        System.out.println("Name do zapisu" + ":" + mainProperitesName);
                    } else if (tempNode.getNodeName() == "Name" && node.getNodeName() == "IfcPropertySingleValue"){
                        propertiesName = tempNode.getNodeValue();
                        System.out.println(tempNode.getNodeName()+": "+propertiesName);
                    } else if (tempNode.getNodeName() == "NominalValue"){
                        propertiesValue = tempNode.getNodeValue();
                        System.out.println(tempNode.getNodeName()+": "+propertiesValue);
                    }else {
                        System.out.println(tempNode.getNodeName() + ":" + propertiesId);
                    }
                }
                if (node.hasChildNodes()) {
                    visitPropertiesChildNodes(node.getChildNodes());
                    System.out.println("\n");
                }
            }
        }
    }
    private static void saveToFile(){
        outer.put("metaData", array);
        System.out.println(outer);

        //save to file()
        FileWriter fw = null;
        try {
            fw = new FileWriter("FullData.json");
            BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write(String.valueOf(outer));
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}