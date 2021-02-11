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
 *       (c) RTE 2020
 *       Authors: Marco Chiaramello, Jerome Picault
 *       ENTSO-E 2020 Authors: Lars-Ola Österlund
 *       
 *       Change history
 *       Date		By	Description
 *       2020-12-28	LOO	Package structure simplified
 **/
package cgmbp;

//import cgmbp.Profile;
import cgmbp.EvaluationResult;
import cgmbp.RuleDescription;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public interface ReportWriter {

    Logger logger = Logger.getLogger(ReportWriter.class.getName());;

//    void writeSingleReport(Profile profile, List<EvaluationResult> results, HashMap<String, RuleDescription> rules, Path path) ;
    void writeSingleReport(String key, List<EvaluationResult> results, HashMap<String, RuleDescription> rules, Path path) ;

}
