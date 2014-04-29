package recordStoreAudry;

import java.sql.*;

public class Record {
	private int id;
	private String title;
	private String artist;
	private int consignerId;
	private Date dateAdded;
	private Date dateSold;
	private double price;
	private double priceSold;
	private boolean barginBin;
	private boolean sold;

	// constructor
	public Record(/* int d, */String ttl, String rtst, /* int cnsgnrD, Date dtDdd, */
			double prc) {
		// this.id = d;
		this.title = ttl;
		this.artist = rtst;
		// this.consignerId = cnsgnrD;
		// this.dateAdded = dtDdd;
		this.price = prc;
	}

	// to string
	public String toString() {
		return "Record [id=" + id + ", title=" + title + ", artist=" + artist
				+ ", consignerId=" + consignerId + ", dateAdded=" + dateAdded
				+ ", dateSold=" + dateSold + ", price=" + price
				+ ", priceSold=" + priceSold + ", barginBin=" + barginBin
				+ ", sold=" + sold + "]";
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

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Date getDateSold() {
		return dateSold;
	}

	public void setDateSold(Date dateSold) {
		this.dateSold = dateSold;
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
