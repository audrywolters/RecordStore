package recordStoreAudry;

import java.util.Calendar;

public class Login {
	private int id;
	private int staffId;
	private Calendar timeIn;
	private Calendar timeOut;
	
	
	//construct
	public Login(Calendar timeIn, Calendar timeOut) {
		this.timeIn = timeIn;
		this.timeOut = timeOut;
	}

	//get and set
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public int getStaffId() {
		return staffId;
	}
	

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public Calendar getTimeIn() {
		return timeIn;
	}


	public void setTimeIn(Calendar timeIn) {
		this.timeIn = timeIn;
	}


	public Calendar getTimeOut() {
		return timeOut;
	}


	public void setTimeOut(Calendar timeOut) {
		this.timeOut = timeOut;
	}
	
	
	
	
}
