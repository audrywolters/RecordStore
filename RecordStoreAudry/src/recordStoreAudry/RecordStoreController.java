package recordStoreAudry;

import java.sql.Timestamp;
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
	private static int staffId;
	private static int loginId; 
	//store all the DB data for program to work with
	//this way we don't need access the DB as much
	private static LinkedList<Record> allRecords = new LinkedList<Record>();
	private static LinkedList<Consigner> allConsigners = new LinkedList<Consigner>();
	private static LinkedList<Payment> allPayments = new LinkedList<Payment>();
	private static LinkedList<Staff> allStaff = new LinkedList<Staff>();
	private static LinkedList<Login> allLogins = new LinkedList<Login>();
	//save the id of who is logged in
	private static int userLoggedIn = 0;
	//barign bin duration
	private static int bbDaysOld;
	private static double bbPrice;
	private static double consignerPercent;

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
		
		controller.updateConfig();

		// store all data in linked lists
		model.requestAllRecords();
		model.requestAllConsigners();
		model.requestAllPayments();
		model.requestAllStaff();
		model.requestAllLogins();


		// launch the menu, get the user's choice
		System.out.println("Welcome to Record Store Manager.");
		controller.login();


		// clean up
		model.cleanUp();
	}


	private void updateConfig() {
		//retrieve configuration data from DB
		Number[] configs = model.fetchConfigurations();
		
		//parse the data
		int daysOld = (Integer) configs[0];
		double price = (Double) configs[1];
		double percent = (Double) configs[2];
		
		//set the data
		RecordStoreController.setBbDaysOld(daysOld);
		RecordStoreController.setBbPrice(price);
		RecordStoreController.setConsignerPercent(percent);
	}


	///CHECK USER CREDENTIALS///
	public void login() {
		boolean managerStatus = false;
		boolean usernameMatch = false;
		boolean usernameInvalid = true;
		boolean passwordInvalid = true;

		System.out.println("\nHi. Enter your login name and password:");
		//check username
		while(usernameInvalid) {
			//get username
			String entryUsername = view.getEntryUsername();

			//check all staff for matching username
			for (Staff staff : allStaff) {
				//if match
				if (entryUsername.equals(staff.getUsername())) {
					//if manager set to manager
					if (staff.isManager()) {
						managerStatus = true;
					}
					//allow password loop to begin
					usernameMatch = true;
					//stop loop
					usernameInvalid = false;	
					break;
				}
			}			
		}	

		//check password
		//begin loop only if the correct username has been found
		if (usernameMatch) {			
			while (passwordInvalid) {
				//get password
				String entryPassword = view.getEntryPassword();
				//check all staff for match
				for (Staff staff : allStaff) {
					//if match
					if (entryPassword.equals(staff.getPassword())) {
						//get the current date/time
						java.util.Date date= new java.util.Date();
						Timestamp now = new Timestamp(date.getTime());
						//save who is logged in in the controller
						setUserLoggedIn(staff.getId());
						//add the new login to storage
						Login login = new Login(staff.getId(), now);
						int loginId = generateLoginId();
						login.setId(loginId);
						allLogins.add(login);
						passwordInvalid = false;
						break;
					}
				}
			}

		}

		//search inventory for old records
		System.out.println("\nNow Searching for Old Inventory.");
		//tell check if old what the manager status is
		checkIfOld(managerStatus);


	}


	///LOGOUT///
	public void logoutAndLogin() {
		logoutUser();	

		//run login screen
		login();
	}



	public void logoutUser() {
		for (Login l : allLogins) {
			if (getUserLoggedIn() == l.getStaffId()) {
				//get current time/date
				java.util.Date date= new java.util.Date();
				Timestamp now = new Timestamp(date.getTime());
				//and set it
				l.setTimeOut(now);
				//update login in database
				model.updateLogin(l);
				break;
			}
		}		

		System.out.println("Logged Out.");
	}




	public void generateReport() {
		int unsoldRecords = 0;
		for (Record record : allRecords) {
			if (!record.isSold()) {
				unsoldRecords++;
			}
		}
		System.out.println("There are a total of " + unsoldRecords + " unsold records in the inventory.");

		printAllRecords();

	}




	public void addUserRecord() {
		//check if too many copies
		String[] titleArtist = view.getTitleArtist();

		//check to see if there are other copies
		boolean tooMany = tooManyCopies(titleArtist[0], titleArtist[1]);
		if(tooMany) {
			System.out.println("Sorry, there are too many copies of " + titleArtist[0]);

		} else {
			//print prices
			LinkedList<Double> commonPrices = findCommonPrice(titleArtist[0], titleArtist[1]);
			System.out.println("Average Price = " + commonPrices.get(0));
			System.out.println("Lowest Price = " + commonPrices.get(1));
			System.out.println("Highest Price = " + commonPrices.get(2));

			//get the user to set a price
			double price = view.getPrice();
			
			//get the consigner id from user
			int consignerId = view.getIdFromUser("Consigner");

			//date is now
			Calendar dateAdded = new GregorianCalendar();

			//get whomever is logged in
			int checkedInBy = RecordStoreController.getUserLoggedIn();

			//create record
			Record record = new Record(titleArtist[0], titleArtist[1], consignerId, price, checkedInBy, dateAdded);

			//send to DB
			requestAddRecord(record);

		}

	}


	///ADD NEW RECORD////
	public void requestAddRecord(Record record) {
		//boolean sent from model to see if record was added
		boolean successful = model.addUserRecord(record);
		//if the record was added, update in allRecords
		if(successful) {
			allRecords.add(record);
			//tick the id
			int tempId = generateRecordId();
			record.setId(tempId);
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

		if (copies >= 4) {
			return true;
		} else {
			return false;
		}


	}


	///FIND COMMON PRICES OF RECORD///
	public LinkedList<Double> findCommonPrice(String title, String artist) {
		LinkedList<Double> commonPrices = model.fetchCommonPrice(title, artist);
		return commonPrices;

	}


	///PRINT ALL RECORDS///
	public void printAllRecords() {
		for (Record r : allRecords) {
			System.out.println(r);
		}
	}


	///UPDATE SOLD RECORD///
	public void sellRecord() {
		//find the right record
		Record record = searchForRecord();
		//if found
		if (record!= null) {
			//prep the record
			record.setSold(true);
			record.setPriceSold(record.getPrice());
			Calendar now = GregorianCalendar.getInstance();
			//don't parse here, parse in sql
			record.setDateSold(now);
			//get the logged in staff id
			int soldBy = userLoggedIn;
			record.setSoldBy(soldBy);

			//prep payment
			int payId = generatePaymentId();
			int recId = record.getId();
			int consignId = record.getConsignerId();
			//consigner receives half of the total price
			double amountDue = record.getPrice() * consignerPercent;
			boolean outstanding = true;
			Payment payment = new Payment(recId, consignId, outstanding);
			payment.setId(payId);
			payment.setAmountDue(amountDue);

			//call database to update recored
			boolean successful = model.updateSoldRecord(record);
			if (successful) {
				//if it was successful add payment to db
				successful = model.addPayment(payment);
				if (successful) {
					//if successful add to linked list
					allPayments.add(payment);
				}

			}
		}
	}



	///SEARCH FOR A RECORD///
	public Record searchForRecord() {
		String userResponse = "y";

		int idFromUser = view.getIdFromUser("record");

		//search through all records 
		for (Record record : allRecords) {
			//to find one that matches the user's id entry
			if (idFromUser == record.getId()) {
				//get confirmation that it's the correct record
				userResponse = view.isThisYourRecord(record);

				if (userResponse.equalsIgnoreCase("y")) {
					return record;
				} 				
			}

			if (record.getId() == allRecords.size() || userResponse.equalsIgnoreCase("n")) {
				//if the loop completes and hasn't found anything, print sorry message 
				System.out.println("Sorry. No matches found.");	
				break;
			}
		}
		return null;
	}



	///DELETE A RECORD///
	public void deleteRecord(Record record) {
		model.deleteRecord(record);
	}



	///CHECK IF BARGIN BIN / CHARITY ///
	public static void checkIfOld(boolean managerStatus) {
		//get 30 days ago
		Calendar numbOfDaysAgo = new GregorianCalendar();
		numbOfDaysAgo.add(Calendar.DAY_OF_MONTH, -bbDaysOld);
		//get one year ago
		Calendar oneYearAgo = new GregorianCalendar();
		oneYearAgo.add(Calendar.YEAR, -1);

		//keep track of id in linked list of record to be deleted 
		LinkedList<Integer> deleteRecordsIds = new LinkedList<Integer>();

		for (Record r : allRecords) {
			//compare dateAdded to one year ago 
			if (r.getDateAdded().before(oneYearAgo) ) {  
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
				//compare date added to inventory and 30 days ago, and check if it's already in the bargin bin
			} else if (r.getDateAdded().before(numbOfDaysAgo) && !r.isBarginBin()) {  //and not in bargin bin
				boolean successful;
				//if older ask user what to do with it
				int userChoice = view.moveRecordOrCall(r, "Bargin Bin");

				if (userChoice == 1) {
					//move record to bargin bin and update in inventory
					r.setBarginBin(true);
					r.setPrice(bbPrice);
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

		//now run the menu and tell menu what the manager status is
		view.runMenu(managerStatus);
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
			int id = generateConsignerId();
			//set id in the consigner object
			consigner.setId(id);
			//add to the LinkedList Storage
			allConsigners.add(consigner);
			System.out.println(consigner);
		}


	}



	///SEARCH FOR A CONSIGNER///
	public Consigner searchForConsigner() {
		int idFromUser = view.getIdFromUser("consigner");
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



	///FIND CONSIGNER'S RECORDS///
	public LinkedList<Record> findConsingerRecords(Consigner c) {
		//new linked list to hold matching records
		LinkedList<Record> consignerRecords = new LinkedList<Record>();
		//get the consigner's id
		int consignerId = c.getId();

		//search for matching ids in stored records
		for (Record r : allRecords) {
			if (consignerId == r.getConsignerId()) {
				consignerRecords.add(r);
			}
		}

		return consignerRecords;
	}




	///SEARCH FOR A PAYMENT///
	public Payment searchForPayment() {
		int idFromUser = view.getIdFromUser("payment");

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

			if (payment.getId() == allPayments.size()) {
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
	public void updatePayment(Payment payment) {
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
		payment.setPaymentMadeBy(userLoggedIn);

		//call the DB to update itself
		boolean successful = model.updatePayment(payment);
		//let user know status of update
		if(successful) {
			allPayments.add(payment);
		} else {
			System.out.println ("***Update payment through Main Menu***");
		}

	}


	public void printAllPayments() {
		for (Payment p : allPayments) {
			System.out.println(p);
		}
	}


	///CONFIGURE BARGIN BIN AND PAYMENT RATIO///
	public void configurePolicies() {
		int userChoice = view.configureMenu();
		
		switch (userChoice) {

		case 1: { //bb duration
			int daysOld = view.getDaysOld();
			model.updateBbDaysOld(daysOld);
			RecordStoreController.setBbDaysOld(-daysOld);
			break;
			
		} case 2: { //bb price
			double price = view.getUserBbPrice();
			model.updateBbPrice(price);
			RecordStoreController.setBbPrice(price);
			break;
			
		} case 3: { //consigner percent
			double percent = view.getUserPercent();
			model.updateConsignerPercent(percent);
			RecordStoreController.setConsignerPercent(percent);
			break;
		}
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

	public void addToAllStaff(Staff s) {
		allStaff.add(s);
	}

	public void addToAllLogins(Login l) {
		allLogins.add(l);
	}



	public void quit() {
		logoutUser();
		model.cleanUp();
		System.exit(0);
	}


	//this keeps track of ids in the java storage - matches the DB id
	public int generateRecordId() {
		recordId++;
		return recordId;
	}

	public int generateConsignerId() {
		consignerId++;
		return consignerId;
	}

	public int generatePaymentId() {
		paymentId++;
		return paymentId;
	}

	public int generateStaffId() {
		staffId++;
		return staffId;

	}

	public int generateLoginId() {
		loginId++;
		return loginId;
	}


	public static int getUserLoggedIn() {
		return userLoggedIn;
	}


	public static void setUserLoggedIn(int usrLgdIn) {
		userLoggedIn = usrLgdIn;
	}


	public static int getBbDaysOld() {
		return bbDaysOld;
	}


	public static void setBbDaysOld(int BbDaysOld) {
		bbDaysOld = BbDaysOld;
	}


	public static double getBbPrice() {
		return bbPrice;
	}


	public static void setBbPrice(double bbPrice) {
		RecordStoreController.bbPrice = bbPrice;
	}


	public static double getConsignerPercent() {
		return consignerPercent;
	}


	public static void setConsignerPercent(double consignerPercent) {
		RecordStoreController.consignerPercent = consignerPercent;
	}  





}
