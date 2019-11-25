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
package ocl.util;

import ocl.OCLEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.emf.common.util.Diagnostic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class XLSWriter {

    private static Logger LOGGER = null;
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER=Logger.getLogger(OCLEvaluator.class.getName());
    }

    public void writeResults(Map<String, List<EvaluationResult>> synthesis, File path){
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (String k : synthesis.keySet()){
            XSSFSheet sheet = workbook.createSheet(k);
            int rowNum = 0;
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            Cell cell = row.createCell(colNum++);
            cell.setCellValue("SEVERITY");
            cell = row.createCell(colNum++);
            cell.setCellValue("RULE");
            cell = row.createCell(colNum++);
            cell.setCellValue("OBJECT");
            cell = row.createCell(colNum++);
            cell.setCellValue("ID");
            cell = row.createCell(colNum++);
            cell.setCellValue("NAME");
            for (EvaluationResult res : synthesis.get(k)){
                row = sheet.createRow(rowNum++);
                colNum = 0;
                cell = row.createCell(colNum++);
                cell.setCellValue(res.getSeverity()== Diagnostic.ERROR?"ERROR":"WARNING");
                cell = row.createCell(colNum++);
                cell.setCellValue(res.getRule());
                cell = row.createCell(colNum++);
                cell.setCellValue(res.getType());
                cell = row.createCell(colNum++);
                cell.setCellValue(res.getId());
                cell = row.createCell(colNum++);
                String name = res.getName();
                cell.setCellValue(name==null?"":name);
            }
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, colNum-1));
            for (int i = 1; i < colNum; i++)
                sheet.autoSizeColumn(i);
            sheet.createFreezePane(0, 1);
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            workbook.close();
            LOGGER.info("Excel created: " + path);
        } catch (Exception e) {
            LOGGER.severe("Excel creation failed");
            e.printStackTrace();
        }
    }

}
