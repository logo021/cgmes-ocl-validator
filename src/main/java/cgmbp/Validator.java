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
 *       2020-12-28	LOO	Former Validation, file transformations and parallelization removed
 *       				Calls to QAR and Excel reporting changed
 **/

package cgmbp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.emf.common.util.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.io.File;

	public class Validator {
	
	private static Logger LOGGER = null;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Validator.class.getName());
    }
    

	public static void main(String[] args) throws IOException {
		
		LOGGER.info("Initialize Validation");
		
		InputStream xmiStream = null;
		XLSReportWriter xlswriter = new XLSReportWriter();
		XMLReportWriter xmlwriter = new XMLReportWriter();
		ValidationService validationService = new ValidationService();
		
		File inputDir = new File(Configuration.inputDir.toAbsolutePath().toString()+File.separator);
		File[] inputFiles = inputDir.listFiles();
		
		LOGGER.info("Start Validation");
		
		for (File f: inputFiles){
			LOGGER.info("Validation of "+f.getName());
			xmiStream = new FileInputStream(f);
			Diagnostic diagnostics = validationService.evaluate(xmiStream);
			Map<String, List<EvaluationResult>> synthesis = validationService.getErrors(diagnostics, ValidationService.rules, f.getName());
			
			LOGGER.info("Creating reports...");
			
			if (Configuration.generateXLSreports) xlswriter.writeResultsPerIGM(synthesis, ValidationService.rules, Configuration.reportsDir.resolve("ModelExcelReports"));
			if (Configuration.generateXMLreports) xmlwriter.writeResultsPerIGM(synthesis, ValidationService.rules, Configuration.reportsDir.resolve("ModelQAReports"));
			
			LOGGER.info("All reports created");
			
	        diagnostics = null;
		}
		xlswriter = null;
		xmlwriter = null;
		validationService = null;
		inputFiles = null;
		inputDir = null;
		LOGGER.info("End Validation");
	}
}
