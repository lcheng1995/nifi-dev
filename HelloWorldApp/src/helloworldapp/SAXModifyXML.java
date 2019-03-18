/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldapp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author CHENGLX3
 */
public class SAXModifyXML {
    
    public static void doSomething() {
        try {
            // specify input file
            File inputFile = new File("data\\student.xml");
    
            // initiate an instance of SAXModifyHandler
            SAXModifyXMLHandler saxHandler = new SAXModifyXMLHandler();
            saxHandler.processXML(inputFile);
            
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }  
}

class SAXModifyXMLHandler extends DefaultHandler {
    
    // output captured in displayText String Aarry
    static String displayText[] = new String[100];
    static int numberLines = 0;
    static String indentation = "    ";
    
    boolean bFirstName  = false;
    boolean bLastName = false;
    boolean bNickName = false;
    boolean bMarks = false;
    String rollNo;
    
    public void processXML(File input) {
        DefaultHandler handler = this;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(input, handler);
         
            // specify a file writer
            FileWriter fileWriter = new FileWriter("data\\out2_student.xml");
            
            for (int i=0; i<numberLines; i++) {
                fileWriter.write(displayText[i].toCharArray());
                fileWriter.write("\n");
                System.out.println(displayText[i].toString());
            }
            fileWriter.close();
        } catch (Throwable t) { t.printStackTrace(); }
    }
    
    @Override
    public void startDocument() throws SAXException {
        // write XML declaration
        displayText[numberLines]  = indentation 
            + "<?xml version = \"1.0\" encoding = \"UTF-8\"?>";
        numberLines++;
    }
    
   
    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        displayText[numberLines] = indentation;
        displayText[numberLines] += "<?";
        displayText[numberLines] += target;        
        
        if (data != null && data.length() > 0) {
            displayText[numberLines] +=' ';
            displayText[numberLines] += data;            
        }
        displayText[numberLines] += "?>";
        numberLines++;        
    }

    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        
        displayText[numberLines] = indentation;   
        indentation += "    ";     
        displayText[numberLines] += '<';
        displayText[numberLines] += qName;
        
        if (attributes != null) {        
            for ( int i = 0; i<attributes.getLength(); i++) {
                displayText[numberLines] += ' ';
                displayText[numberLines] += attributes.getQName(i);
                displayText[numberLines] += "\"";
                displayText[numberLines] += attributes.getValue(i);
                displayText[numberLines] += '"';
            }
        }          
        displayText[numberLines] += '>';
        numberLines++;        
    }
    
    @Override
    public void characters(char ch[],  int start, int length) throws SAXException {
        // trim spaces
        String charData = (new String(ch, start, length)).trim();
        
        if ( charData.indexOf("\n") <0 && charData.length() > 0 ) {
            displayText[numberLines] = indentation;
            displayText[numberLines] += charData;
            numberLines++;  
        }       
    }
    
    @Override
    public void endElement(String uri, String LocalName, String qName) throws SAXException {
        indentation = indentation.substring(0, indentation.length()-4);
        displayText[numberLines] = indentation;
        // write end tag
        displayText[numberLines] += "</";
        displayText[numberLines] += qName;
        displayText[numberLines] += '>'; 
        numberLines++;  
         
        if (qName.equals("marks")) {
            startElement("", "Result", "Result", null);
            characters("Pass".toCharArray(), 0, "Pass".length());
            endElement("", "Result", "Result");
        }       
    }
}
