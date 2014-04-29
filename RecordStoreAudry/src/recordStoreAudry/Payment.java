package recordStoreAudry;

import java.sql.*;

public class Payment {
	private int id;
	private int recordId;
	private int consignerId;
	private boolean outstanding;
	private double amountDue;
	private double amountPaid;
	private Date dateMade;

	// constructor
	public Payment(int d, int rcrdD, int cnsgnrD, boolean tstndng) {
		this.id = d;
		this.recordId = rcrdD;
		this.consignerId = cnsgnrD;
		this.outstanding = tstndng;
	}

	// to string
	public String toString() {
		return "Payment [id=" + id + ", dateMade=" + dateMade + ", recordId="
				+ recordId + ", consignerId=" + consignerId + ", amountDue="
				+ amountDue + ", amountPaid=" + amountPaid + ", outstanding="
				+ outstanding + "]";
	}

	// get and set
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateMade() {
		return dateMade;
	}

	public void setDateMade(Date dateMade) {
		this.dateMade = dateMade;
	}

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public int getConsignerId() {
		return consignerId;
	}

	public void setConsignerId(int consignerId) {
		this.consignerId = consignerId;
	}

	public double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public boolean isOutstanding() {
		return outstanding;
	}

	public void setOutstanding(boolean outstanding) {
		this.outstanding = outstanding;
	}

}
