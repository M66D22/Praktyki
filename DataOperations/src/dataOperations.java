import java.io.*;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class dataOperations {
    public static void main(String[] args) {
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
            System.out.println("------------------------");
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

                    /*Get other components
                    /
                    /
                    /
                    /
                    /
                    */

                    //Cudzysłów w ascii
                    int ascii = 34;
                    char asciiChar = (char) ascii;

                    String fullData =
                            asciiChar+"Current element"+asciiChar +":"+asciiChar+currentElement+asciiChar+",\n"
                            +asciiChar+"Id"+asciiChar+":"+asciiChar+id+asciiChar+",\n"
                            +asciiChar+"Name"+asciiChar+":"+asciiChar+name+asciiChar+",\n"
                            +asciiChar+"Object type"+asciiChar+":"+asciiChar+objectType+asciiChar+",\n"
                            +asciiChar+"Tag"+asciiChar+":"+asciiChar+tag+asciiChar;

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
}