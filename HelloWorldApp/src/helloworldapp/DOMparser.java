/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 *
 * @author CHENGLX3
 */
public class DOMparser {
    
    private static int x;
    
    public static void DOMparser() {
        x = 1;
    }
    
    public static void doSomething () {
        System.out.println("Hello World!");
        try {
            
            File inputFile = new File("C:\\Users\\chenglx3\\Documents\\NetBeansProjects\\HelloWorldApp\\data\\student.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);           
            doc.getDocumentElement().normalize();
            
            System.out.println("Root element =" + doc.getDocumentElement().getNodeName());
            
            NodeList nList = doc.getElementsByTagName("student");
            System.out.println("----------------------------");
            
            for ( int i = 0; i < nList.getLength(); i++){
                Node nNode = nList.item(i);
                System.out.println("\nCurrent Element = "+nNode.getNodeName());
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) nNode;
                    System.out.println("Studen roll no ="+eElement.getAttribute("rollno"));
                    System.out.println("First Name = " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Last Name = " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    System.out.println("Nick Name = " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
                    System.out.println("Marks = " + eElement.getElementsByTagName("marks").item(0).getTextContent());
                }
            }
            
            
            
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        
        
        
    }
    
}
