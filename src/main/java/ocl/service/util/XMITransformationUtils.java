package ocl.service.util;

import ocl.Profile;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class XMITransformationUtils {

    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    static{
        builderFactory.setNamespaceAware(true);
    }

    /**
     * @param doc
     * @param name
     * @throws TransformerException
     */
    public static void printDocument(Document doc, String name) throws  TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc),new StreamResult(new File(name)));
    }


    /**
     *
     * @param name
     * @return
     */
    public static String getBusinessProcess(String name){
        String business = null;
        Pattern pattern = Pattern.compile("\\_(.*?)\\_.*");
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            for (int l = 1; l <= matcher.groupCount(); l++) {
                business=matcher.group(l);
            }
        }
        return business;
    }


    /**
     *
     * @param object
     * @return
     */
    public static String getSimpleNameNoExt(Profile object){
        int pos = object.file.getName().lastIndexOf(".");
        String name= pos>0 ? object.file.getName().substring(0,pos) : object.file.getName();
        return name;
    }


    /**
     *
     * @param list
     * @return
     */
    public static Node[] convertToArray(NodeList list)
    {
        int length = list.getLength();
        Node[] copy = new Node[length];

        for (int n = 0; n < length; ++n)
            copy[n] = list.item(n);

        return copy;
    }

    /**
     *
     * @param doc
     * @param node
     */
    public static synchronized Node addNode(Document doc, Node node){
        Node nd = doc.getDocumentElement().appendChild(doc.importNode(node,true));
        return nd;
    }



    /**
     *
     * @param profile
     * @return
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public static NodeList getNodeList(Profile profile) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = null;
        ZipFile zip = new ZipFile(new File(profile.file.getAbsolutePath()));
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream xmlStream = zip.getInputStream(entry);
            document = documentBuilder.parse(xmlStream);
            xmlStream.close();
        }
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        return nodeList;

    }

    public static NodeList getNodeList(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        return nodeList;
    }


    public static class BDObject{
        public Node TPn;
        public Node EQn;
        public Node CNn;
        public Node BVn;
    }

    public static class BDExtensions{
        public HashMap<String,Node> CimProfiles = new HashMap<>();
        public HashMap<String,Node> ProcessType = new HashMap<>();
        public HashMap<String,Node> ToBeAdded = new HashMap<>();
        public HashMap<String,Node> ModelingAuthority = new HashMap<>();
        public HashMap<String,Node> GeographicalRegionIds = new HashMap<>();
        public HashMap<String,Node> GeographicalRegionEIC = new HashMap<>();
    }


}
