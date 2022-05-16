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

        String currentElement;
        String id;
        String name;
        String objectType;
        String wallPlacement;
        String tag;

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

            for (int temp=0; temp<nList.getLength(); temp++){
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;

                    currentElement = nNode.getNodeName();
                    id = eElement.getAttribute("id");
                    name = eElement.getAttribute("Name");
                    objectType = eElement.getAttribute("ObjectType");
                    wallPlacement = eElement.getAttribute("ObjectPlacement");
                    tag = eElement.getAttribute("Tag");

                    if (nNode.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(nNode.getChildNodes());
                    }

                    propertySetIdElement1 = propertySetId.get(0);
                    propertySetIdElement2 = propertySetId.get(1);
                    propertySetIdElement3 = propertySetId.get(2);
                    propertySetId.clear();

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

                    fw = new FileWriter(dataFile, true);
                    bfw = new BufferedWriter(fw);
                    bfw.write("\n"+"\n"+fullData);
                    bfw.close();
                }
            }
            checkProperties();
            System.out.println(allPropertiesId.size());
            System.out.println(allPropertySetId.size());
            System.out.println(allPropertySetId);
            System.out.println(allPropertiesId);

            //Końcowa klamra w pliku .json
            fw = new FileWriter(dataFile, true);
            bfw = new BufferedWriter(fw);
            bfw.write("\n    ]" +
                    "\n}");
            bfw.close();
            //System.out.println(allPropertySetId);
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

    private static void visitChildNodes(NodeList nList) {
        System.out.println("\n");
        Node node;
        String psIdValue = "";
        String nodeName = "";
        for (int temp = 0; temp < nList.getLength(); temp++) {
            node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodeName = "Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent();
                //System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
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

    private static void checkProperties(){
        nList = doc.getElementsByTagName("properties");

        for (int temp=0; temp<nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.hasChildNodes()){
                getPropertiesChildNodes(nNode.getChildNodes());
            }
        }
    }

    private static void getPropertiesChildNodes(NodeList nList){
        Node node;
        for (int temp=0; temp<nList.getLength(); temp++){
            node = nList.item(temp);
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++)
                    {
                        Node tempNode = nodeMap.item(i);
                        //System.out.println(tempNode.getNodeValue());
                        propertiesId = tempNode.getNodeValue();
                        if (tempNode.getNodeName() == "id"){
                            System.out.println("To pole to id: "+ propertiesId );
                            for (int k=0; k<allPropertySetId.size(); k++){
                                if (propertiesId.equals(allPropertySetId.get(k))){
                                    System.out.println("To samo id");
                                    allPropertiesId.add(propertiesId);
                                }
                            }
                        }else if(tempNode.getNodeName() == "Name"){
                            System.out.println(tempNode.getNodeName()+":"+tempNode.getNodeValue());
                        }else{
                            System.out.println(propertiesId);
                        }
                    }
                    if (node.hasChildNodes()){
                        getPropertiesChildNodes(node.getChildNodes());
                        System.out.println("\n");
                    }
                }
        }
    }
}