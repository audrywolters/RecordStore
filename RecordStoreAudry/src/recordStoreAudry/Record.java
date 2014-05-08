package recordStoreAudry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Record {
	private int id;
	private String title;
	private String artist;
	private int consignerId;
	private double price;
	private boolean sold;
	private int checkedInBy;
	private Calendar dateAdded;
	private double priceSold;
	private int soldBy;
	private Calendar dateSold;
	private boolean barginBin;


	// constructor
	public Record(String titl, String artst, int consgnId, double pric, int checkdInBy, Calendar dateAdd) {
		//this.id = id;
		this.title = titl;
		this.artist = artst;
		this.consignerId = consgnId;
		this.price = pric;
		this.sold = false;
		this.checkedInBy = checkdInBy;
		this.dateAdded = dateAdd;
		this.barginBin = false;

	}
	

	// to string
	@Override
	public String toString() {
		//parse Calendars for printing
		String formatDateSold;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formatDateAdded = sdf.format(dateAdded.getTime());
		if (sold == false) {
			 formatDateSold = "Not yet Sold";
			 
			 
		} else {
			//print date sold
			 formatDateSold = sdf.format(dateSold.getTime());
		}
		return  id + ". " + title + ", " + artist  + ", Consigner #" + consignerId + ", Price $" + price + "\n"
				+ "---Checked In By #" + checkedInBy  + " on " + formatDateAdded  + ", In Bargin Bin: " + barginBin + "\n"
				+ "---Has been sold: " + sold + ", Sold on " + formatDateSold  + ", Sold for $" + priceSold +  ", Sold By #" + soldBy + "\n";
				 
				
	}

	

	// get and set
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getConsignerId() {
		return consignerId;
	}

	public void setConsignerId(int consignerId) {
		this.consignerId = consignerId;
	}

	public Calendar getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Calendar dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Calendar getDateSold() {
		return dateSold;
	}

	public void setDateSold(Calendar now) {
		this.dateSold = now;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPriceSold() {
		return priceSold;
	}

	public void setPriceSold(double priceSold) {
		this.priceSold = priceSold;
	}

	public boolean isBarginBin() {
		return barginBin;
	}

	public void setBarginBin(boolean barginBin) {
		this.barginBin = barginBin;
	}

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}
	
	public int getCheckedInBy() {
		return checkedInBy;
	}

	public void setCheckedInBy(int checkedInBy) {
		this.checkedInBy = checkedInBy;
	}

	public int getSoldBy() {
		return soldBy;
	}

	public void setSoldBy(int soldBy) {
		this.soldBy = soldBy;
	}

}
