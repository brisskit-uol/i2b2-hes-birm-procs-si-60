package london;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PatientObs {
   String id ;
   ArrayList<Codes> code = new ArrayList<Codes>();
   int age;
   String sex;
   String dob;
   String today;
   
   
 public String getDob() {
	return dob;
}

public void setDob(String dob) {
	this.dob = dob;
}

public String getToday() {
	return today;
}

public void setToday(String today) {
	this.today = today;
}

public String getSex() {
	return sex;
}

public void setSex(String sex) {
	this.sex = sex;
}

public int getAge() {
	return age;
}

public void setAge(int age) {
	this.age = age;
}


public PatientObs() {
	   
}

public PatientObs(String id) {
	super();
	this.id = id;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public ArrayList getCode() {
	return code;
}

public void setCode(String c1, Date startdate, Date enddate) {
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s");
	  Calendar c = Calendar.getInstance();
	  String sstart = sdf.format(startdate);
	  sstart = sstart + "0.000+01:00";
	  
	  if (enddate == null) {enddate = new Date();}
	  
	  SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s");
	  Calendar c2 = Calendar.getInstance();
	  String eend = sdf2.format(enddate);
	  eend = eend + "0.000+01:00";
	  
	  
    code.add(new Codes(c1,sstart,eend));
}
   
  public class Codes
  {
	  String c;
	  String st;
	  String end;
	  
	public Codes(String c, String st, String end) {
		super();
		this.c = c;
		this.st = st;
		this.end = end;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	  
  }


}
