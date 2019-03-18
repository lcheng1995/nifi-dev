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
public class SAXQueryXML extends DefaultHandler {
    
    boolean bFirstName  = false;
    boolean bLastName = false;
    boolean bNickName = false;
    boolean bMarks = false;
    String rollNo; 
    
    public static void doSomething() {
        try {
            File inputFile = new File("data/student.xml");
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            
            SAXQueryXML handler = new SAXQueryXML();
            saxParser.parse(inputFile, handler);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
        if ( qName.equalsIgnoreCase("student")) {
            rollNo = attributes.getValue("rollno");   
        } 
        
        if ("393".equals(rollNo) && qName.equalsIgnoreCase("student")) {
            System.out.println("Start Elment ="+qName);
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
            if ("393".equals(rollNo) && qName.equalsIgnoreCase("student")) {
                System.out.println("End Elment ="+qName);
            }
        }
    }
    
    @Override
    public void characters(char ch[],  int start, int length) throws SAXException {
        if(bFirstName && "393".equals(rollNo)) {
            System.out.println("    First Name="+ new String(ch, start, length));
            bFirstName = false;
        } else if(bLastName && "393".equals(rollNo)) {
            System.out.println("    Last Name="+ new String(ch, start, length));
            bLastName = false;
        } else if(bNickName && "393".equals(rollNo)) {
            System.out.println("    Nick Name="+ new String(ch, start, length));
            bNickName = false;
        } else if(bMarks & "393".equals(rollNo)) {
            System.out.println("    bMarks="+ new String(ch, start, length));
            bMarks = false;
        }  
    }
}
