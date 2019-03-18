/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abbvie.nifi.processors.xmlparser;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
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
import org.apache.nifi.processor.io.OutputStreamCallback; 

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.nifi.logging.ComponentLog;

@Tags({"example"})
@CapabilityDescription("Provide a description")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class LuTestReadFile extends AbstractProcessor {

    public static final PropertyDescriptor PROPERTY_FILE_PATH = new PropertyDescriptor
            .Builder().name("PROPERTY_FILE_PATH")
            .displayName("Input File Path")
            .description("Example Property")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .defaultValue("/local/mlapp/tools/nifi/abbvie.xml")
            .build();

    public static final Relationship REL_SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Example relationship")
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        descriptors.add(PROPERTY_FILE_PATH);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<Relationship>();
        relationships.add(REL_SUCCESS);
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
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        //FlowFile flowFile = null;
        final String inputFile = context.getProperty(PROPERTY_FILE_PATH).toString(); 
        final ComponentLog logger = getLogger();
        logger.error("start -------------------------" + inputFile);
        FlowFile flowFile = session.create();
        try {
            
            // test reading a file into flow file
            Path path = Paths.get(inputFile);
            logger.error("----------------- file path = "+path.toString());
            flowFile = session.importFrom(path, true, flowFile);
            
            /*
            //  Test flow file from a string
            String testString = "this is a test a test a test";
            final byte[] data = testString.getBytes();
            if (data.length > 0) {
                flowFile = session.write(flowFile, new OutputStreamCallback() {
                    @Override
                    public void process(final OutputStream out) throws IOException {
                        out.write(data);
                    }
                });
            }
            */
                
            logger.error("--------------- done generate flow file" );
            logger.error("--------------- here is the flow file = " + flowFile.toString());
        
            /*
            Map<PropertyDescriptor, String> processorProperties = context.getProperties();
            Map<String, String> generatedAttributes = new HashMap<String, String>();
            for (final Map.Entry<PropertyDescriptor, String> entry : processorProperties.entrySet()) {
                PropertyDescriptor property = entry.getKey();
                if (property.isDynamic() && property.isExpressionLanguageSupported()) {
                    String dynamicValue = context.getProperty(property).evaluateAttributeExpressions().getValue();
                    generatedAttributes.put(property.getName(), dynamicValue);
                }
            }
            
            
            flowFile = session.putAllAttributes(flowFile, generatedAttributes);

            */

            //session.getProvenanceReporter().receive(flowFile, file.toURI().toString(), importMillis);
            logger.error("I am here 2222222222222222222222222--");
            session.transfer(flowFile, REL_SUCCESS);
            logger.error("I am here 33333333333333333333333--");
            return; 
        } catch (Exception e) {
            logger.error("I am here  55555555555555555555"+e.toString());
            session.remove(flowFile);
            return;
        }
           
    }
}
