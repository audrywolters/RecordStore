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

		//check if records need to be moved
		checkIfOld();




		// launch the menu, get the user's choice
		view.runMenu();

		// clean up
		model.cleanUp();
	}



	///ADD NEW RECORD////
	public void requestAddRecord(Record record) {
		//boolean sent from model to see if record was added
		boolean successful = model.addUserRecord(record);
		//if the record was added, update in allRecords
		if(successful) {
			allRecords.add(record);
			setRecordId();
			int tempId = getRecordId();
			record.setId(tempId);
			//debug
			System.out.println("New Id: " + tempId);
		}
	}



	///CHECK IF THERE ARE TOO MANY COPIES OF RECORD///
	public boolean tooManyCopies(String title, String artist) {
		int copies = 0;

		//search all records
		for (Record r : allRecords) {
			//compare new title & artist entry to what's in inventory
			if (title.equalsIgnoreCase(r.getTitle()) && 
					(artist.equalsIgnoreCase(r.getArtist()))) {
				//if they are the same tick copies
				copies++;
			}
		}

		if (copies >= 2) {
			return true;
		} else {
			return false;
		}


	}

	
	///PRINT ALL RECORDS///
	public void printAllRecords() {
		for (Record r : allRecords) {
			System.out.println(r);
		}
	}


	///UPDATE SOLD RECORD///
	public void requestUpdateSoldRecord() {
		//prep record
		Record record = searchForRecord();
		double priceSold = view.getPriceFromUser();
		record.setSold(true);
		record.setPriceSold(priceSold);
		Calendar now = GregorianCalendar.getInstance();
		//don't parse here, parse in sql
		record.setDateSold(now);

		//prep payment
		setPaymentId();
		int payId = getPaymentId();
		int recId = record.getId();
		int consignId = record.getConsignerId();
		double amountDue = record.getPriceSold() * .5 ;
		boolean outstanding = true;
		Payment payment = new Payment(recId, consignId, outstanding);
		payment.setId(payId);
		payment.setAmountDue(amountDue);

		//call database to update
		model.updateSoldRecord(record);
		boolean successful = model.addPayment(payment);
		if (successful) {
			allPayments.add(payment);
		}


	}



	///SEARCH FOR A RECORD///
	public Record searchForRecord() {
		int idFromUser = view.getIdFromUser();

		System.out.println("OUTSTANDING PAYMENTS");
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



	///DELETE A RECORD///
	public void deleteRecord(Record record) {
		model.deleteRecord(record);
	}



	///CHECK IF BARGIN BIN / CHARITY ///
	public static void checkIfOld() {
		//get 30 days before today
		Calendar thirtyDaysAgo = new GregorianCalendar();
		thirtyDaysAgo.add(Calendar.DAY_OF_MONTH, -30);
		//get one year ago
		Calendar oneYearAgo = new GregorianCalendar();
		oneYearAgo.add(Calendar.YEAR, -1);

		//keep track of id in linked list of record to be deleted 
		LinkedList<Integer> deleteRecordsIds = new LinkedList<Integer>();

		for (Record r : allRecords) {
			//compare dateAdded to one year ago
			if (r.getDateAdded().before(oneYearAgo)) {
				boolean successful;
				//if older ask user what to do with it
				int userChoice = view.moveRecordOrCall(r, "Charity");

				//delete either way
				if (userChoice == 1 || userChoice == 2) {
					//if consigner takes it, delete from inventory
					successful = model.deleteRecord(r);
					if(successful) {
						//copy linked list position for deleting after loop
						deleteRecordsIds.add(r.getId());
					} else {
						System.out.println("***Delete using main menu.***");
					}
				}
				//compare date added to inventory and 30 days ago, 
			} else if (r.getDateAdded().before(thirtyDaysAgo)) { 
				boolean successful;
				//if older ask user what to do with it
				int userChoice = view.moveRecordOrCall(r, "Bargin Bin");

				if (userChoice == 1) {
					//move record to bargin bin and update in inventory
					successful = model.updateInventoryStatus(r);
					if(successful) {
						r.setBarginBin(true);
					} else {
						System.out.println("***Update using main menu.***");
					}
				} else if (userChoice == 2) {
					//if consigner takes it, delete from inventory
					successful = model.deleteRecord(r);
					if(successful) {
						//copy linked list position for deleting after loop
						deleteRecordsIds.add(r.getId());
					} else {
						System.out.println("***Delete using main menu.***");
					}
				}


			}
		}


		//for every stored id of a record that was deleted from DB
		for (Integer dri : deleteRecordsIds) {
			//loop through the allRecords for a matching id
			for (Record r : allRecords) {
				//if the id's match (id's of deleted records & stored records)
				if (dri == r.getId()) {
					//remove from allRecords
					allRecords.remove(r);
					break;
				}
			}
		}
	}


	///PRINT ALL CONSIGNERS///
	public void printAllConsigners() {
		for (Consigner c : allConsigners) {
			System.out.println(c);
		}
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



	///SEARCH FOR A CONSIGNER///
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



	///CALC MONEY OWED FOR CONSIGNER///
	public double calcMoneyOwed(Consigner c) {
		double totalOwed = 0;

		//loop through payments
		for (Payment p : allPayments) {
			//if ids match
			if (c.getId() == p.getConsignerId()) {
				//check if money is owed
				if (p.getOutstanding() == true) {
					totalOwed = totalOwed + p.getAmountDue();
				}
			}
		}

		if (totalOwed >= 0) {
			return totalOwed;
		} else {
			return -3.33;
		}




	}




	///SEARCH FOR A PAYMENT///
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


	///VIEW OUTSTANDING PAYMENTS///
	public void findOutsandingPayments() {
		for (Payment p : allPayments) {		
			if (p.getOutstanding() == true) {
				System.out.println(p);
			}
		}
	}


	///UPDATE PAYMENT///
	public void updatePaymentCont(Payment payment) {
		//prep variables
		Calendar dateMade = new GregorianCalendar();
		double amountDue = 0;
		double amountPaid = payment.getAmountDue();
		boolean outstanding = false;
		//set varaibles
		payment.setDateMade(dateMade);
		payment.setAmountDue(amountDue);
		payment.setAmountPaid(amountPaid);
		payment.setOutstanding(outstanding);

		//call the DB to update itself
		boolean successful = model.updatePayment(payment);
		//let user know status of update
		if(successful) {
			allPayments.add(payment);
		} else {
			System.out.println ("***Update payment through Main Menu***");
		}

	}

	///ADD A NEW RECORD TO STORAGE///
	public void addToAllRecords(Record r) {
		allRecords.add(r);

	}
	public void addToAllConsigners(Consigner c) {
		allConsigners.add(c);

	}
	public void addToAllPayments(Payment p) {
		allPayments.add(p);
	}



	//TODO rename setRecordId() - not a setter
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
