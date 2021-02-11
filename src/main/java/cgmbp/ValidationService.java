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
 *       2020-12-28	LOO	Substantially reduced, code from several other files pulled in
 *       				Intermediate Jason result files removed
 **/
package cgmbp;

import java.io.InputStream;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.IllegalValueException;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.ocl.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.ocl.pivot.model.OCLstdlib;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

public class ValidationService {
	
	static Logger logger = null;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        logger = Logger.getLogger(ValidationService.class.getName());

    }
    
	private static Resource ecoreResource;
    public static ResourceSet resourceSet;
    private static URI mmURI;
    public static HashMap<String, RuleDescription> rules;
    public static List<EPackage> myPList = new ArrayList<>();
    
	static {
			
		RuleDescriptionParser parser = new RuleDescriptionParser();
		rules = parser.parseRules("config/UMLRestrictionRules.xml");
		parser = null;
		
		resourceSet = new ResourceSetImpl();
		CompleteOCLStandaloneSetup.doSetup();
		OCLstdlib.install();
		
		String ecoreFileName = Configuration.configs.get("ecore_model");
		java.util.Map<java.lang.String, java.lang.Object> rfrm = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
		rfrm.put("ecore", new EcoreResourceFactoryImpl());
		mmURI = URI.createFileURI(new File(ecoreFileName).getAbsolutePath());
		
		try {
            ecoreResource = resourceSet.getResource(mmURI, true);
        }
        catch (Exception e ){
            logger.severe("Ecore file missing in "+Configuration.configs.get("ecore_model")+" !");
            System.exit(0);
        }
		
		myPList = getPackages(ecoreResource);
		
	}
	
	private static List<EPackage> getPackages(Resource r){
        ArrayList<EPackage> pList = new ArrayList<EPackage>();
        if (r.getContents() != null)
            for (EObject obj : r.getContents())
                if (obj instanceof EPackage) {
                    pList.add((EPackage)obj);
                }
        TreeIterator<EObject> test = r.getAllContents();
        while(test.hasNext()){
            EObject t = test.next();
            if(t instanceof EPackage)
                pList.add((EPackage)t);
        }
        return pList;
    }
    
	public static ResourceSet createResourceSet(){

        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		for(EPackage ePackage : myPList){
            resourceSet.getPackageRegistry().put(ePackage.getNsURI(),ePackage);
        }
		
        return resourceSet;
    }
	
	public Diagnostic evaluate(InputStream inputStream){
		
		ResourceSet resourceSet = createResourceSet();
		
		HashMap<String, Boolean> options = new HashMap<>();
		UUID uuid = UUID.randomUUID();
		options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION,true);
		Resource model = resourceSet.createResource(URI.createURI(uuid.toString()));
		
		try {
            model.load(inputStream,options);
		} catch (Resource.IOWrappedException we){
            Exception exc = we.getWrappedException();
            if (exc instanceof IllegalValueException){
                IllegalValueException ive = (IllegalValueException) exc;
                EObject object = ive.getObject();
                String name = (object.eClass().getEStructuralFeature("name") != null) ? (" "+object.eGet(object.eClass().getEStructuralFeature("name"))+" ") : "";
                String id =  (object.eClass().getEStructuralFeature("mRID") != null) ? String.valueOf(object.eGet(object.eClass().getEStructuralFeature("mRID"))) : null;
                logger.severe("Problem with: " + id + name + " (value:" + ive.getValue().toString() + ")");
            }
            if (Configuration.debugMode) exc.printStackTrace();
            else logger.severe(exc.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
		
		EObject rootObject = model.getContents().get(0);
		Diagnostician dc = new Diagnostician();
		Diagnostic diagnostics = dc.validate(rootObject);
		
		return diagnostics;
	}
	
	public Map<String, List<EvaluationResult>> getErrors(Diagnostic diagnostics, HashMap<String, RuleDescription> rules, String fileName) {

        List<EvaluationResult> results = new ArrayList<>();
        Map<String, List<EvaluationResult>> synthesis = new HashMap<>();
        
        if (diagnostics==null) return synthesis;
        for (Diagnostic childDiagnostic: diagnostics.getChildren()){
            List<?> data = childDiagnostic.getData();
            EObject object = (EObject) data.get(0);
            String msg;
            Matcher matcher;
            Pattern pattern = Pattern.compile(".*'(\\w*)'.*");
            if(data.size()==1){
                msg = childDiagnostic.getMessage();
                matcher = pattern.matcher(msg);
                while (matcher.find()) {
                    String name = (object.eClass().getEStructuralFeature("name") != null) ? String.valueOf(object.eGet(object.eClass().getEStructuralFeature("name"))) : null;
                    String ruleName = matcher.group(1);
                    if (!excludeRuleName(ruleName)){
                        String severity = rules.get(ruleName) == null ? "UNKOWN" : rules.get(ruleName).getSeverity();
                        int level = rules.get(ruleName) == null ? 0 : rules.get(ruleName).getLevel();
                        results.add(new EvaluationResult(severity,
                                ruleName,
                                level,
                                object.eClass().getName(),
                                (object.eClass().getEStructuralFeature("mRID") != null) ? String.valueOf(object.eGet(object.eClass().getEStructuralFeature("mRID"))) : null,
                                name, null
                        ));
                    }
                }
            } else {
                // it is for sure a problem of cardinality, we set it by default to IncorrectAttributeOrRoleCard,
                // later on we check anyway if it exists in UMLRestrictionRules file
                msg = childDiagnostic.getMessage();
                matcher = pattern.matcher(msg);
                while (matcher.find()) {
                    String ruleName = matcher.group(1);
                    if (!excludeRuleName(ruleName)) {
                        if(rules.get(ruleName)==null){
                            ruleName = "IncorrectAttributeOrRoleCard";
                        }
                        String severity = rules.get(ruleName) == null ? "UNKOWN" : rules.get(ruleName).getSeverity();
                        int level = rules.get(ruleName) == null ? 0 : rules.get(ruleName).getLevel();
                        EvaluationResult evaluationResult = new EvaluationResult(severity,
                                ruleName,
                                level,
                                object.eClass().getName(),
                                (object.eClass().getEStructuralFeature("mRID") != null) ? String.valueOf(object.eGet(object.eClass().getEStructuralFeature("mRID"))) : null,
                                null, null
                        );
                        if(rules.get(ruleName)!=null){
                            evaluationResult.setSpecificMessage(matcher.group(1)+" of "+object.eClass().getName()+" is required.");
                        }
                        results.add(evaluationResult);
                    }
                }
            }
        }
        
        synthesis.put(fileName, results);
        
        return synthesis;
    }
	
	private boolean excludeRuleName(String ruleName){
        // MessageType introduced in ecore file but not managed on Java side
        return "MessageType".equalsIgnoreCase(ruleName);
    }
}
