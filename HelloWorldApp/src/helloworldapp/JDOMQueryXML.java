package helloworldapp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author CHENGLX3
 */
public class JDOMQueryXML {
    
    public static void doSomething(){
        try {
            File input = new File("data\\car.xml"); 
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(input);
            System.out.println("Root element = "+document.getRootElement().getQualifiedName()); 
            Element classElement = document.getRootElement();
            
            //
            List<Element> supercarList = classElement.getChildren();
             System.out.println("----------------------------");
            
            for (int i=0; i<supercarList.size(); i++) {
                Element supercarElement = supercarList.get(i);
                System.out.println("\nCurrent element = "+supercarElement.getName());
                Attribute attribute = supercarElement.getAttribute("company");
                System.out.println("company : " + attribute.getValue() );
                
                List<Element> carnameList = supercarElement.getChildren("carname");
                for (int j=0; j<carnameList.size(); j++) {
                    Element carElement = carnameList.get(j);
                    System.out.println("car name="+carElement.getText());
                    Attribute typeAttribute = carElement.getAttribute("type");
                    
                    System.out.println("car type = ");
                    if(typeAttribute != null) {
                        System.out.println(typeAttribute.getValue());
                    } else {
                        System.out.println("");
                    }         
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
