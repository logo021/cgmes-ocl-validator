/**
 *       THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *       EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *       OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *       SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *       INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *       TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *       BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *       CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *       ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *       DAMAGE.
 *       (c) RTE 2019 Authors: Marco Chiaramello, Jerome Picault
 *       ENTSO-E 2020 Authors: Lars-Ola Österlund
 *       
 *       Change history
 *       Date		By	Description
 *       2020-12-28	LOO	Reference to rule description file moved to config.properties
 **/
package cgmbp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

public class RuleDescriptionParser {

    private static Logger LOGGER = null;
    private DocumentBuilder dBuilder = null;
    private Document doc = null;
    
    
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER=Logger.getLogger(RuleDescriptionParser.class.getName());
    }


    public HashMap<String, RuleDescription> parseRules(String inputFile) {

        HashMap<String, RuleDescription> rules = new HashMap<>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        
        try {
        dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) 
        	{e.printStackTrace();}
        
        if(!new File(inputFile).exists()){
            LOGGER.severe(Configuration.configs.get("rules_descr")+" is missing !");
            System.exit(0);
        }
        
        try {
        doc = dBuilder.parse(inputFile);}
        catch (IOException | SAXException e) 
        	{e.printStackTrace();}
        
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("cgmbp:UMLRestrictionRule");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            RuleDescription rd = new RuleDescription();

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                rd.setName(eElement.getElementsByTagName("cgmbp:UMLRestrictionRule.name")
                        .item(0)
                        .getTextContent());
                rd.setSeverity(eElement.getElementsByTagName("cgmbp:UMLRestrictionRule.severity")
                        .item(0)
                        .getTextContent());
                rd.setLevel(new Integer(eElement.getElementsByTagName("cgmbp:UMLRestrictionRule.level")
                        .item(0)
                        .getTextContent()));
                rd.setDescription(eElement.getElementsByTagName("cgmbp:UMLRestrictionRule.description")
                        .item(0)
                        .getTextContent());
                Node nm = eElement.getElementsByTagName("cgmbp:UMLRestrictionRule.message")
                        .item(0);
                if (nm == null) rd.setMessage(""); else rd.setMessage(nm.getTextContent());
            }

            rules.put(rd.getName(), rd);

        }

        return rules;
    }
}
