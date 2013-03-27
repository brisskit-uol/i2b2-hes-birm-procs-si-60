package london;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.ParseException;
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

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFCell;
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

public class Ontology {

	Set<PatientObs> unique = new TreeSet<PatientObs>();

	public void getExcel()
	{
		File file = new File("/host/1hacklaptops/hack1/unzipped/data.xls");
		try {
			FileInputStream in = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(in);
			ExcelExtractor extractor = new ExcelExtractor(workbook);
			String text = extractor.getText();
			System.out.println(text);

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

                // Iterate over each cell in the row and print out the cell's content
                Iterator cells = row.cellIterator();
                while( cells.hasNext() ) {
                    HSSFCell cell = (HSSFCell) cells.next();
                    System.out.println( "Cell #" + cell.getCellNum() );
                    switch ( cell.getCellType() ) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            System.out.println( cell.getNumericCellValue() );
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            System.out.println( cell.getStringCellValue() );
                            break;
                        default:
                            System.out.println( "unsuported sell type" );
                            break;
                    }
                }

            }

        } catch ( IOException ex ) {
            ex.printStackTrace();
        }


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

						  String dt = "2012-10-10T00:00:00.000+01:00";
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

						  String dt = "2012-10-10T00:00:00.000+01:00";
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
		 	  String currentdate = value.getToday(); // 2012-10-10T00:00:00.000+01:00

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



                        // performCall(xml);


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
		Ontology ontology = new Ontology();
		//ontology.getExcel();
		ontology.test();

	}
}
