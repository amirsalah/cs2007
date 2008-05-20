import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *  Used to parse definition file
 **/

public class XSLTProcessor {
    DOMSource source;  // a DOM source used by XSLT processors

    public XSLTProcessor(String def_file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        File dataFile = new File(def_file); // the XML based definition file
        //factory.setNamespaceAware(true);
        //factory.setValidating(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(dataFile);
            source = new DOMSource(document);   // Initialize the DOM tree
        } catch(SAXException sxe) {
            // Error generated by this application
            // (or a parser-initialization error)
            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }

            x.printStackTrace();
        } catch(ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch(IOException ioe) {
            System.out.println("Could not find the file: " + def_file);
        }
    }

    /**
     * Generate a file containing ELC wrapper functions
     */
    public void genELCWrappers(){
//        File styleSheet = new File("DefinitionFile/ELCTemplate.xsl");
        File styleSheet = new File("DefinitionFile/ELCTemplate.xsl");
        try {
        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(styleSheet);
        Transformer transformer = tFactory.newTransformer(stylesource);

        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
        } catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            System.out.println("\n** Transformer Factory error");
            System.out.println("   " + tce.getMessage());

            // Use the contained exception, if any
            Throwable x = tce;

            if (tce.getException() != null) {
                x = tce.getException();
            }

            x.printStackTrace();
        } catch (TransformerException te) {
            // Error generated by the parser
            System.out.println("\n** Transformation error");
            System.out.println("   " + te.getMessage());

            // Use the contained exception, if any
            Throwable x = te;

            if (te.getException() != null) {
                x = te.getException();
            }

            x.printStackTrace();
        }
    }

    public void genExtFunc(){

    }
}
