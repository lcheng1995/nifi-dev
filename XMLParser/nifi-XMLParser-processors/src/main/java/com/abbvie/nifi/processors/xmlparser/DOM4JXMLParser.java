/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abbvie.nifi.processors.xmlparser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.XPath;


/**
 *
 * @author CHENGLX3
 */
class DOM4JXMLParser {
    
    private static final String INPUT_FILE_PATH = "data\\abbvie.xml";
    //private String rootTagName;
    private static int includeCount;
    private static Document original; 
    private static String namespace;  
    private static final Map<String, String> NS_CONTEXT = new HashMap<>();
    
    public DOM4JXMLParser(Document document, int includeCount) {
        DOM4JXMLParser.original = document;
        DOM4JXMLParser.includeCount = includeCount;
        
        // set namespace 
        setNamespaces(DOM4JXMLParser.original); 
    }
    
    public DOM4JXMLParser(int includeCount)  {      
        try { 
            //this.rootTagName = rootTagName; 
            DOM4JXMLParser.includeCount = includeCount;
            
            // read the input file in
            File inputFile = new File(INPUT_FILE_PATH);
            SAXReader reader = new SAXReader();  
            DOM4JXMLParser.original = reader.read( inputFile ); 
                  
            // set namespace 
            setNamespaces(DOM4JXMLParser.original); 
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }  
    
    private static void setNamespaces(Document document) {
        //get primary namespace
        DOM4JXMLParser.namespace = document.getRootElement().getNamespaceURI();
            //System.out.println(namespace);
            
            // set additoinal namespace context URI
        DOM4JXMLParser.NS_CONTEXT.put("p", namespace);   
        DOM4JXMLParser.NS_CONTEXT.put("d", "http://schemas.microsoft.com/ado/2007/08/dataservices");
        DOM4JXMLParser.NS_CONTEXT.put("m", "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata"); 
        DOM4JXMLParser.NS_CONTEXT.put("georss", "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata");
        DOM4JXMLParser.NS_CONTEXT.put("gml", "http://www.opengis.net/gml");
    }
    
    private static Document generateGroupXML(Node entryNode) throws DocumentException  {
        
        String groupId; 
        String principalType;
        String loginName;
        String title; 
        String loginElementValue; 
        
        Document groupXML = DocumentHelper.createDocument();
        Element root = groupXML.addElement( "group" );
       // System.out.println("Entry Node = ======= "+ entryNode.getName());
        
        //get group id
        XPath idXpath = DocumentHelper.createXPath("./p:id");
        idXpath.setNamespaceURIs(NS_CONTEXT);
        groupId = idXpath.selectSingleNode(entryNode).getText();
        root.addAttribute("id", groupId);
        //System.out.println("groupId="+groupId); 
        
        // get PrincipalType
        XPath principalTypeXpath = DocumentHelper.createXPath("./p:content/m:properties/d:PrincipalType");
        principalTypeXpath.setNamespaceURIs(NS_CONTEXT);
        principalType = principalTypeXpath.selectSingleNode(entryNode).getText();
        
        // get LoginName
        XPath loginNameXpath = DocumentHelper.createXPath("./p:content/m:properties/d:LoginName");
        loginNameXpath.setNamespaceURIs(NS_CONTEXT);
        loginName = loginNameXpath.selectSingleNode(entryNode).getText();
        
        // get Title
        XPath titleXpath = DocumentHelper.createXPath("./p:content/m:properties/d:Title");
        titleXpath.setNamespaceURIs(NS_CONTEXT);
        title = titleXpath.selectSingleNode(entryNode).getText();
         
        if ( principalType.equals("8")) {
            loginElementValue = loginName;
        } else {
            loginElementValue = title;
        }
        //System.out.println("------------------------------------------");
        //System.out.println("p="+principalType+", l="+loginName+", t="+title+", value="+loginElementValue);
        //add login element
        root.addElement("login")
            .addText(loginElementValue);  

        return groupXML;
    }
    
    public static List<Document> generateGroupsXML() 
        throws UnsupportedEncodingException, DocumentException, IOException  {  

        //  includeCount
        List<Document> groupXMLs = new ArrayList<>(); 
        int groupCounter = 0;  
        
        Element originalRoot = original.getRootElement();
        //System.out.println("Root element :" + original.getRootElement().getName());
       
        XPath entryXpath = DocumentHelper.createXPath("//p:entry");
        entryXpath.setNamespaceURIs(NS_CONTEXT);
        List<Node> entryNodes = entryXpath.selectNodes(original.getRootElement());
        
        // Loop through entryNode to generate groupXML docs
        for (Node entryNode : entryNodes) {  
            if (groupCounter == DOM4JXMLParser.includeCount ) {
                break; 
            }
            Document groupXML = DocumentHelper.createDocument();
            groupXML = generateGroupXML(entryNode);
            groupXMLs.add(groupXML );
            groupCounter++;

        }
        
        return groupXMLs;
    }
    
    public static Document generateParsedsXML( 
        List<Document> generateGroupsXML, String rootTagName) 
        throws UnsupportedEncodingException, DocumentException, IOException {
        
        Document parsedXML = DocumentHelper.createDocument();
        Element parsedXMLRoot = parsedXML.addElement(rootTagName);
        Element groupsElement = 
            parsedXMLRoot.addElement("groups")
                .addAttribute("count",  String.valueOf(includeCount)); 
        
        // add each groupXML to parsedXML under groups. 
        generateGroupsXML.forEach((groupXML)-> {
            Element groupXMLRootElement = groupXML.getRootElement();
            groupsElement.add(groupXMLRootElement);
        });
        
        return parsedXML;
    }
    
    public void printIt() {
        
               
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer;
        
        try { 
            List<Document> groupXMLs = generateGroupsXML();             
            writer = new XMLWriter( System.out, format );      
            // Write output  
            for (Document groupXML : groupXMLs) {
                System.out.println("------------------------------------------");
                writer.write(groupXML); 
            }
            
        } catch (UnsupportedEncodingException e) {         
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } 
    }
}



