/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;

import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author CHENGLX3
 */
public class SAXParserDemo {
    public static void doSomething() {
        try {
            File inputFile = new File("data/student.xml");
            SAXParserFactory sFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = sFactory.newSAXParser();
            UserHandler userHandler = new UserHandler();
            saxParser.parse(inputFile, userHandler);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class UserHandler extends DefaultHandler {
    
    boolean bFirstName  = false;
    boolean bLastName = false;
    boolean bNickName = false;
    boolean bMarks = false;
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
        if ( qName.equalsIgnoreCase("student")) {
            String rollNo = attributes.getValue("rollno"); 
            System.out.println("    Roll no="+rollNo);
        } else if (qName.equalsIgnoreCase("firstname")){
            bFirstName = true;
        } else if (qName.equalsIgnoreCase("lastname")){
            String lastname = attributes.getValue("lastname"); 
            bLastName = true;
        } else if (qName.equalsIgnoreCase("nickname")){
            bNickName = true;
        } else if (qName.equalsIgnoreCase("marks")){
            bMarks=true;
        }
    }
    
    @Override
    public void endElement(String uri, String LocalName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("student")) {
            System.out.println("End element ="+qName);
        }
    }
    
    @Override
    public void characters(char ch[],  int start, int length) throws SAXException {
        if(bFirstName) {
                System.out.println("    First Name="+ new String(ch, start, length));
                bFirstName = false;
        } else if(bLastName) {
            System.out.println("    Last Name="+ new String(ch, start, length));
            bLastName = false;
        } else if(bNickName) {
            System.out.println("    Nick Name="+ new String(ch, start, length));
            bNickName = false;
        } else if(bMarks) {
            System.out.println("    bMarks="+ new String(ch, start, length));
            bMarks = false;
        }  
    }
}

