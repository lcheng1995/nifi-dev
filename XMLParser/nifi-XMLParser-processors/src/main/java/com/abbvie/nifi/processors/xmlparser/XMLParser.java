/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abbvie.nifi.processors.xmlparser;

import static com.abbvie.nifi.processors.xmlparser.DOM4JXMLParser.generateGroupsXML;
import java.io.IOException;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.io.OutputStreamCallback; 
import org.apache.nifi.processor.exception.FlowFileHandlingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream; 
import java.io.UnsupportedEncodingException;
import java.io.StringReader;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;


@Tags({"xml,parser"})
@InputRequirement(Requirement.INPUT_REQUIRED)
@CapabilityDescription("Provide a description")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class XMLParser extends AbstractProcessor {

    public static final PropertyDescriptor PROPERTY_ROOT_TAG_NAME = new PropertyDescriptor
            .Builder()
            .name("PROPERTY_ROOT_TAG_NAME")
            .displayName("Root Tag Name")
            .description("This is the root tag name of the parsed XML")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .defaultValue("MyRoot")
            .build();
    
    public static final PropertyDescriptor PROPERTY_INCLUDE_COUNT = new PropertyDescriptor
            .Builder()
            .name("PROPERTY_INCLUDE_COUNT")
            .displayName("Include Count")
            .description("The number of group xml will be included in the output")
            .required(true)
            .addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
            .defaultValue("2")
            .build();

    public static final Relationship REL_ORIGINAL = new Relationship.Builder()
            .name("original")
            .description("Optional: Original relationship")
            .autoTerminateDefault(true)
            .build();
    
    public static final Relationship REL_PARSED = new Relationship.Builder()
            .name("parsed")
            .description("Parsed relationship")
            .build();
    
    public static final Relationship REL_GROUPS = new Relationship.Builder()
            .name("groups")
            .description("Groups relationship")
            .build();
    
    public static final Relationship REL_FAILURE = new Relationship.Builder()
            .name("failure")
            .description("If a FlowFile fails processing for any reason (for example, the FlowFile is not valid XML), it will be routed to this relationship")
            .autoTerminateDefault(true)
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        descriptors.add(PROPERTY_ROOT_TAG_NAME);
        descriptors.add(PROPERTY_INCLUDE_COUNT);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(REL_ORIGINAL);
        relationships.add(REL_PARSED);
        relationships.add(REL_GROUPS);
        relationships.add(REL_FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {

    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) 
        throws ProcessException {
        // get properties
        final int includeCount = context.getProperty(PROPERTY_INCLUDE_COUNT).asInteger();
        final String rootTagName = context.getProperty(PROPERTY_ROOT_TAG_NAME).toString(); 
        
        final ComponentLog logger = getLogger();

        //
        Document originalXML = DocumentHelper.createDocument();
        Document parsedXML = DocumentHelper.createDocument();
        List<Document> groupXMLs = new ArrayList<>();  
        final List<FlowFile> groupXMLFlow= new ArrayList<>();
        
        // 
        logger.debug("Get the original flow file");
        FlowFile originalFlowFile = session.get();
        if ( originalFlowFile == null ) {
            return;
        }
        logger.info("The original flow file info: "+originalFlowFile.toString());
        
        // Extract the originalXML document out of the original flow file. 
        logger.debug("Retrieve originalXML from originalFlowFile");
        String originalFlowFileString = stringFromFlowFile(originalFlowFile, session); 
        try {
            originalXML = DocumentHelper.parseText(originalFlowFileString); 
        } catch (DocumentException e) {
            logger.error("Failed to convert originalFlowFileString to XML Document"+e.toString());
        } catch (Exception e) {
            logger.error("Failed to extract from the originalFlowFileString"+e.toString());
        }
        
        // generate all groupXML and parsedXML from the original
        try {
            logger.debug("Generate groupXML and parsedXML from the original");     
            DOM4JXMLParser dom4jXMLParser = new DOM4JXMLParser(originalXML, includeCount);
            
            // generate groupsXMLs
            groupXMLs = dom4jXMLParser.generateGroupsXML();  
            groupXMLs.forEach((groupXML)-> {
                logger.debug("GroupXML document = "+ groupXML.asXML());
            });
            //geneate parsedXML
            parsedXML = dom4jXMLParser.generateParsedsXML(groupXMLs, rootTagName); 
            logger.debug("The parsed document = "+ parsedXML.asXML());
        } catch (UnsupportedEncodingException e) {         
            logger.error("Encounter UnsupportedEncodingException while generating XMLs: "+e.toString());
            return;
        } catch (IOException e) {
            logger.error("Encounter IOException while generating XMLs: "+e.toString());
            return; 
        } catch (DocumentException e) {
            logger.error("Encounter DocumentException while generating XMLs: "+e.toString());
            return;
        } 
        
        // Process each groupXM to flow file and transfer accordingly
        groupXMLs.forEach( (groupXML) -> {
            byte[] groupXMLBytes = groupXML.asXML().getBytes();
            FlowFile groupFlowFile = session.create();
            try {
                logger.debug("Write groupXML to outputStream, groupXM = "+ groupXML.asXML());
                groupFlowFile = session.write(groupFlowFile, outputStream -> outputStream.write(groupXMLBytes));
                logger.debug("transfer groupXML to REL_GROUP");
                session.transfer(groupFlowFile, REL_GROUPS);  
            } catch (Exception e) {
                logger.error("Failed to write and transfer groupXML to REL_GROUP: "+e.toString()); 
                session.remove(groupFlowFile);
                return;
            }
         });


        // process parsedXML to flow file and transfer to REL_PARSED
        FlowFile parsedFlowFile = session.create();
        byte[] parsedXMLByte = parsedXML.asXML().getBytes();
        try {
            logger.debug("Write parsedXML to outputStream, parsedXML = "+ parsedXML.asXML());
            parsedFlowFile = session.write(parsedFlowFile, outputStream -> outputStream.write(parsedXMLByte));
            logger.debug("transfer parsedXML to REL_PARSED");
            session.transfer(parsedFlowFile, REL_PARSED); 
        } catch (Exception e) {
            logger.error("Failed to write and transfer parsedXML to REL_PARSED: "+e.toString()); 
            session.remove(parsedFlowFile);
            
            return;
        }
        
        // transfer the original flow file to REL_ORIGINAL
        session.transfer(originalFlowFile, REL_ORIGINAL);
        //session.remove(originalFlowFile);
        return;
    }
    
    public static String stringFromFlowFile(FlowFile flow, ProcessSession session) {

       ByteArrayOutputStream bytes = new ByteArrayOutputStream();
       session.exportTo(flow, bytes);
       return bytes.toString();

   }
    
    /*
    public static Document documentFromString(final String xmlString) throws ParserConfigurationException, SAXException, IOException {

        InputSource src = new InputSource(new StringReader(xmlString));
 
        DocumentBuilder buider =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return buider.parse(src);
   }
    */


}



/*
byte[] groupXMLByte = groupXML.asXML().getBytes();
                FlowFile groupXMLFlowFile = session.create();
                groupXMLFlowFile = session.write(groupXMLFlowFile, new OutputStreamCallback() {
                    @Override
                    public void process(final OutputStream out) throws IOException {
                        out.write(groupXMLByte);
                    }
                });
*/


/*
    byte[] parsedXMLByte = parsedXML.asXML().getBytes();
    Map<PropertyDescriptor, String> processorProperties = context.getProperties();
    Map<String, String> generatedAttributes = new HashMap<String, String>();
    for (final Map.Entry<PropertyDescriptor, String> entry : processorProperties.entrySet()) {
        PropertyDescriptor property = entry.getKey();
        if (property.isDynamic() && property.isExpressionLanguageSupported()) {
            String dynamicValue = context.getProperty(property).evaluateAttributeExpressions().getValue();
            generatedAttributes.put(property.getName(), dynamicValue);
        }
    }
*/ 


/*
// read in original XML doc
session.read(originalFlowFile, rawIn -> {
    try (final InputStream in = new java.io.BufferedInputStream(rawIn)) {
        SAXReader reader = new SAXReader();
        reader.read(in);
    } catch (DocumentException e) {
        logger.error("Unable to parse {} due to {}", new Object[]{originalFlowFile, e});
        failed.set(true);
    }
});
*/