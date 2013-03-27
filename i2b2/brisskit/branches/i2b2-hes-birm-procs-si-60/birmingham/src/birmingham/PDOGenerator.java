package birmingham;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import london.PatientObs;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;
import com.web.DataSelect;

public class PDOGenerator {

	Set<PatientObs> unique = new TreeSet<PatientObs>();
	
	List<Model> patients = new ArrayList<Model>();
	
	  String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
	   			 "<pdo:patient_data xmlns:pdo=\"http://www.i2b2.org/xsd/hive/pdo/1.1/pdo\">" +
	   			 "<pdo:event_set>";

			  String xml2 = "</pdo:event_set>" +
	     			 "<pdo:pid_set>";

			  String xml3 = "</pdo:pid_set>" +
	     			 "<pdo:eid_set>";

			  String xml4 = "</pdo:eid_set>" +
	     			 "<pdo:patient_set>";

			  String xml5 = "</pdo:patient_set><pdo:observation_set>";

			  String xml6 = "</pdo:observation_set></pdo:patient_data>";
			  
	

	public void getExcel()
	{
		File file = new File("/home/si84/Documents/birm2.xls");
		try {
			FileInputStream in = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(in);
			ExcelExtractor extractor = new ExcelExtractor(workbook);
			String text = extractor.getText();
//			System.out.println(text);

			HSSFSheet sheet = workbook.getSheetAt(0);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        try {
        	FileInputStream in = new FileInputStream(file);
            HSSFWorkbook wb = new HSSFWorkbook(in);
            HSSFSheet sheet = wb.getSheetAt(0);

            // Iterate over each row in the sheet
            Iterator rows = sheet.rowIterator();
            while( rows.hasNext() ) {
                HSSFRow row = (HSSFRow) rows.next();
                System.out.println( "Row #" + row.getRowNum() );

                
                Model m = new Model();
                
                // Iterate over each cell in the row and print out the cell's content
                Iterator cells = row.cellIterator();
                while( cells.hasNext() ) {
                    HSSFCell cell = (HSSFCell) cells.next();
                   //System.out.println( "Cell #" + cell.getCellNum() );
                   
                   String cellnum = cell.getCellNum()+1 + "";
                   System.out.println( "Cell #" + cellnum );
                   
                    switch ( cell.getCellType() ) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                        	
                        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            	System.out.println( "D " +cell.getDateCellValue());
                                m.getVals().put(cellnum, cell.getDateCellValue().toGMTString()); 
                            }
                            else
                            {                          	
                            	 double doubleVal = cell.getNumericCellValue();
                            	 int intVal = (int) doubleVal;                	
                            	 System.out.println( "N " + intVal );
                            	 m.getVals().put(cellnum, intVal+""); 
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:                                                                           
                            	System.out.println( "S " +cell.getStringCellValue() );
                                m.getVals().put(cellnum, cell.getStringCellValue()); 
                            break;                         
                        default:
                            System.out.println( "unsuported sell type" );
                            break;
                    }
                }
                
                patients.add(m);

            }
            
            System.out.println("Size = " +patients.size());

        } catch ( IOException ex ) {
            ex.printStackTrace();
        }


	}

