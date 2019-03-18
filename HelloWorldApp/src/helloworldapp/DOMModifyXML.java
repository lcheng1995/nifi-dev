/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author CHENGLX3
 */
public class DOMModifyXML {
    
    public static void doSomething() {
        try  {
            File inputFile = new File("data\\car.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            // root node
            Node cars = doc.getFirstChild();
            Node supercar = doc.getElementsByTagName("supercars").item(0);
            
            // update supercar attribute
            NamedNodeMap attr = supercar.getAttributes();
            Node nodeAttr = attr.getNamedItem("company");
            nodeAttr.setTextContent("Goodie");
            
            //Loop the supercar child node
            NodeList nList = supercar.getChildNodes();
            
            for (int i=0; i < nList.getLength(); i++ ) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    if ("carname".equals(eElement.getNodeName())) {
                        if ("Ferarri 101".equals(eElement.getTextContent())) {
                            eElement.setTextContent("Ferrari 101-updated");
                        }
                        if ("Ferarri 201".equals(eElement.getTextContent())) {
                            eElement.setTextContent("Ferrari 201-updated");
                        }
                    }          
                }
            }
            
            //
            NodeList childNodes = cars.getChildNodes();
            for (int i= 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
            
                if ("luxurycars".equals(childNode.getNodeName())) {
                    cars.removeChild(childNode);
                }          
            }
            
            
            // place doc into a DOMSource -- a holder for transformaton source tree in the form of DOM tree
            DOMSource source = new DOMSource(doc);
            
            // Create Transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();  
            
            //write to console
            StreamResult consoleResult = new  StreamResult(System.out);
            transformer.transform(source, consoleResult);
            System.out.println();
            
            
            
            
            
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
    }
    
    
    
    
    
}
