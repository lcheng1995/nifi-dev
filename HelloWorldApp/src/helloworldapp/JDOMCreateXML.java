/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet; 

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author CHENGLX3
 */
public class JDOMCreateXML {
    
    public static void doSomething(){
        try {
            // define a XML Document
            Document doc = new Document();

            // root element
            Element carsElement = new Element("cars");
            doc.setRootElement(carsElement);
            
            // supercars element
            Element supercarElement= new Element("supercars");
            supercarElement.setAttribute("type", "Ferrari");
            
            // carname Element 1
            Element carnameElement1 = new Element("carname");
            carnameElement1.setAttribute(new Attribute("type", "formula one"));
            carnameElement1.setText("Ferrari 101");
            // carname Element 1
            Element carnameElement2 = new Element("carname");
            carnameElement1.setAttribute(new Attribute("type", "sports"));
            carnameElement1.setText("Ferrari 101");
            
            // add carname elements to supercarElement
            supercarElement.addContent(carnameElement1);
            supercarElement.addContent(carnameElement2);
            
            // add supercar element to root
            doc.getRootElement().addContent(supercarElement);
            // display xml
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, System.out);
            
        } catch(IOException e) {
         e.printStackTrace();
        }
    }
    
}
