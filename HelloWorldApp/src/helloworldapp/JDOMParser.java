/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author CHENGLX3
 */
public class JDOMParser {
    
    public static void doSometing() {
        
        try {
            File inputFile = new File("data\\student.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            System.out.println("Root element = "+document.getRootElement().getQualifiedName()); 
            Element classElement = document.getRootElement();
            
            //
            List<Element> studentList = classElement.getChildren();
            System.out.println("----------------------------");
            
            for (int i=0; i<studentList.size(); i++) {
                Element student = studentList.get(i);
                System.out.println("\nCurrent element = "+student.getName());
                Attribute attribute = student.getAttribute("rollno");
                System.out.println("Student roll no ="+attribute.getValue());
                System.out.println("First Name ="+student.getChild("firstname").getText());
                System.out.println("Last Name ="+student.getChild("lastname").getText());
                System.out.println("Nick Name ="+student.getChild("nickname").getText());
                System.out.println("Marks ="+student.getChild("marks").getText());
            }
            
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
}
