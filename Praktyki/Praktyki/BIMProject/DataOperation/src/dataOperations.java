import java.io.*;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class dataOperations {
    public static void main(String[] args)   {
        File ifcFile = new File("Przykładowy plik IFC.xml");
        File dataFile = new File("Data.json");

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

        String objectTypeProperty;
        String xlinkhref;

        int i=0;
        try {
            //Początkowa klamra w pliku .json
            fw = new FileWriter(dataFile, true);
            bfw = new BufferedWriter(fw);
            bfw.write("{");
            bfw.close();
            //Dzialanie programu
            Scanner ifcFileScanner = new Scanner(ifcFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(ifcFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: "+doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("IfcWallStandardCase");



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
                    //Cudzysłów w ascii
                    int ascii = 34;
                    char asciiChar = (char) ascii;

                    String nodeChild = visitChildNodes(nNode.getChildNodes());
                    //System.out.println(nodeChild);

                    String fullData =
                            asciiChar+"Current element"+asciiChar +":"+asciiChar+currentElement+asciiChar+",\n"
                                    +asciiChar+"Id"+asciiChar+":"+asciiChar+id+asciiChar+",\n"
                                    +asciiChar+"Name"+asciiChar+":"+asciiChar+name+asciiChar+",\n"
                                    +asciiChar+"Object type"+asciiChar+":"+asciiChar+objectType+asciiChar+","
                                    +nodeChild;

                    fw = new FileWriter(dataFile, true);
                    bfw = new BufferedWriter(fw);
                    bfw.write("\n"+"\n"+fullData);
                    bfw.close();
                }
            }
            //Końcowa klamra w pliku .json
            fw = new FileWriter(dataFile, true);
            bfw = new BufferedWriter(fw);
            bfw.write("\n}");
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

    private static String visitChildNodes(NodeList nList)
    {
        System.out.println("\n");
        Node node;
        String nodeName = "";
        String fullData = "";
        String attrName = "";
        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                nodeName = "Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent();
                // System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                //Check all attributes
                if (node.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++)
                    {
                        Node tempNode = nodeMap.item(i);
                        attrName = "    Attr name : " + tempNode.getNodeName()+ "; Value = " + tempNode.getNodeValue();
                        // System.out.println("    Attr name : " + tempNode.getNodeName()+ "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        //We got more childs; Let's visit them as well
                        visitChildNodes(node.getChildNodes());
                    }
                    fullData = fullData+"\n"+nodeName+"\n"+attrName;
                    //System.out.println("\n"+fullData);
                }
            }
        }
        return fullData;
    }
}