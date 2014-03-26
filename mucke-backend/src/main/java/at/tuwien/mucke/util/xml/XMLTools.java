package at.tuwien.mucke.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.Charset;

/**
 * XML tools shared between classes of the search system
 *
 * @author Ralf Bierig
 */
public class XMLTools {

    /**
     * Extracts Nodes from the given file (encoded in latin ISO) based on the given xPath
     *
     * @param file  The file to extract from
     * @param xPath The xPath query
     * @return A list of matching nodes
     */
    public static NodeList getNodes(File file, String xPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        NodeList nodeList = null;
        try {
            builder = factory.newDocumentBuilder();
            String all = "";
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), Charset.forName("ISO-8859-1"));
            try {
                while (isr.ready()) {
                    all += ("" + (char) isr.read());
                }
            } catch (IOException e) {
                System.err.println("EXCEPTION while reading file: '" + file.getPath() + "'");
                e.printStackTrace();
            } finally {
                isr.close();
            }
            document = builder.parse(new InputSource(new StringReader(all)));
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(xPath);
            nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        } catch (Exception e) {
            System.err.println("EXCEPTION while extracting xPath '" + xPath + "': " + e.getMessage());
            //e.printStackTrace();
        }
        if (nodeList == null || nodeList.getLength() == 0) {
            // System.err.println("EMPTY node list. Check your XPath expression!");
        }
        return nodeList;
    }

}