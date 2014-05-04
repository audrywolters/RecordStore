package recordStoreAudry;


public class Consigner {
	private int id;
	private String name;
	private String phone;
	//private double moneyOwed;

	// constructor
	public Consigner(String nm, String phn) {
		//this.id = d;
		this.name = nm;
		this.phone = phn;
	}

	// to string
	public String toString() {
		return "Consigner [id=" + id + ", name=" + name + ", phone=" + phone + "]";
	}

	// get and set
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/*
	public double getMoneyOwed() {
		return moneyOwed;
	}

	public void setMoneyOwed(double moneyOwed) {
		this.moneyOwed = moneyOwed;
	}
	*/

}
