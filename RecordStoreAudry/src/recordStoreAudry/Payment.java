package recordStoreAudry;

import java.util.Calendar;

public class Payment {
	private int id;
	private int recordId;
	private int consignerId;
	private boolean outstanding;
	private double amountDue;
	private double amountPaid;
	private Calendar dateMade;

	// constructor
	public Payment(/*int d, */int recrdId, int consgnrId, boolean outstndng) {
		//this.id = d;
		this.recordId = recrdId;
		this.consignerId = consgnrId;
		this.outstanding = outstndng;
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

	public Calendar getDateMade() {
		return dateMade;
	}

	public void setDateMade(Calendar dateMade) {
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

	public boolean getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(boolean outstanding) {
		this.outstanding = outstanding;
	}

}
