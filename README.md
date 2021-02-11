# EMF/OCL Validator Java Wrapper version 1.0

This project is a framework to automate the validation of xmi files. 
It uses the Eclipse Modeling Framework (EMF) and the Object Constraint Language (OCL).

## Purpose

The Java Wrapper is intended to validate CIMXML files assembled into models
called Individual Grid Models (IGMs) or Common Grid Models (CGMs) within ENTSO-E.

The goal of this tool is to provide information about the quality of the CIMXML files
and the IGMs/CGMs.
The specifications that describe the CIMXML files are
- IEC TS 61970-600 (CGMES 2.4.15).
- IEC 61970-552

To perform the validation the CIMXML files and IGMs/CGMs need to be converted to xmi files.
The original Java Wrapper did this conversion but it has been removed from this version.
Reason is that there are two solutions for the conversion, the one in the original 
Java Wrapper and an XSLT solution that was used as the template for creating the 
Java Wrapper conversion. Maintaining the two solutions turned out to be difficult so the original
XSLT solution was kept and the Java Wrapper conversion was removed. Hence this version of the
Java Wrapper do not include the CIMXML to xmi conversion and use xmi files as input.

With the CIMXML to xmi conversion removed from the Java Wrapper it has become almost generic 
only described by an ecore file (the information model) and ocl rules in the ecore file.

But the Java Wrapper still contains non generic parts and they are
 - the xmi file name contains meta data that is used
 - the result report, the QAR, is specific to an XML schema used by ENTSO-E
 - a description of the OCL rules, also a CIMXML document.


## Compilation and packaging

### Requirements

- Java 64 bits >= 1.8. It can be downloaded from https://www.java.com/fr/download/.
- Maven >= 3.5. it can be downloaded from https://maven.apache.org/download.cgi
- Python is needed to run the startup scripts and can be downloaded from https://www.python.org/downloads/

Make sure that Python is included in the Windows path, there is a install checkbox for this

For Maven, if you are behind a proxy, be sure to set the configuration properly or use a maven repository managed by your organization.

### Compilation

Run the command `mvn clean package` to generate the jar file of the validation library. The generated jar 
is saved in the `target` folder.

As an alternative the files may be loaded in an Eclipse project where compilation and packaging is done.


### Distributable library with dependencies

Run the command `mvn clean install` to create a redistributable package containing the validator library 
with required dependencies and scripts to easily launch the validator. 
The archived validator is stored in the `target` folder.

## Runtime

### Requirements

This tool requires Java >= 1.8.
It can be downloaded from https://www.java.com/fr/download/.

Python is needed to run the launch script.

The tool has been tested under Windows only.

## Use the installation package

Unzip the installation package wherever you want.
The installation package contains has the following structure:

- config/		the input configuration files
- reports/		the resulting reports. The Svedala test network reports are included as reference
- scripts/		The Python script to run the Java Wrapper
- target/		the executable Java Wrapper jar file.
- xmi/			igms/cgms converted to xmi, input to the validation. The Svedala xmi file is present as reference

## Validate
### Prepare for validation

- copy the following latest files to the `config` directory; information model (cgmes61970oclModel.ecore), rule descriptions (UMLRestrictionRules.xml) and QAR XML schema (QAR_v4.1_CGMBP_23032020.xsd)
- remove the `temp` directory
- empty the `reports` directory from any not wanted reports
- The meta data files can be aquired from ENTSO-E, please contact CGMBP working group or digital section

### How to validate

Run the script `validate.py` to start the validation of the xmi files

### Get the results

The results are picked up in the `reports` directory, there are two sub directories
- `ModelExcelReports` the model validation result on MS Excel format
- `ModelQAReports` the model validation result according to the XML schema

## Disclaimer

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Copyright
&copy; RTE 2019-2020
Contributors: Marco Chiaramello, Jérôme Picault, Maria Hergueta Ortega, Thibaut Vermeulen, 
&copy; ENTSO-E 2020 Lars-Ola Gottfried Österlund

## License
Mozilla Public License 2.0

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

