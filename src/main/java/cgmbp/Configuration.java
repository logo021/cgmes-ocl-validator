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
 *       2020-12-28	LOO	Substantially reduced and environment variables removed
 *       				File structure changed to fit xslts
 **/

package cgmbp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class Configuration {

	public static Path configDir = Paths.get("config");
    public static Path inputDir = Paths.get("xmi");
    public static Path reportsDir = Paths.get("reports");
    public static Boolean debugMode = false;
    public static int batchSize = 2;

    public static Boolean generateXMLreports = false;
    public static Boolean generateXLSreports = false;

    public static HashMap<String,String> configs = null;

    static Logger logger = null;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        logger = Logger.getLogger(Configuration.class.getName());

        try {
            configs = getConfig();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Inizializes configurations
     * @return
     * @throws IOException
     */
    public static HashMap<String,String> getConfig() throws IOException {
        HashMap<String,String> configs =  new HashMap<>();
        
        InputStream config = new FileInputStream(configDir+File.separator+"config.properties");
        Properties properties = new Properties();
        properties.load(config);
        
        String debug = properties.getProperty("debugMode");
        String xmlreport = properties.getProperty("generateXMLreports");
        String xlsreport = properties.getProperty("generateXLSreports");
        
        if (debug!=null) debugMode = debug.equalsIgnoreCase("TRUE");
        if (xlsreport != null) generateXLSreports = xlsreport.equalsIgnoreCase("TRUE");
        if (xmlreport != null) generateXMLreports = xmlreport.equalsIgnoreCase("TRUE");
       

        String rules_descr = configDir+File.separator+properties.getProperty("rules_descr");
        String ecore_model = configDir+File.separator+properties.getProperty("ecore_model");

        if (rules_descr!=null && ecore_model!=null){
            configs.put("rules_descr", rules_descr);
            configs.put("ecore_model", ecore_model);
        }
        else{
            logger.severe("Variable rules_descr or ecore_model are missing from properties file");
            System.exit(0);
        }

        return configs;
    }
}
