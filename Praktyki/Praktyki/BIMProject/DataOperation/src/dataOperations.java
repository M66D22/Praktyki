import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

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

    static File ifcFile = new File("Przykładowy plik IFC.xml");
    static File dataFile = new File("Data.json");

    static String propertiesId = "";

    //Properties variables
        //Id list
    static List<String> allPropertiesId = new ArrayList<>();

    //properties function variables
    static String mainProperitesName = "";

    static String currentElement;
    static String id;
    static String name;
    static String objectType;
    static String wallPlacement;
    static String tag;

    public static void main(String[] args){
        FileWriter fw;
        BufferedWriter bfw;

        try {
            fw = new FileWriter(dataFile);
            String clearFile = "";
            fw.write(clearFile);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            //Początkowa klamra w pliku .json
            fw = new FileWriter(dataFile, true);
            bfw = new BufferedWriter(fw);
            bfw.write("{" +
                    "\n   "+asciiChar+"metaData"+asciiChar+":"+" [");
            bfw.close();
            //Dzialanie programu
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(ifcFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: "+doc.getDocumentElement().getNodeName());
            nList = doc.getElementsByTagName("IfcWallStandardCase");
            nListProperties = doc.getElementsByTagName("properties");

            for (int temp=0; temp<nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Node nNodeProperties = nListProperties.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    currentElement = nNode.getNodeName();
                    id = eElement.getAttribute("id");
                    name = eElement.getAttribute("Name");
                    objectType = eElement.getAttribute("ObjectType");
                    wallPlacement = eElement.getAttribute("ObjectPlacement");
                    tag = eElement.getAttribute("Tag");
                }
            }

            saveFromProperties();
            saveFromWallStandardCase();

            //Końcowa klamra w pliku .json
            fw = new FileWriter(dataFile, true);
            bfw = new BufferedWriter(fw);
            bfw.write("\n    ]" +
                    "\n}");
            bfw.close();
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

    private static void saveFromWallStandardCase(){
        for (int temp=0; temp<nList.getLength(); temp++){
            Node nNode = nList.item(temp);
            if (nNode.hasChildNodes()){
                visitChildNodes(nNode.getChildNodes());
            }
            propertySetIdElement1 = propertySetId.get(0);
            propertySetIdElement2 = propertySetId.get(1);
            propertySetIdElement3 = propertySetId.get(2);
            propertySetId.clear();

            saveAllDataToFile();
        }
    }

    private static void saveFromProperties(){
        for (int temp=0; temp<nListProperties.getLength(); temp++){
            Node nNodeProperties = nListProperties.item(temp);
            if (nNodeProperties.hasChildNodes()){
                visitPropertiesChildNodes(nNodeProperties.getChildNodes());
            }
        }
        System.out.println(allPropertySetId);
        System.out.println(allPropertiesId);
    }

    private static void visitChildNodes(NodeList nList) {
        System.out.println("\n");
        Node node;
        Node node1;
        String psIdValue = "";
        String nodeName = "";
        for (int temp = 0; temp < nList.getLength(); temp++) {
            node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodeName = "Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent();
                //System.out.println(nodeName);
                //Check all attributes
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    //System.out.println(nodeMap.getLength());
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        //System.out.println("Attr name : " + tempNode.getNodeName()+ "; Value = " + tempNode.getNodeValue());
                        String test = "Attr name : " + tempNode.getNodeName() + "; Value = " + tempNode.getNodeValue();
                        if (node.getNodeName().equals("IfcPropertySet")) {
                            propertySetType = node.getNodeName();
                            //System.out.println("\n"+propertySetType);
                            psIdValue = tempNode.getNodeValue();
                            allPropertySetId.add(psIdValue.substring(1));
                            propertySetId.add(psIdValue.substring(1));
                        } else if (node.getNodeName().equals("IfcPresentationLayerAssignment")) {
                            propertySetType = node.getNodeName();
                            //System.out.println("\n"+propertySetType);
                            propertySetLayer = tempNode.getNodeValue();
                            propertySetLayer = propertySetLayer.substring(1);
                            //System.out.println(propertySetLayer);
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
        for (int temp = 0; temp < nList.getLength(); temp++) {
            node = nList.item(temp);
            if (node.hasAttributes()) {
                // get attributes names and values
                NamedNodeMap nodeMap = node.getAttributes();
                System.out.println("NODE: " + node.getNodeName() + ": ");
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
                    } else {
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

    private static void saveAllDataToFile(){
        String fullData =
                "      {\n"
                        +"          "+asciiChar+"type: "+asciiChar +":"+asciiChar+currentElement+asciiChar+",\n"
                        +"          "+asciiChar+"id"+asciiChar+":"+asciiChar+id+asciiChar+",\n"
                        +"          "+asciiChar+"name"+asciiChar+":"+asciiChar+name+asciiChar+",\n"
                        +"          "+asciiChar+"Object type"+asciiChar+":"+asciiChar+objectType+asciiChar+",\n"
                        +"          "+asciiChar+"Tag"+asciiChar+":"+asciiChar+tag+asciiChar+",\n"
                        +"          "+asciiChar+"propertySet"+asciiChar+": [\n"
                        +"              {\n"
                        +"                  "+asciiChar+"id"+asciiChar+": "+asciiChar+propertySetIdElement1+asciiChar+",\n"
                        +"                  "+asciiChar+"Name"+asciiChar+": "+asciiChar+asciiChar+",\n"
                        +"                  "+asciiChar+"properties"+asciiChar+": [\n"
                        +"                  ]\n"
                        +"              },\n"
                        +"              {\n"
                        +"                  "+asciiChar+"id"+asciiChar+": "+asciiChar+propertySetIdElement2+asciiChar+",\n"
                        +"                  "+asciiChar+"Name"+asciiChar+": "+asciiChar+asciiChar+",\n"
                        +"                  "+asciiChar+"properties"+asciiChar+": [\n"
                        +"                  ]\n"
                        +"              },\n"
                        +"              {\n"
                        +"                  "+asciiChar+"id"+asciiChar+": "+asciiChar+propertySetIdElement3+asciiChar+",\n"
                        +"                  "+asciiChar+"Name"+asciiChar+": "+asciiChar+asciiChar+",\n"
                        +"                  "+asciiChar+"properties"+asciiChar+": [\n"
                        +"                  ]\n"
                        +"              }\n"
                        +"          ],\n"
                        +"          "+asciiChar+"layer"+asciiChar+": "+asciiChar+propertySetLayer+asciiChar+"\n"
                        +"      },";

        try {
            FileWriter fw = new FileWriter(dataFile, true);
            BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write("\n"+"\n"+fullData);
            bfw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Funkcja która dynamicznie będzie tworzyła w zapisie elementy properties w zależnosci od tego ile ich jest:
    //#########
    private void automaticCreatingVariablesToProperties(){
        //Creating variables
    }
}