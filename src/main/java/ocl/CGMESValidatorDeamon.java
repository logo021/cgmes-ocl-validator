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
 *       (c) RTE 2019
 *       Authors: Marco Chiaramello, Jerome Picault
 **/
package ocl;

import ocl.service.ReportingService;
import ocl.service.TransformationService;
import ocl.service.ValidationService;
import ocl.service.WatchingService;
import ocl.service.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.Logger;

public class CGMESValidatorDeamon {

    private static Logger logger = null;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        logger = Logger.getLogger(WatchingService.class.getName());

    }

    private Path inputPath;

    private WatchingService watchingService;
    private TransformationService transformationService;
    private ValidationService validationService;
    private ReportingService reportingService;

    public CGMESValidatorDeamon(Path path){
        this.inputPath = path;

    }

    private void initializeServices() throws IOException {

        logger.info("Initialization of services...");
        // watching service
        watchingService = new WatchingService(inputPath);
        // transformation service
        transformationService = new TransformationService();
        watchingService.setListener(transformationService);
        // validation service
        validationService = new ValidationService();
        transformationService.setListener(validationService);
        // reporting service
        reportingService = new ReportingService();
        validationService.setListener(reportingService);

        logger.info("Initialization of services OK");
    }


    private void launchServices() throws InterruptedException{
        logger.info("Launching services...");
        new Thread(watchingService).start();
        new Thread(transformationService).start();
        new Thread(validationService).start();
        new Thread(reportingService).start();


    }

    private void cleanupServices(){

    }


    public static void main(String args[]){
        Locale.setDefault(new Locale("en", "EN"));

        CGMESValidatorDeamon deamon = new CGMESValidatorDeamon(Configuration.inputDir);

        // initialization of services
        try {
            deamon.initializeServices();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        // launch services
        try {
            deamon.launchServices();

        } catch (InterruptedException e){
            // clean services
            deamon.cleanupServices();
        }


    }

}
