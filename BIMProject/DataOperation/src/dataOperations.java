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
    private static FileWriter mainFileWriter;
    private static BufferedWriter mainBufferedWriter;

    static File exampleIfcFile = new File("Przykładowy plik IFC.xml");
    static File jsonDataFile = new File("DataFile.json");

    //Główne właściwośći ściany
    static String type, id, name, objectType, tag;

    //Property set values
    static String typePs, xlinkHrefPs;

    static String xlinkAttribute = "http://www.w3.org/1999/xlink";

    //Help variables
    static int ascii34 = 34;
    static char asciiChar34 = (char) ascii34;

    static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    static DocumentBuilder dBuilder;
    static Document doc;

    public static void main(String[] args) {
        makeDataFileClear();
        wallStandardCaseLoadToJson();
    }

    //Ładowanie do pliku .json wartości z tagu wallStandardCase
    public static void wallStandardCaseLoadToJson(){
        try {
            Scanner ifcFileScanner = new Scanner(exampleIfcFile);
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(exampleIfcFile);
            doc.getDocumentElement().normalize();
            System.out.println("Działa");

            //nList WallStandardCase
            NodeList nListWallStandardCase = doc.getElementsByTagName("IfcWallStandardCase");
            System.out.println("nlw: "+nListWallStandardCase.item(0));

            //nNlist property set
            NodeList nListPs = doc.getElementsByTagName("IfcPropertySet");
            System.out.println("nList: "+nListPs.item(0).getNodeType());

            for (int temp = 0; temp < nListWallStandardCase.getLength(); temp++){
                Node nNodeWallStandardCase = nListWallStandardCase.item(temp);
                Node nNodePs = nListPs.item(0);

                if (nNodeWallStandardCase.getNodeType() == Node.ELEMENT_NODE){
                    Element eElementWallStandardCase = (Element) nNodeWallStandardCase;
                    Element eElementPs = (Element) nNodePs;

                    type = nNodeWallStandardCase.getNodeName();
                    id = eElementWallStandardCase.getAttribute("id");
                    name = eElementWallStandardCase.getAttribute("Name");
                    objectType = eElementWallStandardCase.getAttribute("ObjectType");
                    tag = eElementWallStandardCase.getAttribute("Tag");
                    System.out.println(eElementPs);
                    typePs = nNodePs.getNodeName();
                    xlinkHrefPs = eElementPs.getAttributeNS(xlinkAttribute, "href");
                    System.out.println(xlinkHrefPs);

                    String fullData =
                            asciiChar34 +"type"+ asciiChar34 +":"+ asciiChar34 +type+ asciiChar34 +",\n"
                                    + asciiChar34 +"id"+ asciiChar34 +":"+ asciiChar34 +id+ asciiChar34 +",\n"
                                    + asciiChar34 +"name"+ asciiChar34 +":"+ asciiChar34 +name+ asciiChar34 +",\n"
                                    + asciiChar34 +"Object type"+ asciiChar34 +":"+ asciiChar34 +objectType+ asciiChar34 +",\n"
                                    + asciiChar34 +"Tag"+ asciiChar34 +":"+ asciiChar34 +tag+ asciiChar34+"\n"

                                    + asciiChar34 +typePs+ asciiChar34;

                    mainFileWriter = new FileWriter(jsonDataFile, true);
                    mainBufferedWriter = new BufferedWriter(mainFileWriter);
                    mainBufferedWriter.write("\n\n"+fullData);
                    mainBufferedWriter.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeDataFileClear(){
        try {
            mainFileWriter = new FileWriter(jsonDataFile);
            String clearFileString = "";
            mainFileWriter.write(clearFileString);
            mainFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}