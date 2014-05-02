package recordStoreAudry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Record {
	private int id;
	private String title;
	private String artist;
	private int consignerId;
	private Calendar dateAdded;
	private Calendar dateSold;
	private double price;
	private double priceSold;
	private boolean barginBin;
	private boolean sold;

	// constructor
	public Record(String titl, String artst, double pric, int consgnId, Calendar dateAdd) {
		//this.id = id;
		this.title = titl;
		this.artist = artst;
		this.price = pric;
		this.consignerId = consgnId;
		this.dateAdded = dateAdd;
		this.barginBin = false;
		this.sold = false;
	}

	// to string
	

	// get and set
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		//parse Calendars for printing
		String formatDateSold;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formatDateAdded = sdf.format(dateAdded.getTime());
		if (dateSold == null) {
			 formatDateSold = "Not yet Sold";
		} else {
			//print date sold
			 formatDateSold = sdf.format(dateSold.getTime());
		}
		return "Record [id=" + id + ", title=" + title + ", artist=" + artist
				+ ", consignerId=" + consignerId + ", dateAdded=" + formatDateAdded
				+ ", dateSold=" + formatDateSold + ", price=" + price
				+ ", priceSold=" + priceSold + ", barginBin=" + barginBin
				+ ", sold=" + sold + "]";
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

}