	public void createPDO()
	{
		int c = 0;
		
		String currentdate = "2013-03-03T00:00:00.000+01:00";
		String birthdate = "1972-04-25T00:00:00.000+01:00";
		String age = "40";
		String ethnicity = "Unknown";
		String sex = "MALE";
		
		String event = "";
		String pid  = "";
		String eid = "";
		String patient = "";
		String obs = "";
		
		for(Model row : patients) {
		   c++;	
		   


		  String pdo = "";
			  
		   if (c>=4)
		   {
			   System.out.println("ID " + row.getVals().get("1"));
			   
	            event += "<event download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate  + "</event_id>" +
	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
	        			 "<param column=\"ACTIVE_STATUS_CD\" name=\"active status\">F</param>" +
	        			 "<param column=\"INOUT_CD\" name=\"\">@</param>" +
	        			 "<param column=\"LOCATION_CD\" name=\"\">@</param>" +
	        			 "<param column=\"LOCATION_PATH\" name=\"\">@</param>" +
	        			 "<start_date>" + currentdate + "</start_date>" +
	        			 "<end_date>@</end_date>" +
	        			 "</event>";
	            
	            pid += "<pid>" +
	        			 "<patient_id download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" source=\"BRICCS\" status=\"Active\" update_date=\"" + currentdate + "\" upload_id=\"1\">" + row.getVals().get("1") + "BG</patient_id>" +
	        			 "</pid>";

                eid += "<eid>" +
	        			 "<event_id download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" source=\"BRICCS\" sourcesystem_cd=\"BRICCS\" status=\"Active\" update_date=\"" + currentdate + "\" upload_id=\"1\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
	        			 "</eid>";

                patient += "<patient download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
	        			 "<param column=\"vital_status_cd\" name=\"date interpretation code\">N</param>" +
	        			 "<param column=\"birth_date\" name=\"birthdate\">" + birthdate + "</param>" +
	        			 "<param column=\"age_in_years_num\" name=\"age\">" + age + "</param>" +
	        			 "<param column=\"race_cd\" name=\"ethnicity\">" + ethnicity + "</param>" +
	        			 "<param column=\"sex_cd\" name=\"sex\">" + sex + "</param>" +
	        			 "</patient>";
          
                System.out.println("3 " + patients.get(0).getVals().get("3"));
                System.out.println("3 " + row.getVals().get("3"));
                
                String suffix = "";
                
                if (row.getVals().get("3") != null && !row.getVals().get("3").equals("NULL"))
                {
                	
                if (row.getVals().get("3").equals("1")) {suffix = "NO";}
                if (row.getVals().get("3").equals("2")) {suffix = "YES";} 
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("3") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                
                if (row.getVals().get("4") != null && !row.getVals().get("4").equals("NULL"))
                {
                	                
                if (row.getVals().get("4").equals("1")) {suffix = "NO";}
                if (row.getVals().get("4").equals("2")) {suffix = "YES";} 
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("4") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                if (row.getVals().get("5") != null && !row.getVals().get("5").equals("NULL"))
                {                	       
                	suffix = "0";	
                                
                if (isNumeric(row.getVals().get("5")))
                {
                	suffix = row.getVals().get("5");                	         	
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("5") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                if (row.getVals().get("7") != null && !row.getVals().get("7").equals("NULL"))
                {                	       
                	suffix = "0";	
                                
                if (isNumeric(row.getVals().get("7")))
                {
                	suffix = row.getVals().get("7");                	         	
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("7") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                if (row.getVals().get("8") != null && !row.getVals().get("8").equals("NULL"))
                {                	       
                	suffix = "0";	
                                
                if (isNumeric(row.getVals().get("8")))
                {
                	suffix = row.getVals().get("8");                	         	
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("8") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                if (row.getVals().get("9") != null && !row.getVals().get("9").equals("NULL"))
                {
                	                
                if (row.getVals().get("9").equals("1")) {suffix = "NO";}
                if (row.getVals().get("9").equals("2")) {suffix = "YES";} 
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("9") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                if (row.getVals().get("10") != null && !row.getVals().get("10").equals("NULL"))
                {                	       
                	suffix = "0";	
                                
                if (isNumeric(row.getVals().get("10")))
                {
                	int val = Integer.parseInt(row.getVals().get("10"));    
                	
                	/*if (val >=0 && val <= 9) { suffix = "Smoking history.Years smoked.0-9."; }
                	if (val >=10 && val <= 19) { suffix = "Smoking history.Years smoked.10-19."; }
                	if (val >=20 && val <= 29) { suffix = "Smoking history.Years smoked.20-29."; }
                	if (val >=30 && val <= 39) { suffix = "Smoking history.Years smoked.30-39."; }
                	if (val >=40 && val <= 49) { suffix = "Smoking history.Years smoked.40-49."; }
                	if (val >=50 && val <= 59) { suffix = "Smoking history.Years smoked.50-59."; }
                	if (val >=60 && val <= 69) { suffix = "Smoking history.Years smoked.60-69."; }
                	if (val >=70) { suffix = "Smoking history.Years smoked.70-100."; }*/
                	
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("10") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 11 CBO:YearsAlcoholConsumption:
                
                if (row.getVals().get("11") != null && !row.getVals().get("11").equals("NULL"))
                {
                	                
                if (isNumeric(row.getVals().get("11")))
                {
                     	suffix = row.getVals().get("11");                	         	
                }
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("11") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 13 CBO:BeerWine:
                
                if (row.getVals().get("13") != null && !row.getVals().get("13").equals("NULL"))
                {
                	                
                if (row.getVals().get("13").equals("1")) {suffix = "NO";}
                if (row.getVals().get("13").equals("2")) {suffix = "YES";} 
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("13") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 14 CBO:Spirits:
                
                if (row.getVals().get("14") != null && !row.getVals().get("14").equals("NULL"))
                {
                	                
                if (row.getVals().get("14").equals("1")) {suffix = "NO";}
                if (row.getVals().get("14").equals("2")) {suffix = "YES";} 
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("14") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 19 CBO:LesionType:
                
                if (row.getVals().get("19") != null && !row.getVals().get("19").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("19")))
                {
                	if (row.getVals().get("19").equals("1")) { suffix = "Control"; }         
                	if (row.getVals().get("19").equals("2")) { suffix = "Leukoplakia"; } 
                	if (row.getVals().get("19").equals("3")) { suffix = "Erythroplakia"; } 
                	if (row.getVals().get("19").equals("4")) { suffix = "Candidal"; } 
                	if (row.getVals().get("19").equals("5")) { suffix = "Cancer"; } 
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("19") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 20 CBO:LesionAppearance:
                
                if (row.getVals().get("20") != null && !row.getVals().get("20").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("20")))
                {
                	if (row.getVals().get("20").equals("1")) { suffix = "Homogenous"; }         
                	if (row.getVals().get("20").equals("2")) { suffix = "Heterogenous"; } 
                	if (row.getVals().get("20").equals("3")) { suffix = "Normal"; } 
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("20") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 20 Biopsy.Biopsy site
                
                if (row.getVals().get("21") != null && !row.getVals().get("21").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("21")))
                {
                	if (row.getVals().get("21").equals("1")) { suffix = "C00.3"; }         
                	if (row.getVals().get("21").equals("2")) { suffix = "C00.4"; } 
                	if (row.getVals().get("21").equals("3")) { suffix = "C06.0"; } 
                	if (row.getVals().get("21").equals("4")) { suffix = "C06.1"; }         
                	if (row.getVals().get("21").equals("5")) { suffix = "C06.2"; } 
                	if (row.getVals().get("21").equals("6")) { suffix = "C03.0"; } 
                	if (row.getVals().get("21").equals("7")) { suffix = "C03.1"; }         
                	if (row.getVals().get("21").equals("8")) { suffix = "C04.0"; } 
                	if (row.getVals().get("21").equals("9")) { suffix = "C04.1"; } 
                	if (row.getVals().get("21").equals("10")) { suffix = "C04.8"; }         
                	if (row.getVals().get("21").equals("11")) { suffix = "C05.0"; } 
                	if (row.getVals().get("21").equals("12")) { suffix = "C02.0"; } 
                	if (row.getVals().get("21").equals("13")) { suffix = "C02.1"; }         
                	if (row.getVals().get("21").equals("14")) { suffix = "C02.2"; } 
                	if (row.getVals().get("21").equals("15")) { suffix = "C02.8"; } 
                	if (row.getVals().get("21").equals("16")) { suffix = "C02.3"; }         
                	if (row.getVals().get("21").equals("17")) { suffix = "C06.8"; } 
                	if (row.getVals().get("21").equals("18")) { suffix = "C02.4"; } 
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("21") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                // 22 CBO:DysplasiaDifferentiation:
                
                if (row.getVals().get("23") != null && !row.getVals().get("23").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("23")))
                {
                	if (row.getVals().get("23").equals("1")) { suffix = "Mild"; }         
                	if (row.getVals().get("23").equals("2")) { suffix = "Moderate"; } 
                	if (row.getVals().get("23").equals("3")) { suffix = "Severe"; }     
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("23") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //23 CBO:ClinicalTNM:
                
                if (row.getVals().get("24") != null && !row.getVals().get("24").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("24")))
                {
                	if (row.getVals().get("24").equals("1")) { suffix = "Tis"; }         
                	if (row.getVals().get("24").equals("2")) { suffix = "T0"; } 
                	if (row.getVals().get("24").equals("3")) { suffix = "TX"; } 
                	if (row.getVals().get("24").equals("4")) { suffix = "T1"; }         
                	if (row.getVals().get("24").equals("5")) { suffix = "T2"; } 
                	if (row.getVals().get("24").equals("6")) { suffix = "T3"; } 
                	if (row.getVals().get("24").equals("7")) { suffix = "T4"; }  
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("24") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //24 CBO:ClinicalTNM:
                
                if (row.getVals().get("25") != null && !row.getVals().get("25").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("25")))
                {
                	if (row.getVals().get("25").equals("1")) { suffix = "N0"; }         
                	if (row.getVals().get("25").equals("2")) { suffix = "N1"; } 
                	if (row.getVals().get("25").equals("3")) { suffix = "N2a"; } 
                	if (row.getVals().get("25").equals("4")) { suffix = "N2b"; }         
                	if (row.getVals().get("25").equals("5")) { suffix = "N2c"; } 
                	if (row.getVals().get("25").equals("6")) { suffix = "N3"; }                 	  
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("25") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //25 CBO:ClinicalTNM:
                
                if (row.getVals().get("26") != null && !row.getVals().get("26").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("26")))
                {
                	if (row.getVals().get("26").equals("1")) { suffix = "M0"; }         
                	if (row.getVals().get("26").equals("2")) { suffix = "M1"; }                 		  
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("26") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //26 CBO:PathologicaTNM:T
                
                if (row.getVals().get("27") != null && !row.getVals().get("27").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("27")))
                {
                	if (row.getVals().get("27").equals("1")) { suffix = "Tis"; }         
                	if (row.getVals().get("27").equals("2")) { suffix = "T0"; } 
                	if (row.getVals().get("27").equals("3")) { suffix = "TX"; } 
                	if (row.getVals().get("27").equals("4")) { suffix = "T1"; }         
                	if (row.getVals().get("27").equals("5")) { suffix = "T2"; } 
                	if (row.getVals().get("27").equals("6")) { suffix = "T3"; } 
                	if (row.getVals().get("27").equals("7")) { suffix = "T4"; }  
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("27") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //27 CBO:PathologicaTNM:N
                
                if (row.getVals().get("28") != null && !row.getVals().get("28").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("28")))
                {
                	if (row.getVals().get("28").equals("1")) { suffix = "N0"; }         
                	if (row.getVals().get("28").equals("2")) { suffix = "N1"; } 
                	if (row.getVals().get("28").equals("3")) { suffix = "N2a"; } 
                	if (row.getVals().get("28").equals("4")) { suffix = "N2b"; }         
                	if (row.getVals().get("28").equals("5")) { suffix = "N2c"; } 
                	if (row.getVals().get("28").equals("6")) { suffix = "N3"; }       
                	if (row.getVals().get("28").equals("7")) { suffix = "Nx"; } 
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("28") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //28 CBO:PathologicaTNM:M
                
                if (row.getVals().get("29") != null && !row.getVals().get("29").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("29")))
                {
                	if (row.getVals().get("29").equals("1")) { suffix = "M0"; }         
                	if (row.getVals().get("29").equals("2")) { suffix = "M1"; } 
                	if (row.getVals().get("29").equals("3")) { suffix = "Mx"; } 
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("29") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
                //29 CBO:CancerDifferentiation:
                
                if (row.getVals().get("30") != null && !row.getVals().get("30").equals("NULL"))
                {                	       
                	suffix = "";	
                                
                if (isNumeric(row.getVals().get("30")))
                {
                	if (row.getVals().get("30").equals("1")) { suffix = "Well"; }         
                	if (row.getVals().get("30").equals("2")) { suffix = "Moderate"; } 
                	if (row.getVals().get("30").equals("3")) { suffix = "Poor"; } 
                }
               
                
                obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
 	        			 "<event_id source=\"BRICCS\">" + row.getVals().get("1")  + "_" + currentdate + "</event_id>" +
 	        			 "<patient_id source=\"BRICCS\">" + row.getVals().get("1") + "BG</patient_id>" +
 	        			 "<concept_cd name=\"BRICCS\">"+ patients.get(0).getVals().get("30") + suffix + "</concept_cd>" +
 	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
 	        			 "<start_date>" + currentdate + "</start_date>" +
 	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
 	        			 "<valuetype_cd>T</valuetype_cd>" +
 	        			 "<units_cd>@</units_cd>" +
 	        			 "<end_date>" + currentdate + "</end_date>" +
 	        			 "<location_cd name=\"missing value\">@</location_cd>" +
 	        			 "</observation>";
                
                }
                
          
	            
	            
		   }
		}// FOR
		
		String pdo =   xml1 +
   			 event +
   			 xml2 +
   			 pid +
   			 xml3 +
   			 eid +
   			 xml4 +
   			 patient +
   			 xml5 +
   			 obs +
   			 xml6;
		
		try{


		    // Create file
		    FileWriter fstream = new FileWriter("/home/si84/workspace/birmingham/src/pdo/birmpdo.xml");
		    BufferedWriter out = new BufferedWriter(fstream);
		    //insert your xml content here
		    out.write(pdo);
		    out.close();


		}catch (Exception e){
		    System.err.println("Error: " + e.getMessage());
		}finally
		{
		    //Close the output stream

		}


	     System.out.println(pdo);	
	     performCall(pdo);

	     event = "";
     pid  = "";
     eid = "";
     patient = "";
     obs = "";

     pdo = "";
     

     
		
		//System.out.println("obs " + obs);
		//System.out.println("Size c = " +c);
	}
	
	
	public static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
	
	public void test()
	{

		/*
		DataSelect db = new DataSelect();
		try {
			db.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

		Table table;

		Map<String,PatientObs> aPatientObs = new HashMap<String,PatientObs>();


		try {
			table = Database.open(new File("/host/1hacklaptops/hack1/unzipped/SampleData.mdb")).getTable("AnnualSample");

			int count = 0;

			//yyyy-MM-dd'T'HH:mm:s




			for(Map<String, Object> row : table) {
			      count = count + 1;

			      //if (count > 2000) { break; }


			      String nhsno = (String) row.get("NHSNO");
				  String d1 = (String) row.get("DIAG");
				  String d2 = (String) row.get("DIAG2");
				  String d3 = (String) row.get("DIAG3");
				  String d4 = (String) row.get("DIAG4");
				  String d5 = (String) row.get("DIAG5");
				  String d6 = (String) row.get("DIAG6");
				  Date enddate = (Date) row.get("HPSPLL_ED");
				  Date startdate = (Date) row.get("HPSPLL_SD");
				  int age1 = 0;
				  if ( row.get("AGE") != null) { age1 = (Integer) row.get("AGE"); }
				  int sex1 = 0;
				  if ( row.get("SEX") != null) { sex1 = (Integer) row.get("SEX"); };


				  //System.out.println("Column 'a' has value: " + d1);
				  //System.out.println("Column 'a' has value: " + d2);
				  //System.out.println("Column 'a' has value: " + d3);
				  //System.out.println("Column 'a' has value: " + d4);
				  //System.out.println("Column 'a' has value: " + d5);
				  //System.out.println("Column 'a' has value: " + d6);

				  // Patient, object of fact



				  PatientObs current;

				  if (nhsno != null)
				  {
					  if ( aPatientObs.containsKey(nhsno) == false)
					  {
						  current = new PatientObs();
						  current.setAge(age1);
						  if (sex1 == 1) { current.setSex("MALE"); }
						  if (sex1 == 2) { current.setSex("FEMALE"); };
						  if (sex1 == 0) { current.setSex("UNKNOWN"); };

						  String dt = "2013-03-03T00:00:00.000+01:00";
						  current.setToday(dt);
						  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s");
						  Calendar c = Calendar.getInstance();
						  c.setTime(sdf.parse(dt));
						  c.add(Calendar.DATE, -(age1*365));  // number of days to add
						  dt = sdf.format(c.getTime());


						  current.setDob(dt + "0.000+01:00");



					  }
					  else
					  {
						  current =  aPatientObs.get(nhsno);
						  current.setAge(age1);
						  if (sex1 == 1) { current.setSex("MALE"); }
						  if (sex1 == 2) { current.setSex("FEMALE"); };
						  if (sex1 == 0) { current.setSex("UNKNOWN"); };

						  String dt = "2013-03-03T00:00:00.000+01:00";
						  current.setToday(dt);
						  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s");
						  Calendar c = Calendar.getInstance();
						  c.setTime(sdf.parse(dt));
						  c.add(Calendar.DATE, -(age1*365));  // number of days to add
						  dt = sdf.format(c.getTime());


						  current.setDob(dt + "0.000+01:00");

					  }

					  if ( d1 != null && !d1.equals(""))
					  {
						  current.setCode(d1,startdate,enddate);
					  }

					  if (d2 != null && !d2.equals(""))
					  {
						  current.setCode(d2,startdate,enddate);
					  }

					  if (d3 != null && !d3.equals(""))
					  {
						  current.setCode(d3,startdate,enddate);
					  }

					  if (d4 != null && !d4.equals(""))
					  {
						  current.setCode(d4,startdate,enddate);
					  }

					  if (d5 != null && !d5.equals(""))
					  {
						  current.setCode(d5,startdate,enddate);
					  }

					  if (d6 != null && !d6.equals(""))
					  {
						  current.setCode(d6,startdate,enddate);
					  }

					  aPatientObs.put(nhsno, current);
				  }


								}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





		  String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
   			 "<pdo:patient_data xmlns:pdo=\"http://www.i2b2.org/xsd/hive/pdo/1.1/pdo\">" +
   			 "<pdo:event_set>";

		  String xml2 = "</pdo:event_set>" +
     			 "<pdo:pid_set>";

		  String xml3 = "</pdo:pid_set>" +
     			 "<pdo:eid_set>";

		  String xml4 = "</pdo:eid_set>" +
     			 "<pdo:patient_set>";

		  String xml5 = "</pdo:patient_set><pdo:observation_set>";

		  String xml6 = "</pdo:observation_set></pdo:patient_data>";


		  Iterator it = aPatientObs.keySet().iterator();

		  int allp = 0;

		  int pdo_count = 0;


		  String event = "";
		  String pid  = "";
		  String eid = "";
		  String patient = "";
		  String obs = "";

		  String pdo = "";

		  //List all_pdo = new ArrayList();

		  while (it.hasNext()) {
		      String key = (String) it.next();
		      PatientObs value = (PatientObs) aPatientObs.get(key);
		      allp++;

		      //System.out.println(allp + " " + key + " " + value.getToday() + " " + " " + value.getDob() + " " + " " + value.getSex() + " " + value.getCode());

		      //do stuff here
		 	  //String currentdate = "2012-06-26T09:12:02+00:00";
		 	  String currentdate = value.getToday(); // 2013-03-03T00:00:00.000+01:00

		 	  //String brisskitid = "bru2-111111111";
		 	  String brisskitid = key; // nmnp3ukd

			  //String event_id = "bru2-111111111_2012-06-26T09:12:02+00:00";
			  String event_id = key + "_" + currentdate; // leading zeros

			  String birthdate = value.getDob();

			  int age = value.getAge();
			  String ethnicity = "UNKNOWN";
			  String sex = value.getSex();

			  String obscode = "CIV:CSex:Male";  // ICD10:B70  // HES:A15-A19




	         // xml1

            event += "<event download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
	        			 "<event_id source=\"BRICCS\">" + event_id + "</event_id>" +
	        			 "<patient_id source=\"BRICCS\">" + brisskitid + "</patient_id>" +
	        			 "<param column=\"ACTIVE_STATUS_CD\" name=\"active status\">F</param>" +
	        			 "<param column=\"INOUT_CD\" name=\"\">@</param>" +
	        			 "<param column=\"LOCATION_CD\" name=\"\">@</param>" +
	        			 "<param column=\"LOCATION_PATH\" name=\"\">@</param>" +
	        			 "<start_date>" + currentdate + "</start_date>" +
	        			 "<end_date>@</end_date>" +
	        			 "</event>";

           // xml2

            pid += "<pid>" +
	        			 "<patient_id download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" source=\"BRICCS\" status=\"Active\" update_date=\"" + currentdate + "\" upload_id=\"1\">" + brisskitid + "</patient_id>" +
	        			 "</pid>";

           // xml3

            eid += "<eid>" +
	        			 "<event_id download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" source=\"BRICCS\" sourcesystem_cd=\"BRICCS\" status=\"Active\" update_date=\"" + currentdate + "\" upload_id=\"1\">" + event_id + "</event_id>" +
	        			 "</eid>";

           // xml4

           patient += "<patient download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
	        			 "<patient_id source=\"BRICCS\">" + brisskitid + "</patient_id>" +
	        			 "<param column=\"vital_status_cd\" name=\"date interpretation code\">N</param>" +
	        			 "<param column=\"birth_date\" name=\"birthdate\">" + birthdate + "</param>" +
	        			 "<param column=\"age_in_years_num\" name=\"age\">" + age + "</param>" +
	        			 "<param column=\"race_cd\" name=\"ethnicity\">" + ethnicity + "</param>" +
	        			 "<param column=\"sex_cd\" name=\"sex\">" + sex + "</param>" +
	        			 "</patient>";

           // xml5


			             ArrayList<PatientObs.Codes> all = value.getCode();
			             if (all != null)
			             {
			            	 Iterator iterator = all.iterator();
			                 int j=0;
			                 for (; iterator.hasNext();) {
			                   PatientObs.Codes c1 = (PatientObs.Codes) iterator.next();

			                   String c1_c = c1.getC();
			                   String c1_s = c1.getSt();
			                   String c1_e = c1.getEnd();

			                   //System.out.println( c1_s.length() );
			                   //System.out.println( c1_s );
			                   //System.out.println( c1_e.length() );
			                   //System.out.println( c1_e );

			                   //System.out.println( c1_c.length() );
			                   //System.out.println( c1_c );
			                   c1_c = "ICD10:" + c1_c.substring(0, 3);

			                   obs += "<observation download_date=\"" + currentdate + "\" import_date=\"" + currentdate + "\" sourcesystem_cd=\"BRICCS\" update_date=\"" + currentdate + "\" upload_id=\"1\">" +
			  	        			 "<event_id source=\"BRICCS\">" + event_id + "</event_id>" +
			  	        			 "<patient_id source=\"BRICCS\">" + brisskitid + "</patient_id>" +
			  	        			 "<concept_cd name=\"BRICCS\">"+c1_c+"</concept_cd>" +
			  	        			 "<observer_cd source=\"BRICCS\">@</observer_cd>" +
			  	        			 "<start_date>" + c1_s + "</start_date>" +
			  	        			 "<modifier_cd name=\"missing value\">@</modifier_cd>" +
			  	        			 "<valuetype_cd>T</valuetype_cd>" +
			  	        			 "<units_cd>@</units_cd>" +
			  	        			 "<end_date>" + c1_e + "</end_date>" +
			  	        			 "<location_cd name=\"missing value\">@</location_cd>" +
			  	        			 "</observation>";
			                 }
			             }


			             if (allp % 5000 == 0 || it.hasNext() == false)
			             {
			            	 System.out.println(allp);

			            	 pdo =   xml1 +
			            			 event +
			            			 xml2 +
			            			 pid +
			            			 xml3 +
			            			 eid +
			            			 xml4 +
			            			 patient +
			            			 xml5 +
			            			 obs +
			            			 xml6;

			            	 //all_pdo.add(pdo);


			            	 try{


			            		    // Create file
			            		    FileWriter fstream = new FileWriter("/home/localadmin1/workspace/birmingham/src/pdo/pdo"+pdo_count+".xml");
			            		    BufferedWriter out = new BufferedWriter(fstream);
			            		    //insert your xml content here
			            		    out.write(pdo);
			            		    out.close();

			            		    pdo_count++;

			            		}catch (Exception e){
			            		    System.err.println("Error: " + e.getMessage());
			            		}finally
			            		{
			            		    //Close the output stream

			            		}


			            	 //System.out.println(pdo);

			       		     event = "";
			    		     pid  = "";
			    		     eid = "";
			    		     patient = "";
			    		     obs = "";

			    		     pdo = "";

			             }


			             //xml1 = xml1 + obs;

                         //xml1 = xml1 + "</pdo:observation_set></pdo:patient_data>";



//                         performCall(xml);


			 /*
			  *
			  *
			  *
			  <?xml version="1.0" encoding="UTF-8"?>
<pdo:patient_data xmlns:pdo="http://www.i2b2.org/xsd/hive/pdo/1.1/pdo">
<pdo:event_set>
<event download_date="2012-08-28T16:57:02+00:00" import_date="2012-08-28T16:57:02+00:00" sourcesystem_cd="BRICCS" update_date="2012-08-28T16:57:02+00:00" upload_id="1">
<event_id source="BRICCS">bru3-445467309_2012-08-28T16:57:02+00:00</event_id>
<patient_id source="BRICCS">bru3-445467309</patient_id>
<param column="ACTIVE_STATUS_CD" name="active status">F</param>
<param column="INOUT_CD" name="">@</param>
<param column="LOCATION_CD" name="">@</param>
<param column="LOCATION_PATH" name="">@</param>
<start_date>2012-08-28T16:57:02+00:00</start_date>
<end_date>@</end_date>
</event>
</pdo:event_set>
<pdo:pid_set>
<pid>
<patient_id download_date="2012-08-28T16:57:02+00:00" import_date="2012-08-28T16:57:02+00:00" sourcesystem_cd="BRICCS" source="BRICCS" status="Active" update_date="2012-08-28T16:57:02+00:00" upload_id="1">bru3-445467309</patient_id>
</pid>
</pdo:pid_set>
<pdo:eid_set>
<eid>
<event_id download_date="2012-08-28T16:57:02+00:00" import_date="2012-08-28T16:57:02+00:00" source="BRICCS" sourcesystem_cd="BRICCS" status="Active" update_date="2012-08-28T16:57:02+00:00" upload_id="1">bru3-445467309_2012-08-28T16:57:02+00:00</event_id>
</eid>
</pdo:eid_set>
<pdo:patient_set>
<patient download_date="2012-08-28T16:57:02+00:00" import_date="2012-08-28T16:57:02+00:00" sourcesystem_cd="BRICCS" update_date="2012-08-28T16:57:02+00:00" upload_id="1">
<patient_id source="BRICCS">bru3-445467309</patient_id>
<param column="vital_status_cd" name="date interpretation code">N</param>
<param column="birth_date" name="birthdate">1980-08-18T00:00:00.000+01:00</param>
<param column="age_in_years_num" name="age">32</param>
<param column="race_cd" name="ethnicity">Male</param>
<param column="sex_cd" name="sex">White</param>
</patient>
</pdo:patient_set>
<pdo:observation_set>
<observation download_date="2012-09-11T16:21:18.722+01:00" import_date="2012-09-11T16:21:18.722+01:00" sourcesystem_cd="BRICCS" update_date="2012-09-11T16:21:18.722+01:00" upload_id="1">
<event_id source="BRICCS">bru3-445467309_2012-08-28T16:57:02+00:00</event_id>
<patient_id source="BRICCS">bru3-445467309</patient_id>
<concept_cd name="BRICCS">CIV:CAge</concept_cd>
<observer_cd source="BRICCS">@</observer_cd>
<start_date>2012-04-07T00:44:26.600+01:00</start_date>
<modifier_cd name="missing value">@</modifier_cd>
<valuetype_cd>N</valuetype_cd>
<tval_char>E</tval_char>
<nval_num units="years">32</nval_num>
<valueflag_cd name="Low">L</valueflag_cd>
<quantity_num>@</quantity_num>
<units_cd>years</units_cd>
<end_date>@</end_date>
<location_cd name="missing value">@</location_cd>
</observation>
</pdo:observation_set>
</pdo:patient_data>


			  *
			  *
			  */



		    // System.out.println(it.next());

		     //create pdo
		     //pass to webservice


		  } // end of while

/*
		   Iterator iterator = all_pdo.iterator();
	       int j=0;
	       for (; iterator.hasNext();) {
	         System.out.printf("%d squared is %d.\n",
	                            j++, iterator.next());
	       }

*/







		//getConnection()
	}

    private static URI getBaseURI() {
        //return UriBuilder.fromUri("http://localhost:8080/i2b2WS/rest").build();
        return UriBuilder.fromUri("http://hack3.brisskit.le.ac.uk:8080/i2b2WS/rest").build();
        //return UriBuilder.fromUri("http://i2b2:8080/i2b2WS/rest").build();

    }

	public String performCall(String xml)
	{
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());



        Form form = new Form();
        form.add( "incomingXML", xml );
        form.add( "activity_id", "941" );

        //ClientResponse response = service.path("service").path("pdo").type(MediaType.APPLICATION_XML).post(ClientResponse.class, xml);
        ClientResponse response = service.path("service").path("pdo").type(MediaType.APPLICATION_XML).post(ClientResponse.class, form);

 		System.out.println("performCall Output from Server .... \n");
 		String output = response.getEntity(String.class);
 		System.out.println(output);

 		return output;

	}

	public void createExcel()
	{
	        HSSFWorkbook workbook = new HSSFWorkbook();

	        //
	        // Create two sheets in the excel document and name it First Sheet and
	        // Second Sheet.
	        //
	        HSSFSheet firstSheet = workbook.createSheet("FIRST SHEET");
	        HSSFSheet secondSheet = workbook.createSheet("SECOND SHEET");

	        //
	        // Manipulate the firs sheet by creating an HSSFRow wich represent a
	        // single row in excel sheet, the first row started from 0 index. After
	        // the row is created we create a HSSFCell in this first cell of the row
	        // and set the cell value with an instance of HSSFRichTextString
	        // containing the words FIRST SHEET.
	        //
	        HSSFRow rowA = firstSheet.createRow(0);
	        HSSFCell cellA = rowA.createCell(0);
	        cellA.setCellValue(new HSSFRichTextString("FIRST SHEET"));

	        HSSFRow rowB = secondSheet.createRow(0);
	        HSSFCell cellB = rowB.createCell(0);
	        cellB.setCellValue(new HSSFRichTextString("SECOND SHEET"));

	        //
	        // To write out the workbook into a file we need to create an output
	        // stream where the workbook content will be written to.
	        //

	        /*
	        FileOutputStream fos = null;
	        try {
	            fos = new FileOutputStream(new File("CreateExcelDemo.xls"));
	            workbook.write(fos);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (fos != null) {
	                try {
	                    fos.flush();
	                    fos.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        */
	}

	public static void main(String args[])
	{
		PDOGenerator pdogen = new PDOGenerator();
		pdogen.getExcel();
		pdogen.createPDO();
		//pdogen.test();

	}
	
	
	/*
	 * 
	 * val = Integer.parseInt(row.getVals().get("5"));
                	
                	if (val >=0 && val <= 9) { prefix = "Smoking history.Years smoked.0-9."; }
                	if (val >=10 && val <= 19) { prefix = "Smoking history.Years smoked.10-19."; }
                	if (val >=20 && val <= 29) { prefix = "Smoking history.Years smoked.20-29."; }
                	if (val >=30 && val <= 39) { prefix = "Smoking history.Years smoked.30-39."; }
                	if (val >=40 && val <= 49) { prefix = "Smoking history.Years smoked.40-49."; }
                	if (val >=50 && val <= 59) { prefix = "Smoking history.Years smoked.50-59."; }
                	if (val >=60 && val <= 69) { prefix = "Smoking history.Years smoked.60-69."; }
                	if (val >=70) { prefix = "Smoking history.Years smoked.70-100."; }
	 */
}
