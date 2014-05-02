package recordStoreAudry;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class RecordStoreController {

	private static RecordStoreModel model;
	private static RecordStoreView view;
	//ids kept track of here
	private static int recordId;
	private static int consignerId;
	private static int paymentId;
	//store all the DB data for program to work with
	//this way we don't need access the DB as much
	private static LinkedList<Record> allRecords = new LinkedList<Record>();
	private static LinkedList<Consigner> allConsigners = new LinkedList<Consigner>();
	private static LinkedList<Payment> allPayments = new LinkedList<Payment>();

	public static void main(String args[]) {
		RecordStoreController controller = new RecordStoreController();
		model = new RecordStoreModel(controller);
		view = new RecordStoreView(controller);

		// connect to db
		model.createConnection();

		// make tables
		model.createTables();

		// insert data
		model.addTestData();

		// store all data in linked lists
		model.requestAllRecords();
		model.requestAllConsigners();
		model.requestAllPayments();

		// launch the menu, get the user's choice
		view.runMenu();

		// clean up
		model.cleanUp();
	}


	///ADD NEW RECORD////
	public void requestAddRecord(Record record) {
		boolean successful = model.addUserRecord(record);

		if(successful) {
			allRecords.add(record);
			setRecordId();
			int tempId = getRecordId();
			record.setId(tempId);
			//debug
			System.out.println("New Id: " + tempId);
		}
	}


	///UPDATE SOLD RECORD///
	public void requestUpdateSoldRecord() {

		Record record = searchForRecord();
		double priceSold = view.getPriceFromUser();
		record.setSold(true);
		record.setPriceSold(priceSold);
		//prep data
		
		Calendar now = GregorianCalendar.getInstance();
		//don't parse here, parse in sql
		//java.sql.Date dateSold = new java.sql.Date(today.getTime());
		record.setDateSold(now);
		//call database to update
		model.updateSoldRecord(record);

	}


	///SEARCH FOR RECORD///
	public Record searchForRecord() {
		int idFromUser = view.getIdFromUser();

		//search through all records 
		for (Record record : allRecords) {
			//to find one that matches the user's id entry
			if (idFromUser == record.getId()) {
				//get confirmation that it's the correct record
				String userResponse = view.isThisYourRecord(record);

				if (userResponse.equalsIgnoreCase("y")) {
					return record;
				} 				
			}

			if (record.getId() == allRecords.size()) {
				//if the loop completes and hasn't found anything, print sorry message 
				System.out.println("Sorry. No matches found.");					
			}
		}
		return null;
	}


	
	public void deleteRecord(Record record) {
		model.deleteRecord(record);
	}
	
	
	
	///ADD CONSIGNER///
	public void addConsigner(Consigner consigner) {
		//call the model to insert data to DB
		boolean successful = model.addConsigner(consigner);
		//if insert was successful
		if (successful) {
			//tick the id counter
			setConsignerId();
			//get id
			int id = getConsignerId();
			//set id in the consigner object
			consigner.setId(id);
			//add to the LinkedList Storage
			allConsigners.add(consigner);
			System.out.println(consigner);
		}
		
		
	}
	
	
	public Consigner searchForConsigner() {
		int idFromUser = view.getIdFromUser();

		//search through all records 
		for (Consigner consigner : allConsigners) {
			//to find one that matches the user's id entry
			if (idFromUser == consigner.getId()) {
				//get confirmation that it's the correct record
				String userResponse = view.isThisYourConsigner(consigner);

				if (userResponse.equalsIgnoreCase("y")) {
					return consigner;
				} 				
			}

			if (consigner.getId() == allConsigners.size()) {
				//if the loop completes and hasn't found anything, print sorry message 
				System.out.println("Sorry. No matches found.");					
			}
		}
		return null;
	}


	public Payment searchForPayment() {
		int idFromUser = view.getIdFromUser();

		//search through all records 
		for (Payment payment : allPayments) {
			//to find one that matches the user's id entry
			if (idFromUser == payment.getId()) {
				//get confirmation that it's the correct record
				String userResponse = view.isThisYourPayment(payment);

				if (userResponse.equalsIgnoreCase("y")) {
					return payment;
				} 				
			}

			if (payment.getId() == allConsigners.size()) {
				//if the loop completes and hasn't found anything, print sorry message 
				System.out.println("Sorry. No matches found.");					
			}
		}
		return null;
	}


	///SEARCH FOR OLD COPIES///	
	public Record searchForOldCopies() {
		return null;
	}


	//add a new record to storage
	public void addToAllRecords(Record r) {
		allRecords.add(r);

	}
	public void addToAllConsigners(Consigner c) {
		allConsigners.add(c);

	}
	public void addToAllPayments(Payment p) {
		allPayments.add(p);
	}


	//setters overridden to keep track of ids
	//every time an item is created in the DB, 
	//the id will tick here (as it does in the DB)
	public void setRecordId() {
		recordId++;
	}
	public int getRecordId() {
		return recordId;
	}

	public void setConsignerId() {
		consignerId++;
	}
	public int getConsignerId() {
		return consignerId;
	}

	public void setPaymentId() {
		paymentId++;
	}
	public int getPaymentId() {
		return paymentId;
	}

	//get the storage
	public LinkedList<Record> getAllRecords() {
		return allRecords;
	}	
	public LinkedList<Consigner> getAllConsigners() {
		return allConsigners;
	}
	public LinkedList<Payment> getAllPayments() {
		return allPayments;
	}





	


	





}
