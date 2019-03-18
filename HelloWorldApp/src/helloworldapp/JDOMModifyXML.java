/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author CHENGLX3
 */
public class JDOMModifyXML {
    public static void doSomething(){
        try {
            File inputFile = new File("data\\car.xml");
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();

            //get first supercar
            Element supercarElement = rootElement.getChild("supercars");

            // update supercar attribute
            Attribute attribute = supercarElement.getAttribute("company");
            attribute.setValue("My super car");
            
            List<Element> list = supercarElement.getChildren();
            
            for (int i=0; i<list.size(); i++) {
                Element carElement = list.get(i);

                if("Ferarri 101".equals(carElement.getText())) {
                   carElement.setText("my car 001");
                }
                if("Ferarri 201".equals(carElement.getText())) {
                   carElement.setText("my car 002");
                }
            }
            
            //get all supercars element
            List<Element> supercarslist = rootElement.getChildren();

            for (int temp = 0; temp < supercarslist.size(); temp++) {
               Element tempElement = supercarslist.get(temp);
               
               // remove luxuery cars
               if("luxurycars".equals(tempElement.getName())) {
                  rootElement.removeContent(tempElement);
               }        	 
            }

            XMLOutputter xmlOutput = new XMLOutputter();

            // display xml
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, System.out);  
 
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
