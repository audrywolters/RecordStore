package recordStoreAudry;

import java.sql.Timestamp;

public class Login {
	private int id;
	private int staffId;
	private Timestamp timeIn;
	private Timestamp timeOut;
	
	
	//construct
	public Login(Timestamp timeIn) {
		this.timeIn = timeIn;
		//this.timeOut = timeOut;
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

	public Timestamp getTimeIn() {
		return timeIn;
	}


	public void setTimeIn(Timestamp timeIn) {
		this.timeIn = timeIn;
	}


	public Timestamp getTimeOut() {
		return timeOut;
	}


	public void setTimeOut(Timestamp timeOut) {
		this.timeOut = timeOut;
	}
	
	
	
	
}
